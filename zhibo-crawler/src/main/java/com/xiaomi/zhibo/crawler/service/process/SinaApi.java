package com.xiaomi.zhibo.crawler.service.process;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.PostBarType;
import com.xiaomi.zhibo.crawler.util.CrawlerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @author : zhongxiankui
 * @time : 2016-08,19 10:37:上午10:37
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-crawler
 */

@Service
public class SinaApi extends PostBarApi{
    
    private static final Logger logger = LoggerFactory.getLogger(SinaApi.class);
    
    private static final String SINA_RESULT = "result";
    private static final String SINA_COMMENT_NEW = "cmntlist";
    private static final String SINA_COMMENT_HOT = "hot_list";
    private static final String SINA_COMMENT_UID = "uid";
    private static final String SINA_COMMENT_CONTENT = "content";
    private static final String SINA_COMMENT_API_PREFIX = "http://comment5.news.sina.com.cn/page/info?version=1&format=js&" +
            "page=%d&page_size=%d&group=%d&compress=0&ie=utf-8&oe=utf-8&";
    private static final String SINA_API_COUNT = "count";
    private static final String SINA_API_TOTAL = "total";
    
    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {
        int group = 0;
        if(url.contains("slide.")){
            group = 1;
        }
        String sinaUrl = String.format(SINA_COMMENT_API_PREFIX, start, limit, group);
        String channel = "";
        String newsId = "";

        if(url.contains("channel") && url.contains("newsid")){
            int newIdStart = url.indexOf("newsid");
            int channelStart = url.indexOf("channel");
            String newIdStr = url.substring(newIdStart);
            String channelStr = url.substring(channelStart);
            newsId = "newsid=" + newIdStr.split("=")[1].split("&")[0];
            channel = "channel=" +channelStr.split("=")[1].split("&")[0];
        }else{
            try {
                Document doc = Jsoup.connect(url).get();
                Elements newsHeadlines = doc.select("script");
                Iterator<Element> it = newsHeadlines.iterator();
                while (it.hasNext()) {
                    Element element = (Element) it.next();
                    String[] data = element.data().toString().split("var");

                    for (String variable : data) {
                        if (variable.contains("=")) {
                            if (variable.contains("newsid:") && variable.contains("channel:")) {
                                int newIdStart = variable.indexOf("newsid");
                                int channelStart = variable.indexOf("channel");
                                String newIdStr = variable.substring(newIdStart);
                                String channelStr = variable.substring(channelStart);
                                newsId = "newsid=" + newIdStr.split("\'")[1].split("\'")[0];
                                channel = "channel=" + channelStr.split("\'")[1].split("\'")[0];
                                sinaUrl += channel + "&" + newsId;
                                return sinaUrl;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        logger.info("channel:{}, newsId:{}", channel, newsId);
        sinaUrl += channel + "&" + newsId;
        return sinaUrl;
    }
    
    @Override
    public String preTrim(String data, PostBarType postBarType) {
        
        if(null == data || data.isEmpty() || null == postBarType){
            logger.error("empty data:{} or postBarType:{}", data, postBarType);
            return null;
        }
        
        int start = data.indexOf("{");
        int end = data.lastIndexOf("}");
        if(0 > start || 0 > end){
            logger.error("can't find need data! start:{}, end:{}", start, end);
            return null;
        }
        
        String dataTrim = data.substring(start, end+1);
//        logger.debug("dataTrim:{}", dataTrim);
        
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
            JSONObject jsonObjectAll = new JSONObject(data);
            JSONObject jsonObject = (JSONObject)jsonObjectAll.get(SINA_RESULT);
            JSONArray contentJsonArray = new JSONArray();
            JSONArray jsonArray = null;
            if(PostBarType.HOT == postBarType && jsonObject.has(SINA_COMMENT_HOT)){//热门评论
                jsonArray = (JSONArray)jsonObject.get(SINA_COMMENT_HOT);
            }else if(PostBarType.NEW == postBarType && jsonObject.has(SINA_COMMENT_NEW)){ //最新评论
                jsonArray = jsonObject.getJSONArray(SINA_COMMENT_NEW);
            }else {
                logger.error("unknown postBarType:{} or not need key json:{}",postBarType, jsonObject.toString());
                return postBarJson;
            }
            
            int size = jsonArray.length();

            //对jsonArray按发布时间进行排序
            List<JSONObject> jsonList = new ArrayList<JSONObject>();
            for(int i=0; i<size; i++){
                JSONObject value= (JSONObject) jsonArray.get(i);
                jsonList.add(value);

            }
            Collections.sort(jsonList, new CrawlerUtil.SortBySINA());

            size = jsonList.size();
            for (int i = 0; i < size; i++){
                JSONObject postBarJsonTmp = new JSONObject();
                JSONObject value= jsonList.get(i);
                postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                String content1 = (String) value.getString(SINA_COMMENT_CONTENT);
                String content = content1.replaceAll("<br>", "");
                postBarJsonTmp.put(Constant.POSTBAR_CONTENT, content);
                contentJsonArray.put(postBarJsonTmp);
            }
    
            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);
            if(jsonObject.has(SINA_API_COUNT) && jsonObject.getJSONObject(SINA_API_COUNT).has(SINA_API_TOTAL)){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObject.getJSONObject(SINA_API_COUNT).getInt(SINA_API_TOTAL));
            }
            
        }catch (JSONException e){
            logger.error("e:{}", e);
        }
    
        logger.debug("postBarJson:{}", postBarJson.toString());
        return postBarJson;
    }

}
