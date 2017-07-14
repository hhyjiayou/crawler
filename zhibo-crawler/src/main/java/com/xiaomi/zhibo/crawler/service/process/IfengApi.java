package com.xiaomi.zhibo.crawler.service.process;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.PostBarType;
import com.xiaomi.zhibo.crawler.util.CrawlerUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : zhongxiankui
 * @time : 2016-08,19 10:37:上午10:37
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-crawler
 */

@Service
public class IfengApi extends PostBarApi {
    
    
    private static final Logger logger = LoggerFactory.getLogger(IfengApi.class);
    
    private static final String IFENG_COMMENTS = "comments";
    private static final String IFING_CONTENT = "comment_contents";
    private static final String IFENG_COMMENT_NEW_CALLBACK = "newCommentListCallBack";
    private static final String IFENG_COMMENT_HOT_CALLBACK = "hotCommentListCallBack";
    private static final String IFENG_COMMENT_API = "http://comment.ifeng.com/get.php?format=js&job=1&p=%d&" +
            "pageSize=%d&skey=7f7b8b&orderby=";
    private static final String IFENG_COMMENT_URL_DOC_URL = "&doc_url=";
    private static final String IFENG_COMMENT_API_DOC_URL = "docUrl";
    private static final String IFENG_COMMENT_API_SPLIT = "&";
    private static final String IFENG_COMMENT_TOTAL_COUNT = "count";
    
    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {
    
        String afterStr = IFENG_COMMENT_URL_DOC_URL;
        int startIndex = url.indexOf(IFENG_COMMENT_API_DOC_URL);
        if(0 > startIndex){
            logger.error("can't find need data! start:{}", startIndex);
            return null;
        }
        String paramTmp1 = url.substring(startIndex);
        int end = paramTmp1.indexOf(IFENG_COMMENT_API_SPLIT);
        if(0 > end){
            logger.error("can't find need data! end:{}", end);
            return null;
        }
        
        String param = paramTmp1.substring(7, end);
        StringBuffer ifengUrl = new StringBuffer();
        String pageParam = String.format(IFENG_COMMENT_API, start, limit);
        ifengUrl.append(pageParam);
    
        //callback=newCommentListCallBack&docUrl=http%3A%2F%2F2016.ifeng.com%2Fa%2F20160816%2F49784592_0.shtml
        if(PostBarType.NEW == postBarType){ //最新评论
            ifengUrl.append("&callback=");
            ifengUrl.append(IFENG_COMMENT_NEW_CALLBACK);
        }else if(PostBarType.HOT == postBarType){
            ifengUrl.append("uptimes&callback=");
            ifengUrl.append(IFENG_COMMENT_HOT_CALLBACK);
        }
    
        afterStr += param;
        ifengUrl.append(afterStr);
        
        return ifengUrl.toString();
    }
    
    @Override
    public String preTrim(String data, PostBarType postBarType) {
    
        if(null == data || data.isEmpty() || null == postBarType){
            logger.error("empty data:{} or postBarType:{}", data, postBarType);
            return null;
        }
        
        int start = data.indexOf("=");
        int end = start;
        
        if(PostBarType.NEW == postBarType){
            end = data.lastIndexOf(IFENG_COMMENT_NEW_CALLBACK);
        }
        else if(postBarType.HOT == postBarType){
            end= data.lastIndexOf(IFENG_COMMENT_HOT_CALLBACK);
        }
        logger.debug("start:{},end:{}", start, end);
        
        String dataTrim = data.substring(start + 1, end - 2);
        logger.debug("dataTrim:{}", dataTrim);
        
        return dataTrim;
    }
    
    @Override
    public JSONObject getPostBarJson(String url, String data, PostBarType postBarType, int limit, int start) {
    
        JSONObject postBarJson = new JSONObject();
        if(null == data || data.isEmpty() || null == postBarType){
            logger.error("empty data:{} or postBarType:{}", data, postBarType);
            return postBarJson;
        }
        
        try{
            JSONObject jsonObject = new JSONObject(data);
            if(!jsonObject.has(IFENG_COMMENTS)){
                logger.error("no need key:{}, json:{}", IFENG_COMMENTS, jsonObject.toString());
                return postBarJson;
            }
            JSONArray jsonArray = (JSONArray)jsonObject.get(IFENG_COMMENTS);
            int size = jsonArray.length();

            //排序
            //对jsonArray按发布时间进行排序
            List<JSONObject> jsonList = new ArrayList<JSONObject>();
            for(int i=0; i<size; i++){
                JSONObject value= (JSONObject) jsonArray.get(i);
                jsonList.add(value);

            }
            Collections.sort(jsonList, new CrawlerUtil.SortByIfeng());

            size = jsonList.size();
            JSONArray contentJsonArray = new JSONArray();
            for (int i = 0; i < size; i++){
                JSONObject postBarJsonTmp = new JSONObject();
                JSONObject jsonUnit = jsonList.get(i);
                String content1 = jsonUnit.getString(IFING_CONTENT);
                String content = content1.replaceAll("<br>", "");
                postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                postBarJsonTmp.put(Constant.POSTBAR_CONTENT, content);
                contentJsonArray.put(postBarJsonTmp);
            }
            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);
            if(jsonObject.has(IFENG_COMMENT_TOTAL_COUNT)){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObject.getInt(IFENG_COMMENT_TOTAL_COUNT));
            }
            
        }catch (Exception e){
            logger.error("e:{}", e);
        }
        
        logger.debug("postBarJson:{}", postBarJson.toString());
        return postBarJson;
    }
}
