package com.xiaomi.zhibo.crawler.service.process;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.PostBarType;
import com.xiaomi.zhibo.crawler.util.CrawlerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by hhy on 16-9-6.
 */
@Service
public class WeiboApi extends PostBarApi{
    private static final Logger logger = LoggerFactory.getLogger(WeiboApi.class);
    private static final String WEIBO_NEWURL = "http://weibo.com/aj/v6/comment/big?ajwvr=6&page=%d&id=%s";
    private static final String WEIBO_HOTURL = "http://weibo.com/aj/v6/comment/big?ajwvr=6&id=%s&filter=hot&page=%d";
    private static final String WEIBO_DATA = "data";
    private static final String WEIBO_HTML = "html";
    private static final String WEIBO_API_COUNT = "count";
    public static String cookie = "";
    private static Map<Long, HtmlUnitDriver> pngMap = new HashMap<Long, HtmlUnitDriver>();


    private static String[] str62key = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z" };


    public  String concatCookie(HtmlUnitDriver driver) {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        String result = sb.toString();
        return result;
    }

    private static  String str62to10(String str) {
        String i10 = "0";
        int c = 0;
        for (int i = 0; i < str.length(); i++) {
            int n = str.length() - i - 1;
            String s = str.substring(i, i + 1);
            for (int k = 0; k < str62key.length; k++) {
                if (s.equals(str62key[k])) {
                    int h = k;
                    c += (int) (h * Math.pow(62, n));
                    break;
                }
            }
            i10 = String.valueOf(c);
        }
        return i10;
    }
    private static String urlToMid(String url){
        String mid = "";
        for (int i = url.length() - 4; i > -4; i = i - 4) {//分别以四个为一组
            int offset1 = i < 0 ? 0 : i;
            int offset2 = i + 4;
            String str = url.toString().substring(offset1, offset2);
            str = str62to10(str);//String类型的转化成十进制的数
            // 若不是第一组，则不足7位补0
            if (offset1 > 0) {
                while (str.length() < 7) {
                    str = '0' + str;
                }
            }
            mid = str + mid;
        }
        return mid;
    }

    @Override
    public String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType) {
        String weibo_url = "";

        String[] trimUrl = url.split("\\?")[0].split("/");
        String lastUrl = trimUrl[trimUrl.length-1];
        String id = urlToMid(lastUrl);

        if(PostBarType.HOT == postBarType){
            weibo_url = String.format(WEIBO_HOTURL, id, start);
        }else{
            weibo_url = String.format(WEIBO_NEWURL, start, id);
        }

        return weibo_url;
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
            logger.debug("data:{}", data);
            JSONObject jsonObjectAll = new JSONObject(data);
            JSONObject jsonObject = (JSONObject)jsonObjectAll.get(WEIBO_DATA);
            JSONArray contentJsonArray = new JSONArray();

            String htmlJson = "";
            if( jsonObject.has(WEIBO_HTML) ){
                htmlJson = (String) jsonObject.get(WEIBO_HTML);
            }else {
                logger.error("unknown postBarType:{} or not need key json:{}",postBarType, jsonObject.toString());
                return postBarJson;
            }

            String[] contentList = htmlJson.split(">：");
            if(contentList.length <= 1){
                logger.error("null htmlJson:{}", htmlJson);
                return null;
            }

            for(int i = 1; i < contentList.length; i++){
                JSONObject postBarJsonTmp = new JSONObject();
                String contentTmp = contentList[i];
                int endIndex = contentTmp.indexOf("<");
                if(endIndex >= contentTmp.length()){
                    logger.error("indexout:{}, length:{}", endIndex, contentTmp.length());
                    continue;
                }
                String content = contentTmp.substring(0, endIndex);
                if(null != content && !content.isEmpty() && !content.contains("回复")){
                    postBarJsonTmp.put(Constant.POSTBAR_UID, CrawlerUtil.getRobotId());
                    postBarJsonTmp.put(Constant.POSTBAR_CONTENT, content);
                    contentJsonArray.put(postBarJsonTmp);
                }
            }

            postBarJson.put(Constant.POSTBAR_DATA, contentJsonArray);
            if(jsonObject.has(WEIBO_API_COUNT) ){
                postBarJson.put(Constant.POSTBAR_TOTAL_COUNT, jsonObject.get(WEIBO_API_COUNT));
            }

        }catch (JSONException e){
            logger.error("e:{}", e);
        }

        logger.debug("postBarJson:{}", postBarJson.toString());
        return postBarJson;
    }
}
