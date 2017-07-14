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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hhy on 16-9-2.
 */

@Service
public class SohuApi extends PostBarApi{

    private static final Logger logger = LoggerFactory.getLogger(SohuApi.class);

    private static final String SOHU_HOTURL = "http://changyan.sohu.com/api/3/topic/liteload?client_id=cyqemw6s1&" +
            "page_size=20&hot_size=5&topic_source_id=%s";;
    private static final String SOHU_NEWURL_PREFIX = "http://changyan.sohu.com/api/2/topic/comments?client_id=cyqemw6s1" +
            "&page_size=%d&topic_id=%d&page_no=%d";
    private static final String SOHU_DIVNAME = "div[id=SOHUCS]";
    private static final String SOHU_DIVATTR = "sid";
    private static final String SOHU_RESULT = "result";
    private static final String HOT_JSON = "hots";
    private static final String NEW_JSON = "comments";
    private static final String CONTENT_JSON = "content";
    private static final String SOHU_API_COUNT = "cmt_cnt";


    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {


        try {

            Document doc = Jsoup.connect(url).get();
            Element newsHeadlines = doc.select(SOHU_DIVNAME).first();
            String topic_source_id = newsHeadlines.attr(SOHU_DIVATTR);
            String hot_url = String.format(SOHU_HOTURL, topic_source_id);
            logger.info("hotUrl:{}", hot_url);
            if(PostBarType.HOT == postBarType){
                return hot_url;
            }else{

                try {
                    String result = getRawData(hot_url);
                    JSONObject json = null;
                    json = new JSONObject(result);
                    int topic_id = (Integer) json.get("topic_id");
                    String new_url = String.format(SOHU_NEWURL_PREFIX, limit, topic_id, start);
                    return new_url;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String preTrim(String data, PostBarType postBarType) {

        if(null == data || data.isEmpty() || null == postBarType){
            logger.error("empty data:{} or postBarType:{}", data, postBarType);
            return null;
        }
        return data;
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
            JSONArray jsonArray = new JSONArray();

            if(PostBarType.HOT == postBarType && jsonObjectAll.has(HOT_JSON)){
                jsonArray = (JSONArray) jsonObjectAll.get(HOT_JSON);
            }else if(PostBarType.NEW == postBarType && jsonObjectAll.has(NEW_JSON)){
                jsonArray = (JSONArray) jsonObjectAll.get(NEW_JSON);
            }else{
                logger.error("empty postBarType:{}", postBarType);
                return postBarJson;
            }
            int size = jsonArray.length();
            //对jsonArray按发布时间进行排序
            List<JSONObject> jsonList = new ArrayList<JSONObject>();
            for(int i=0; i<size; i++){
                JSONObject value= (JSONObject) jsonArray.get(i);
                jsonList.add(value);

            }
            Collections.sort(jsonList, new CrawlerUtil.SortBySohu());

            size = jsonList.size();
            JSONArray contentJsonArray = new JSONArray();
            for(int i= 0; i<size; i++){
                JSONObject postBarJsonTmp = new JSONObject();
                JSONObject dataJson = (JSONObject) jsonList.get(i);
                postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                String contentTmp = (String) dataJson.get(CONTENT_JSON);
                String content = contentTmp.replaceAll("<br>", "");
                postBarJsonTmp.put(Constant.POSTBAR_CONTENT, content);
                contentJsonArray.put(postBarJsonTmp);
            }
            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);

            if(jsonObjectAll.has(SOHU_API_COUNT) ){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObjectAll.get(SOHU_API_COUNT));
            }
        }catch (JSONException e){
            logger.error("e:{}", e);
        }

        logger.debug("postBarJson:{}", postBarJson.toString());
        return postBarJson;
    }

}
