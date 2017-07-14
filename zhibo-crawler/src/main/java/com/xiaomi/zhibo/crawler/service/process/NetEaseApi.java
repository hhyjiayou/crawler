package com.xiaomi.zhibo.crawler.service.process;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.PostBarType;
import com.xiaomi.zhibo.crawler.util.CrawlerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author : zhongxiankui
 * @time : 2016-08,18 18:26:下午6:26
 * @mail : zhongxiankui@xiaomi.com
 * @project MiTest
 */

@Service
public class NetEaseApi extends PostBarApi{
    
    private static final Logger logger = LoggerFactory.getLogger(NetEaseApi.class);
    
    private static final String EASE_COMMENTS = "comments";
    private static final String EASE_CONTENT = "content";
    //private static final String EASE_COMMENTIDS = "commentIds";
    private static final String EASE_API_URL_1 = "/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads";
    private static final String EASE_API_URL_2 = "?offset=%d&limit=%d&showLevelThreshold=72&headLimit=1&tailLimit=2&" +
            "callback=getData&ibc=newspc&_=1471573687088";
    private static final String EASE_API_URL_NEW = "newList";
    private static final String EASE_API_URL_OLD = "hotList";
    private static final String EASE_API_TOTAL_COUNT = "newListSize";
    
    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {
    
        String afterStr = "/comments/";
        if(PostBarType.NEW == postBarType){ //最新评论
            afterStr += EASE_API_URL_NEW;
        }else if(PostBarType.HOT == postBarType){//热门评论
            afterStr += EASE_API_URL_OLD;
        }else {
            logger.error("unknown post Type! postBarType:{}, url:{}", postBarType, url);
        }
        afterStr += String.format(EASE_API_URL_2, start*limit, limit);
        
        String eastUrlTmp = url.substring(0, url.indexOf(".com") + 4);  //http://comment.2016.163.com
        String eastUrl = eastUrlTmp + EASE_API_URL_1;
        int end = url.indexOf(".html");
        String paramTmp = url.substring(0,end);
        int startIndex = paramTmp.lastIndexOf("/");
        String param = paramTmp.substring(startIndex);
    

        eastUrl += param + afterStr;
        
        return eastUrl;
    }
    
    @Override
    public String preTrim(String data, PostBarType postBarType) {
    
        if(null == data || data.isEmpty() || null == postBarType){
            logger.error("empty data:{} or postBarType:{}", data, postBarType);
            return null;
        }
        
        int start = data.indexOf("(");
        int end = data.lastIndexOf(")");
        
        if(0 > start || 0 > end){
            logger.error("can't find need data! start:{}, end:{}", start, end);
            return null;
        }
        
        logger.debug("start:{},end:{}", start, end);
        String dataTrim = data.substring(start + 1, end);
        
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
        
        try {
            
            JSONObject jsonObject = new JSONObject(data);
            if(!jsonObject.has(EASE_COMMENTS)){
                logger.error("no need key:{}, json:{}", EASE_COMMENTS, jsonObject.toString());
                return postBarJson;
            }
            JSONObject commentJson = jsonObject.getJSONObject(EASE_COMMENTS);
            Iterator it = commentJson.keys();

            //排序
            List<JSONObject> jsonList = new ArrayList<JSONObject>();
            while(it.hasNext()){
                String key = (String) it.next();
                JSONObject value = commentJson.getJSONObject(key);
                jsonList.add(value);
            }
            Collections.sort(jsonList, new CrawlerUtil.SortByEast());

            JSONArray contentJsonArray = new JSONArray();
            for(JSONObject value : jsonList){
                JSONObject postBarJsonTmp = new JSONObject();
                postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                String contentStr1 = (String) value.get(EASE_CONTENT);
                String contentStr = contentStr1.replaceAll("<br>", "");
                postBarJsonTmp.put(Constant.POSTBAR_CONTENT, contentStr);
                contentJsonArray.put(postBarJsonTmp);
                if(contentJsonArray.length() >= limit){
                    break;
                }
            }
            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);
            if(jsonObject.has(EASE_API_TOTAL_COUNT)){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObject.getInt(EASE_API_TOTAL_COUNT));
            }
            
        }catch (JSONException e){
            logger.error("e:{}", e);
        }

        return postBarJson;
    }
}
