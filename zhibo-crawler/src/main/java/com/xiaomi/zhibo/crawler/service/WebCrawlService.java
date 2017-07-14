package com.xiaomi.zhibo.crawler.service;

import com.xiaomi.huyu.blink.utils.StringUtils;
import com.xiaomi.zhibo.crawler.biz.CrawlerBiz;
import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.ErrorCode;
import com.xiaomi.zhibo.crawler.constant.NetType;
import com.xiaomi.zhibo.crawler.constant.PostBarType;
import com.xiaomi.zhibo.crawler.dao.MistoreDao;
import com.xiaomi.zhibo.crawler.entity.CommentJson;
import com.xiaomi.zhibo.crawler.pb.PbFeedsComment;
import com.xiaomi.zhibo.crawler.pb.PbFeedsStatStore;
import com.xiaomi.zhibo.crawler.service.process.*;
import com.xiaomi.zhibo.crawler.util.ZkUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by orion on 16/6/29.
 */

@Service
public class WebCrawlService {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawlService.class);
    
    @Autowired
    NetEaseApi netEaseApi;

    @Autowired
    SinaApi sinaApi;
    
    @Autowired
    IfengApi ifengApi;

    @Autowired
    SohuApi sohuApi;

    @Autowired
    TecentApi tecentApi;

    @Autowired
    TouTiaoApi touTiaoApi;

    @Autowired
    WeiboApi weiboApi;

    @Autowired
    CrawlerBiz crawlerBiz;

    @Autowired
    GetNicknameMap getNicknameMap;

    @Autowired
    MistoreDao mistoreDao;
    

    public ErrorCode getPostBarContent(NetType netType, String feedId, String url, List<PostBarType> postBarTypes,
                                       Integer start, Integer limit, JSONObject[] respJson){
        
        JSONObject contentJson = null;

        switch (netType){
            case NET_EASE:
                contentJson = netEaseApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            case SINA:
                contentJson = sinaApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            case IFENG:
                contentJson = ifengApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            case SOHU:
                contentJson = sohuApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            case TOUTIAO:
                contentJson = touTiaoApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            case TECENT:
                contentJson = tecentApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            case WEIBO:
                contentJson = weiboApi.getPostBarByApi(url, start, limit, postBarTypes, netType);
                break;
            default:
                logger.error("unknown netType:{}, feedId:{}, url:{}, postBarTypes:{}",
                        netType, feedId, url, postBarTypes);
                break;
        }

        
        if(null == contentJson || 0 >=contentJson.length()){
            logger.error("empty content json:{}", contentJson);
            return ErrorCode.POSTBAR_ERROR;
        }

        try {
            if(contentJson.has(Constant.POSTBAR_TIMEOUT) && contentJson.get(Constant.POSTBAR_TIMEOUT).equals(Constant.POSTBAR_TIMEOUT)){
                return ErrorCode.CON_TIMEOUT;
            }
        } catch (JSONException e) {
           logger.error("json error:{}", e);
        }

        respJson[0] = contentJson;
        return ErrorCode.OK;
    }
    
    public String getPostBarByApi(Integer netTypeInt, String feedId, String url, List<Integer> postBarTypeList,
                                  Integer start, Integer limit){
    
        JSONObject respJson = new JSONObject();
        ErrorCode errorCode = ErrorCode.OK;

        int startRsp = start;

        
        try{
            if(null ==url || null == postBarTypeList || null == start || null == limit || null == netTypeInt){
                logger.error("feedId:{}, url:{}, postBarTypeList:{}, start:{}, limit:{}, netTypeInt:{}", feedId, url, postBarTypeList, start, limit, netTypeInt);
                errorCode = ErrorCode.PARAM_ERROR;
            }else {
                NetType netType = NetType.getNetType(netTypeInt);

                NetType netTypeTmp = NetType.getNetType(url);
                if (null == netTypeInt || netType == NetType.UNKNOWN || netType != netTypeTmp){
                    logger.error("additional netType:{}, netTypeTmp:{}", netType, netTypeTmp);
                    netType = netTypeTmp;
                }
                if(0 == start && netType != NetType.NET_EASE){
                    startRsp = 1;
                }
                List<PostBarType> postBarTypes = new ArrayList<PostBarType>();

                if(netType == NetType.TOUTIAO){
                    List<Integer> postBarTypesTmp = new ArrayList<Integer>();
                    postBarTypesTmp.add(PostBarType.NEW.getId());
                    postBarTypeList = postBarTypesTmp;
                }

                for (Integer id : postBarTypeList){
                    PostBarType postBarType = PostBarType.getPostBarType(id);
                    if (PostBarType.UNKNOWN == postBarType){
                        logger.error("unknown postType:{}", postBarType);
                        continue;
                    }
                    postBarTypes.add(postBarType);
                }
                if(postBarTypes.size() <= 0){
                    errorCode = ErrorCode.PARAM_ERROR;
                }else{
                    logger.info("netType:{}, feedId:{}, url:{}", netType, feedId, url);

                    JSONObject[] dataJson = new JSONObject[1];
                    errorCode = getPostBarContent(netType, feedId, url, postBarTypes, startRsp, limit, dataJson);


                    if(errorCode == ErrorCode.OK){
                        respJson.put(Constant.KEY_DATA, dataJson[0]);
                    }
                }
    

            }

            respJson.put(Constant.KEY_START, startRsp);
            respJson.put(Constant.KEY_CODE, errorCode.getValue());
            respJson.put(Constant.KEY_MSG, errorCode.getMsg());
            
        } catch (Exception e){
            logger.error("e:{}", e);
        }
        
        return respJson.toString();
    }
    
    
    public String publishPostBar(String feedOwnerId, String feedId, String data, boolean isGood){

        ErrorCode errorCode = ErrorCode.OK;
        JSONObject respJson = new JSONObject();
        int delayTime = ZkUtil.getCrawlerZkConfig().getDelayTime();
        logger.info("delayTime:{}", delayTime);

        try {
            if(null == feedOwnerId || feedOwnerId.isEmpty() || null == feedId ||
                    feedId.isEmpty() || null == data || data.isEmpty()){
                logger.error("empty feedOwnerId:{},feedId:{}, data:{}", feedOwnerId, feedId, data);
                errorCode = ErrorCode.PARAM_ERROR;
                respJson.put(Constant.KEY_CODE, errorCode.getValue());
                respJson.put(Constant.KEY_DATA, errorCode.getMsg());
                return respJson.toString();
            }
            if(!checkFeedId(feedId, "_")){
                logger.error("无效的feedId:{}", feedId);
                errorCode = ErrorCode.INEFFCT_FEEDID;
                respJson.put(Constant.KEY_CODE, errorCode.getValue());
                respJson.put(Constant.KEY_DATA, errorCode.getMsg());
                return respJson.toString();
            }

            List<CommentJson.Comment> commentList = new ArrayList<CommentJson.Comment>();
            long time = new Date().getTime();

            JSONArray dataArray = new JSONArray(data);
            logger.debug("dataArray:{}",dataArray);
            if(null == dataArray){
                errorCode = ErrorCode.PARAM_ERROR;
                logger.error("empty dataArray:{}", dataArray);
            }else{

                //获得用户id的昵称<id, nickname>
                Map<Long, String> nicknameMap = getNicknameMap.getNicknameMap(dataArray);
                if(null == nicknameMap){
                    logger.error("empty nicknameMap:{}", nicknameMap);
                }


                for(int i=0; i<dataArray.length();i++){
                    JSONObject dataJson = (JSONObject) dataArray.get(i);
                    logger.debug("dataJson:{}", dataJson);
                    if(null == dataJson || !dataJson.has("content") || null == dataJson.get("content") ||
                            !dataJson.has("uid") || null == dataJson.get("uid")){
                        logger.error("empty dataJson:{}", dataJson);
                        continue;
                    }

                    Long fromUid = Long.valueOf(dataJson.get("uid").toString());
                    CommentJson.Comment comment = new CommentJson.Comment();
                    comment.setFromUid(fromUid );
                    comment.setContent( dataJson.get("content").toString() );
                    if(null != nicknameMap && null != nicknameMap.get(fromUid) && !nicknameMap.get(fromUid).isEmpty() ){
                        comment.setFromNickname(nicknameMap.get(fromUid));
                    }else {
                        comment.setFromNickname(dataJson.get("uid").toString());
                    }
                    comment.setCreateTime(time);
                    time += delayTime*1000;
                    comment.setGood(isGood);
                    commentList.add(comment);
                }
                logger.debug("feedId:{}, feedOwerId:{}, commentList:{}", feedId, feedOwnerId, commentList);
                errorCode = crawlerBiz.putCommentList(feedOwnerId, feedId, commentList);
            }

            if(errorCode == ErrorCode.OK){  //将feed的观看人数加评论数的30倍
                int addFeedSizeTmp = commentList.size();
                if(0 < addFeedSizeTmp){
                    String key = MistoreDao.CRAWLER_FEED_KEY + feedId;
                    PbFeedsStatStore.FeedsStatStore feedsStatStore = mistoreDao.getFeedsStatStore(key);
                    if(null == feedsStatStore){

                        logger.error("value is null for key:{}, feedsStatStore:{}", key, feedsStatStore);
                        long viewCount = 30*addFeedSizeTmp;

                        PbFeedsStatStore.FeedsStatStore.Builder feedsStatStoreBuilder = PbFeedsStatStore.FeedsStatStore.newBuilder();
                        feedsStatStoreBuilder.setViewcount(viewCount);
                        logger.debug("after feedsStatStore:{}", feedsStatStoreBuilder.build());
                        boolean status = mistoreDao.set(key, feedsStatStoreBuilder.build());
                        if(false == status){
                            logger.debug("error set mistore key:{}",key);
                        }
                    }else {
                        if(feedsStatStore.hasViewcount()){
                            logger.debug("before feedsStatStore:{}", feedsStatStore);
                            long viewCount = feedsStatStore.getViewcount()+30*addFeedSizeTmp;
                            PbFeedsStatStore.FeedsStatStore.Builder feedsStatStoreBuilder = PbFeedsStatStore.FeedsStatStore.newBuilder();
                            if(feedsStatStore.hasCommentCount()){
                                feedsStatStoreBuilder.setCommentCount(feedsStatStore.getCommentCount());
                            }
                            if(feedsStatStore.hasLikeCount()){
                                feedsStatStoreBuilder.setLikeCount(feedsStatStore.getLikeCount());
                            }
                            feedsStatStoreBuilder.setViewcount(viewCount);
                            logger.debug("after feedsStatStore:{}", feedsStatStoreBuilder.build());
                            boolean status = mistoreDao.set(key, feedsStatStoreBuilder.build());
                            if(false == status){
                                logger.debug("error set mistore key:{}",key);
                            }
                        }
                    }
                }
            }

            respJson.put(Constant.KEY_CODE, errorCode.getValue());
            respJson.put(Constant.KEY_DATA, errorCode.getMsg());

        } catch (JSONException e) {
            logger.error("json error:{}", e);
        }

        return respJson.toString();
    }


    public void getAllPostBarByApi(Integer netTypeInt, String feedId, String url, List<Integer> postBarTypeList, String feedOwnerId){

        //求出该feedId中已有的评论数
        Long[] cmntNum = new Long[1];
        ErrorCode errorCode = ErrorCode.OK;

        if(null == feedId || feedId.isEmpty() || null == url || url.isEmpty() || null == netTypeInt || null == postBarTypeList){
            logger.error("empty par feedId:{}, feedOwnerId:{}, url:{}, netTypeInt:{}, postBarTypeList:{}", feedId,
                    feedOwnerId, url, netTypeInt, postBarTypeList);
            errorCode = ErrorCode.PARAM_ERROR;
            return;
        }
        if(!checkFeedId(feedId, "_")){
            logger.error("invalid feedId");
            return;
        }
        errorCode = crawlerBiz.getCmntTotalNum(feedId, cmntNum);
        if(null == cmntNum[0]){
            cmntNum[0] = 0L;
        }
        logger.debug("cmntNum:{}", cmntNum[0]);

        int maxCmntLimit = ZkUtil.getCrawlerZkConfig().getMaxCmntLimit();
        int minCmntLeft = ZkUtil.getCrawlerZkConfig().getMinCmntLeft();
        long leftCmntNum = maxCmntLimit - cmntNum[0] - minCmntLeft;
        logger.debug("maxCmntLimit:{}, minCmntLeft:{}, leftCmntNum:{}", maxCmntLimit, minCmntLeft, leftCmntNum);

        if(errorCode == ErrorCode.OK){
            JSONArray addCmntArray = publishAllCmnt(leftCmntNum, netTypeInt, feedId, url, postBarTypeList);
            logger.info("addCmntArray:{}", addCmntArray);
            if (null != addCmntArray && 0 < addCmntArray.length()) {
                publishPostBar(feedOwnerId, feedId, addCmntArray.toString(), false); //劣质评论
            }
        }
    }

    public JSONArray publishAllCmnt(long leftCmntNum, Integer netTypeInt, String feedId, String url, List<Integer> postBarTypeList){
        //不重复添加评论
        Boolean[] addHot = new Boolean[]{true};
        Boolean[] addNew = new Boolean[]{true};

        ErrorCode errorCode = ErrorCode.OK;

        JSONArray addCmntArray = new JSONArray();
        //避免重复添加数据
        String[] hotFirst = new String[]{""};
        String[] hotEnd = new String[]{""};

        String[] newFirst = new String[]{""};
        String[] newEnd = new String[]{""};

        try {
            Integer[] count = new Integer[1];
            count[0] = 0;
            int start = 1, limit = 20;
            if(netTypeInt == NetType.NET_EASE.getId()){
                start = 0;
            }
            while(count[0] < leftCmntNum){
                int beforeCount = count[0];
                String rsp = getPostBarByApi(netTypeInt, feedId, url, postBarTypeList, start, limit);
                if(null != rsp) {
                    JSONObject rspJson = new JSONObject(rsp);
                    if (rspJson.has(Constant.KEY_CODE)) {
                        int status = (Integer) rspJson.get(Constant.KEY_CODE);
                        if (errorCode.getValue() == status) {
                            if (rspJson.has(Constant.KEY_DATA)) {
                                JSONObject dataJson = (JSONObject) rspJson.get(Constant.KEY_DATA);
                                addComments(leftCmntNum, PostBarType.HOT.getName(), dataJson, addCmntArray, newFirst, newEnd, count, addNew);
                                addComments(leftCmntNum, PostBarType.NEW.getName(), dataJson, addCmntArray, hotFirst, hotEnd, count, addHot);
                            } else {
                                logger.error("skip while");
                                return addCmntArray;
                            }
                            if (count[0] == beforeCount || (false == addHot[0] && false == addNew[0])) {
                                logger.debug("no data add skip");
                                return addCmntArray;
                            }
                            int startTmp = (Integer) rspJson.get(Constant.KEY_START);
                            start = startTmp + 1;
                        }else{
                            logger.debug("error status:{}", status);
                            return addCmntArray;
                        }
                    }else {
                        logger.error("no KEY_CODE:{}", Constant.KEY_CODE);
                        return addCmntArray;
                    }

                    if(count[0] == beforeCount){
                        logger.debug("no data add skip");
                        return addCmntArray;
                    }
                }else{
                    logger.error("nul rsp:{}", rsp);
                    return addCmntArray;
                }
            }
            logger.debug("count:{}, leftCmntNum:{}", count[0], leftCmntNum);
        } catch (JSONException e) {
            logger.error("json error:{}", e);
        }
        return addCmntArray;
    }

    public void addComments(long leftCmntNum, String commentType, JSONObject dataJson, JSONArray addCmntArray,
                             String[] first, String[] end, Integer[] count,  Boolean[] addNew){

        try {
            if(addNew[0]){
                if(dataJson.has(commentType)){
                    JSONObject newJson = (JSONObject) dataJson.get(commentType);
                    if(newJson.has(Constant.POSTBAR_DATA)){
                        JSONArray postbarData = (JSONArray) newJson.get(Constant.POSTBAR_DATA);
                        logger.info("postbarData:{}", postbarData);
                        if(postbarData.length() > 0){
                            String firstStr = "";
                            String endStr = "";
                            if(postbarData.length() == 1){//只有一条
                                firstStr = first[0];
                            }else{
                                JSONObject firstJson =  (JSONObject) postbarData.get(0);
                                firstStr = (String)firstJson.get(Constant.POSTBAR_CONTENT);
                            }
                            JSONObject endJson = (JSONObject) postbarData.get(postbarData.length()-1);
                            endStr = (String)endJson.get(Constant.POSTBAR_CONTENT);
                            logger.debug("firstStr:{}, endStr:{}", firstStr, endStr);
                            logger.debug("first:{}, end:{}", first[0], end[0]);
                            if(first[0].equals(firstStr) && end[0].equals(endStr)){
                                addNew[0] = false;
                            }else {
                                first[0] = firstStr;
                                end[0] = endStr;
                                for (int i = 0; i < postbarData.length() && count[0] < leftCmntNum; i++) {
                                    JSONObject jsonTmp = (JSONObject) postbarData.get(i);
                                    addCmntArray.put(jsonTmp);
                                    count[0]++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            logger.error("json error:{}",e);
        }


    }

    //加载评论
    public String loadFeedComments(String feedId, Integer start, Integer limit){
        ErrorCode errorCode = ErrorCode.OK;
        int total_count = 0;

        JSONObject respJson = new JSONObject();
        try {
            if(null == feedId || feedId.isEmpty() || null == start || null == limit || start<0 || limit <= 0){
                errorCode = ErrorCode.PARAM_ERROR;
                logger.error("empty feedId:{}", feedId);
            }else{
                List<PbFeedsComment.CommentInfo> commentInfoList = crawlerBiz.getFeedComments(feedId);

                if(null == commentInfoList){
                    logger.error("null commentInfoList:{}", commentInfoList);
                    errorCode = ErrorCode.LOADCMT_ERROR;
                }else{
                    JSONArray dataArray = new JSONArray();
                    total_count = commentInfoList.size();
                    logger.info("total_count:{}", total_count);
                    try {
                        for(int i= start*limit; i < (start*limit + limit) && i < total_count; i++){
                            PbFeedsComment.CommentInfo commentInfo = commentInfoList.get(i);
                            if(null == commentInfo || !commentInfo.hasCommentId() || !commentInfo.hasContent() ||
                                    null == commentInfo.getContent() || !commentInfo.hasFromUid()){
                                logger.error("error commentInfo:{}", commentInfo);
                                continue;
                            }
                            JSONObject dataJson = new JSONObject();
                            dataJson.put(Constant.POSTBAR_COMMENT_ID, commentInfo.getCommentId());
                            dataJson.put(Constant.POSTBAR_UID, commentInfo.getFromUid());
                            dataJson.put(Constant.POSTBAR_CONTENT, commentInfo.getContent());
                            if(commentInfo.hasIsGood()){
                                dataJson.put(Constant.POSTBAR_IS_GOOD, commentInfo.getIsGood());
                            }

                            dataArray.put(dataJson);
                        }
                        if(null != dataArray){
                            respJson.put(Constant.KEY_DATA, dataArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            respJson.put(Constant.POSTBAR_TOTAL_COUNT,total_count);
            respJson.put(Constant.KEY_CODE, errorCode.getValue());
            respJson.put(Constant.KEY_MSG, errorCode.getMsg());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return respJson.toString();
    }


    //置顶评论
    public String stickyFeedComments(String feedId, String feedOwnerId,  Long commentId){
        ErrorCode errorCode = ErrorCode.OK;

        JSONObject respJson = new JSONObject();
        try {
            if(null == feedId || feedId.isEmpty() || null == commentId || null == feedOwnerId || feedOwnerId.isEmpty()){
                errorCode = ErrorCode.PARAM_ERROR;
                logger.error("empty feedId:{}, feedOwnerId:{}, commentId:{}",feedId, feedOwnerId, commentId);
            }else{
                List<PbFeedsComment.CommentInfo> commentInfoList = crawlerBiz.getFeedComments(feedId);
                logger.debug("commentInfoList:{}", commentInfoList);
                if(null == commentInfoList){
                    logger.error("null commentInfoList:{}", commentInfoList);
                    errorCode = ErrorCode.LOADCMT_ERROR;
                }else{
                    for(PbFeedsComment.CommentInfo commentInfo : commentInfoList){
                        if(null == commentInfo || !commentInfo.hasCommentId() || !commentInfo.hasContent() ||
                                null == commentInfo.getContent() || !commentInfo.hasFromUid()){
                            logger.error("error commentInfo:{}", commentInfo);
                            continue;
                        }
                        if(commentInfo.getCommentId() == commentId){
                            logger.debug("comment:{}", commentInfo);
                            List<CommentJson.Comment> commentList = new ArrayList<CommentJson.Comment>();
                            CommentJson.Comment comment = new CommentJson.Comment();
                            if(commentInfo.hasCreateTime() && commentInfo.hasFromUid() && commentInfo.hasContent()){
                                comment.setGood(true);
                                comment.setCreateTime(commentInfo.getCreateTime());
                                comment.setContent(commentInfo.getContent());
                                comment.setFromUid(commentInfo.getFromUid());
                                if(commentInfo.hasToUid()){
                                    comment.setToUid(commentInfo.getToUid());
                                }
                                if(commentInfo.hasFromNickname() && null != commentInfo.getFromNickname()){
                                    comment.setFromNickname(commentInfo.getFromNickname());
                                }
                                if(commentInfo.hasToNickname() && null != commentInfo.getToNickname()){
                                    comment.setToNickname(commentInfo.getToNickname());
                                }
                                commentList.add(comment);
                                errorCode = crawlerBiz.putCommentList(feedOwnerId, feedId, commentList);
                            }
                            break;
                        }
                    }
                }
            }

            respJson.put(Constant.KEY_CODE, errorCode.getValue());
            respJson.put(Constant.KEY_MSG, errorCode.getMsg());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return respJson.toString();
    }

    public boolean checkFeedId(String feedId, String prim){
        if( null == feedId || feedId.isEmpty()){
            return false;
        }
        if(!feedId.contains(prim)){
            return false;
        }
        int start = feedId.lastIndexOf(prim);
        String lastFeedId = feedId.substring(start+1);
        return StringUtils.isNumeric(lastFeedId);
    }



    //删除评论
    public String delFeedComments(String feedId, List<Long> commentIds){
        ErrorCode errorCode = ErrorCode.OK;
        JSONObject resp = new JSONObject();
        if( null == feedId || feedId.isEmpty() || !checkFeedId(feedId, "_") || null == commentIds || commentIds.isEmpty() ){
            logger.error("empty feedId:{}, commentIds:{}", feedId, commentIds);
            errorCode = ErrorCode.PARAM_ERROR;
        }else{
            errorCode = crawlerBiz.delFeedComments(feedId, commentIds);
        }

        try {
            resp.put(Constant.KEY_CODE, errorCode.getValue());
            resp.put(Constant.KEY_MSG, errorCode.getMsg());
        } catch (JSONException e) {
            logger.error("json error:{}", e);
        }
        return resp.toString();
    }



}
