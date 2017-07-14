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
 * Created by hhy on 16-9-2.
 */
@Service
public class TouTiaoApi extends PostBarApi{
    private static final Logger logger = LoggerFactory.getLogger(TouTiaoApi.class);
    private static final String TOUTIAO_URLAPI_PREFIX = "http://toutiao.com/group/";
    private static final String TOUTIAO_URLAPI_SUFFIX = "/comments/?count=%d&page=%d&offset=%d&item_id=0&format=json";
    private static final String TOUTIAO_DATA = "data";
    private static final String TOUTIAO_COMMENTS = "comments";
    private static final String TOUTIAO_CONTENT = "text";
    private static final String TOUTIAO_API_COUNT = "comment_pagination";
    private static final String TOUTIAO_API_TOTAL = "total_count";


    public String getGroupId(String url){
        try {
            Document doc = null;
            doc = Jsoup.connect(url).get();
            Elements newsHeadlines = doc.select("script");
            Iterator<Element> it = newsHeadlines.iterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();
                String dataTmp = element.toString();
                if(dataTmp.contains("groupid")){
                    String[] data = element.data().toString().split(";");
                    for (String variable : data) {
                        if (variable.contains("groupid")) {
                            int groupIdStart = variable.indexOf("groupid");
                            String groupIdStrTmp = variable.substring(groupIdStart);
                            String groupId = groupIdStrTmp.split(":")[1].split("\'")[1];
                            return groupId;

                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("jsoup error:{}", e);
        }
        return null;
    }

    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {
        String groupId = getGroupId(url);

        if(null == groupId || groupId.isEmpty()){
            logger.error("empty groupId:{}", groupId);
            return null;
        }
        String TouTiaoUrl = TOUTIAO_URLAPI_PREFIX + groupId + String.format(TOUTIAO_URLAPI_SUFFIX, limit, start, (start-1)*limit);
        return TouTiaoUrl;
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
            JSONObject jsonObject = (JSONObject)jsonObjectAll.get(TOUTIAO_DATA);
            JSONArray contentJsonArray = new JSONArray();
            JSONArray jsonArray = null;
            if(jsonObject.has(TOUTIAO_COMMENTS)) {//热门评论
                jsonArray = (JSONArray) jsonObject.get(TOUTIAO_COMMENTS);
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
            Collections.sort(jsonList, new CrawlerUtil.SortByToutiao());

            size = jsonList.size();
            for (int i = 0; i < size; i++){
                JSONObject postBarJsonTmp = new JSONObject();
                JSONObject value= jsonList.get(i);
                postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                String content1 = (String) value.get(TOUTIAO_CONTENT);
                String content = content1.replaceAll("<br>", "");
                postBarJsonTmp.put(Constant.POSTBAR_CONTENT, content);
                contentJsonArray.put(postBarJsonTmp);
            }

            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);
            if(jsonObject.has(TOUTIAO_API_COUNT) && jsonObject.getJSONObject(TOUTIAO_API_COUNT).has(TOUTIAO_API_TOTAL)){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObject.getJSONObject(TOUTIAO_API_COUNT).getInt(TOUTIAO_API_TOTAL));
            }

        }catch (JSONException e){
            logger.error("e:{}", e);
        }

        logger.debug("postBarJson:{}", postBarJson.toString());
        return postBarJson;
    }
}
