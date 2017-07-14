package com.xiaomi.zhibo.crawler.biz;

import com.xiaomi.huyu.blink.client.model.BlinkResponse;
import com.xiaomi.zhibo.crawler.cache.AddCommentQueue;
import com.xiaomi.zhibo.crawler.constant.ErrorCode;
import com.xiaomi.zhibo.crawler.constant.ServiceErrorCode;
import com.xiaomi.zhibo.crawler.entity.CommentJson;
import com.xiaomi.zhibo.crawler.pb.PbComment;
import com.xiaomi.zhibo.crawler.pb.PbFeedsComment;
import com.xiaomi.zhibo.crawler.rpc.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhongxiankui
 * @time : 2016-07,05 11:01:上午11:01
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */

@Service
public class CrawlerBiz {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlerBiz.class);

    @Autowired
    CommentService commentService;

    /*批量存取数据*/
    public ErrorCode putCommentList(String feedOwnerId, String feedId, List<CommentJson.Comment> commentJsonList){

        ErrorCode errorCode = ErrorCode.OK;

        if(null == feedId || feedId.isEmpty() ||
                null == commentJsonList || commentJsonList.isEmpty()){
            logger.error("empty feedOwnerId:{}, feedId:{}, commentJsonList:{}", feedOwnerId, feedId, commentJsonList);
            errorCode = ErrorCode.PARAM_ERROR;
            return errorCode;
        }


        PbComment.MultiCreateFeedCommentsReq.Builder commentsReqBuilder = PbComment.MultiCreateFeedCommentsReq.newBuilder();
        commentsReqBuilder.setFeedId(feedId);

        for(CommentJson.Comment comment : commentJsonList){
            PbComment.Comment.Builder commentBuilder = PbComment.Comment.newBuilder();
            commentBuilder.setFromUid(comment.getFromUid());
            commentBuilder.setContent(comment.getContent());
            commentBuilder.setCreateTime(comment.getCreateTime());
            /*if(null != feedOwnerId){
                commentBuilder.setToUid(Long.valueOf(feedOwnerId));
            }*/
            if(null != comment.getFromNickname()){
                commentBuilder.setFromNickname(comment.getFromNickname());
            }
            /*if(null != comment.getToNickname()){
                commentBuilder.setToNickname(comment.getToNickname());
            }*/
            if(null != comment.getGood()){
                commentBuilder.setIsGood(comment.getGood());
            }


            commentsReqBuilder.addComments(commentBuilder);
        }
        if(null != commentsReqBuilder.build()){
            boolean status = AddCommentQueue.addComment(commentsReqBuilder.build());
            if(false == status){
                errorCode = ErrorCode.ADDCMT_ERROR;
            }
        }
       // BlinkResponse response = commentService.getCommentReq(commentsReqBuilder.build());
        //ServiceErrorCode serviceErrorCode = commentService.getCommentRsp(response);
        //if(ServiceErrorCode.OK != serviceErrorCode ){
          //  errorCode = ErrorCode.ADDCMT_ERROR;
        //}
        return errorCode;
    }

    public ErrorCode getCmntTotalNum(String feedId, Long[] cmntNum){
        ErrorCode errorCode = ErrorCode.OK;

        if(null == feedId || feedId.isEmpty()){
            logger.error("empty feedId:{}", feedId);
            errorCode = ErrorCode.PARAM_ERROR;
            return errorCode;
        }

        PbFeedsComment.GetFeedCommentsRequest.Builder feedCommentsBuilder = PbFeedsComment.GetFeedCommentsRequest.newBuilder();
        feedCommentsBuilder.setFeedId(feedId);

        BlinkResponse response = commentService.getCommentNumReq(feedCommentsBuilder.build());
        try {
            if (response.isSuccess() && response.serverIsSuccess()) {
                PbFeedsComment.GetFeedCommentsResponse rspTmp = PbFeedsComment.GetFeedCommentsResponse.parseFrom(response.getBody());
                logger.debug("response_errcode:{}", rspTmp.getErrCode());
                if (rspTmp.hasErrCode() && rspTmp.getErrCode() == ServiceErrorCode.OK.getValue()) {
                    PbFeedsComment.FeedComment feedComment = rspTmp.getFeedComment();
                    if(feedComment.hasTotal()){
                        cmntNum[0] = feedComment.getTotal();
                    }
                } else {
                    errorCode = ErrorCode.ADDCMT_ERROR;
                    logger.error("comment code error, errorCode: {}", rspTmp.getErrCode());
                }
            } else {
                errorCode = ErrorCode.ADDCMT_ERROR;
                logger.error("comment code error, errorCode: {}", response.getClientErrorCode());
            }
        } catch (Exception e) {
            logger.error("unexpected error: {}", e);
        }
        return errorCode;
    }


    public List<PbFeedsComment.CommentInfo> getFeedComments(String feedId){
        logger.info("feedId:{}", feedId);

        PbFeedsComment.GetFeedCommentsRequest.Builder feedCommentsBuilder = PbFeedsComment.GetFeedCommentsRequest.newBuilder();
        feedCommentsBuilder.setFeedId(feedId);
        feedCommentsBuilder.setLimit(0);

        BlinkResponse response = commentService.getCommentNumReq(feedCommentsBuilder.build());
        List<PbFeedsComment.CommentInfo> commentInfoList = new ArrayList<PbFeedsComment.CommentInfo>();
        try {
            if (response.isSuccess() && response.serverIsSuccess()) {
                PbFeedsComment.GetFeedCommentsResponse rspTmp = PbFeedsComment.GetFeedCommentsResponse.parseFrom(response.getBody());
                if (rspTmp.hasErrCode() && rspTmp.getErrCode() == ServiceErrorCode.OK.getValue()) {
                    PbFeedsComment.FeedComment feedComment = rspTmp.getFeedComment();
                    if(null != feedComment){
                        commentInfoList = feedComment.getCommentInfosList();
                    }
                } else {
                    logger.error("comment code error, errorCode: {}", rspTmp.getErrCode());
                }
            } else {
                logger.error("comment code error, errorCode: {}", response.getClientErrorCode());
            }
        } catch (Exception e) {
            logger.error("unexpected error: {}", e);
        }

        logger.info("commentInfoList:{}", commentInfoList);
        return commentInfoList;
    }

    public ErrorCode delFeedComments(String feedId, List<Long> commentIds){
        ErrorCode errorCode = ErrorCode.OK;

        PbFeedsComment.MultiDelCommentReq.Builder delFeedCommentsBuilder = PbFeedsComment.MultiDelCommentReq.newBuilder();
        delFeedCommentsBuilder.setFeedId(feedId);
        delFeedCommentsBuilder.addAllCommentIds(commentIds);

        BlinkResponse response = commentService.delCommentsReq(delFeedCommentsBuilder.build());

        ServiceErrorCode serviceErrorCode = commentService.delCommentsRsp(response);
        if(ServiceErrorCode.OK != serviceErrorCode ){
            errorCode = ErrorCode.DELCMT_ERROR;
        }
        return errorCode;
    }
}
