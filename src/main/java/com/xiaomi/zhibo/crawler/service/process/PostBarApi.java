package com.xiaomi.zhibo.crawler.service.process;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.NetType;
import com.xiaomi.zhibo.crawler.constant.PostBarType;
import com.xiaomi.zhibo.crawler.util.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zhongxiankui
 * @time : 2016-08,18 16:26:下午4:26
 * @mail : zhongxiankui@xiaomi.com
 * @project MiTest
 */

public abstract class PostBarApi{
    
    private static final Logger logger = LoggerFactory.getLogger(PostBarApi.class);
    
    //获取数据
    public String getRawData(String url){
        return HttpClient.get(url);
    }

    public String getRawDataWeibo(String url){
        return HttpClient.getWeibo(url);
    }

    //获取贴吧信息
    public JSONObject getPostBarByApi(String url, Integer start, Integer limit, List<PostBarType> postBarTypes, NetType netType){
    
        if(start <= 0){
            start = 0;
            logger.error("revise start:{}, limit:{}", start, limit);
        }
        if(limit <= 0 || limit > 50){
            limit = 50;
            logger.error("revise start:{}, limit:{}", start, limit);
        }
        
        if(null == postBarTypes || postBarTypes.isEmpty() || null == url || url.isEmpty()){
            logger.error("empty url:{}, postBarTypes:{}", url, postBarTypes);
            return null;
        }

        logger.debug("postBarTypes:{}", postBarTypes);
        JSONObject postBarJson = new JSONObject();
        for (PostBarType postBarType : postBarTypes){
            String apiUrl = getPostBarApiUrl(url, start, limit, postBarType);
            logger.info("apiUrl:{}",  apiUrl);
            if(null == apiUrl || apiUrl.isEmpty()){
                logger.error("empty apiUrl:{}", apiUrl);
                return null;
            }
            String data = "";
            if(netType == NetType.WEIBO) {
                data = getRawDataWeibo(apiUrl);
            }else{
                data = getRawData(apiUrl);
            }

            if(null == data || data.isEmpty()){
                logger.error("empty data:{}", data);
                return null;
            }

            if(data.equals(Constant.POSTBAR_TIMEOUT)){
                try {
                    postBarJson.put(Constant.POSTBAR_TIMEOUT, data);
                } catch (JSONException e) {
                    logger.error("json error:{}", e);
                }
                return postBarJson;
            }

            String trimData = preTrim(data, postBarType);
    
            if(null == trimData || trimData.isEmpty()){
                logger.error("empty trimData:{}", trimData);
                return null;
            }
    
            JSONObject postBarJsonTmp = getPostBarJson(url, trimData, postBarType, limit, start);
            if(null == postBarJsonTmp){
                logger.error("empty postBarJsonTmp:{}", postBarJsonTmp);
                postBarJsonTmp = new JSONObject();
            }
            try {
                postBarJson.put(postBarType.getName(), postBarJsonTmp);
            } catch (JSONException e) {
                logger.error("e:{}", e);
            }
    
            afterPostBar(postBarJsonTmp);
        }

        return postBarJson;
    }
    
    //根据url获取评论调用api
    public abstract String getPostBarApiUrl(String url, Integer start, Integer limit, PostBarType postBarType);
    
    //返回数据前期处理
    public abstract String preTrim(String data, PostBarType postBarType);
    
    //提取评论json
    public abstract JSONObject getPostBarJson(String url, String data, PostBarType postBarType, int limit, int start);
    
    //获取的json数据进行存储等处理
    public boolean afterPostBar(final JSONObject postBarJson){
        
        return true;
    }
}
