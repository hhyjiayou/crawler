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
import java.util.*;

/**
 * Created by hhy on 16-9-2.
 */
@Service
public class TecentApi extends PostBarApi{
    private static final Logger logger = LoggerFactory.getLogger(TecentApi.class);
    private static final String TECENT_URLAPI_PREFIX = "http://coral.qq.com/article/";
    private static final String TECENT_HOTURLAPI_SUFFIX = "/hotcomment?reqnum=10&callback=myHotcommentLis";
    private static final String TECENT_NEWURLAPI_SUFFIX = "/comment?commentid=%s&reqnum=%d&tag=&callback=mainComment";
    private static final String TECENT_DATA = "data";
    private static final String TECENT_COMMENTID = "commentid";
    private static final String COMMENTID_MAP = "id";
    private static final String TECENT_CONTENT = "content";
    private static final String TECENT_API_COUNT = "total";

    private static Map<String, Map<Integer, String> > commentIdMap = new HashMap<String, Map<Integer, String> >();

    public static String getCmtId(String url){
        String cmt_id = "";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements newsHeadlines = doc.select("script");
            Iterator<Element> it = newsHeadlines.iterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();
                String dataTmp = element.toString();
                if(dataTmp.contains("cmt_id")){
                    String[] data = element.data().toString().split(";");
                    for (String variable : data) {
                        if (variable.contains("=")) {
                            if (variable.contains("cmt_id")) {
                                String cmt_idTmp = variable.split("=")[1];
                                if(cmt_idTmp.contains("\"")){
                                    cmt_id = cmt_idTmp.split("\"")[1];
                                }else{
                                    cmt_id = cmt_idTmp.substring(1);
                                }
                                return cmt_id;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("e:{}", e);
        }
        return cmt_id;
    }

    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {
        String cmt_id = getCmtId(url);
        logger.debug("cmt_id:{}", cmt_id);
        logger.debug("commentIdMap:{}", commentIdMap);

        if(null == cmt_id || cmt_id.isEmpty()){
            logger.error("empty cmt_id:{}", cmt_id);
            return null;
        }

        String url_prefix = TECENT_URLAPI_PREFIX + cmt_id;
        if(PostBarType.HOT == postBarType){
            String hot_url = url_prefix + TECENT_HOTURLAPI_SUFFIX;
            return hot_url;
        }else if(PostBarType.NEW == postBarType){
            String commentId = "";
            if(1 == start){
                commentId = "0";
            }else{
                commentId = commentIdMap.get(url).get(start);
            }
            if(null != commentId && !commentId.isEmpty()){
                String new_urlTmp = String.format(TECENT_NEWURLAPI_SUFFIX, commentId, limit );
                String new_url = url_prefix + new_urlTmp;
                return new_url;
            }

        }
        return null;
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
            JSONObject jsonObject = (JSONObject)jsonObjectAll.get(TECENT_DATA);
            JSONArray contentJsonArray = new JSONArray();
            JSONArray jsonArray = null;
            if(jsonObject.has(TECENT_COMMENTID)){//热门评论
                jsonArray = (JSONArray)jsonObject.get(TECENT_COMMENTID);
            }else {
                logger.error("unknown postBarType:{} or not need key json:{}",postBarType, jsonObject.toString());
                return postBarJson;
            }


            int size = jsonArray.length();
            JSONObject commentMap = (JSONObject) jsonArray.get(size-1);
            if(commentMap.has(COMMENTID_MAP)){
                String commentId = (String) commentMap.get(COMMENTID_MAP);
                if(null != commentIdMap.get(url)){
                    Map<Integer, String> innerMap = commentIdMap.get(url);
                    innerMap.put(start + 1, commentId);
                    commentIdMap.put(url, innerMap);
                }else{
                    Map<Integer, String> innerMap = new HashMap<Integer, String>();
                    innerMap.put(start+1, commentId);
                    commentIdMap.put(url, innerMap);
                }
                
            }
            //对jsonArray按发布时间进行排序
            List<JSONObject> jsonList = new ArrayList<JSONObject>();
            for(int i=0; i<size; i++){
                JSONObject value= (JSONObject) jsonArray.get(i);
                jsonList.add(value);

            }

            Collections.sort(jsonList, new CrawlerUtil.SortByTecent());

            size = jsonList.size();
            for (int i = 0; i < size; i++){
                JSONObject postBarJsonTmp = new JSONObject();
                JSONObject value= jsonList.get(i);
                postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                String content1 = (String) value.get(TECENT_CONTENT);
                String content = content1.replaceAll("<br>", "");
                postBarJsonTmp.put(Constant.POSTBAR_CONTENT, content);
                contentJsonArray.put(postBarJsonTmp);
            }

            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);
            if(jsonObject.has(TECENT_API_COUNT) ){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObject.get(TECENT_API_COUNT));
            }

        }catch (JSONException e){
            logger.error("e:{}", e);
        }

        logger.debug("postBarJson:{}", postBarJson.toString());
        return postBarJson;
    }

}
