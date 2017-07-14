package com.xiaomi.zhibo.crawler.rpc;

import com.xiaomi.huyu.blink.client.BlinkClientFactory;
import com.xiaomi.huyu.blink.client.Command;
import com.xiaomi.huyu.blink.client.IBlinkClient;
import com.xiaomi.huyu.blink.client.model.BlinkResponse;
import com.xiaomi.huyu.monitor.ModuleCallUtil;
import com.xiaomi.zhibo.crawler.constant.ModCall;
import com.xiaomi.zhibo.crawler.constant.ServiceErrorCode;
import com.xiaomi.zhibo.crawler.pb.PbComment;
import com.xiaomi.zhibo.crawler.pb.PbFeedsComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by hhy on 16-8-19.
 */
@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private IBlinkClient commentBlinkClient = BlinkClientFactory.getBlinkClient(ModCall.BLINK_COMMENT_CATALOG);

    public BlinkResponse getCommentReq(PbComment.MultiCreateFeedCommentsReq req) {

        logger.debug("req:{}", req.toString());

        Command command = new Command(ModCall.BLINK_COMMENT_HEADER_CMDID, ModCall.COMMENT_L5ID);
        long beginTime = System.currentTimeMillis();

        BlinkResponse response = commentBlinkClient.invoke(command, req.toByteArray());
        try {
            if (response == null) {
                logger.error("blink response is null");
                return response;
            }

            logger.info("ServerErrorCode:{},ClientErrorCode:{},Endpoint:{}", response.getServerErrorCode(),
                    response.getClientErrorCode(), response.getEndpoint());
            String providerIp = "";
            if (response.getEndpoint() != null) {
                providerIp = response.getEndpoint().getHostName();
            }
            int retVal = 0;
            if (response.getClientErrorCode() != ServiceErrorCode.OK.getValue()) {
                retVal = response.getClientErrorCode();
            } else if (response.getServerErrorCode() != ServiceErrorCode.OK.getValue()) {
                retVal = response.getServerErrorCode();
            }
            if (retVal == 0) {
                ModuleCallUtil.successCall(ModCall.COMMENT_C2S_MODID, ModCall.COMMENT_MODID, ModCall.COMMENT_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            } else {
                ModuleCallUtil.failCall(ModCall.COMMENT_C2S_MODID, ModCall.COMMENT_MODID, ModCall.COMMENT_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            }

            ServiceErrorCode errorRsp = getCommentRsp(response);
            if (ServiceErrorCode.OK != errorRsp) {
                logger.error("recharge code error:{}", errorRsp.getMsg());
                return null;
            }
        }catch (Exception e){
            logger.error("response error");
        }

        return response;
    }

    //
    public ServiceErrorCode getCommentRsp(BlinkResponse response) {

        try {
            if (response.isSuccess() && response.serverIsSuccess()) {
                PbComment.MultiCreateFeedCommentsRsp rspTmp = PbComment.MultiCreateFeedCommentsRsp.parseFrom(response.getBody());
                logger.debug("response_errcode:{}", rspTmp.getErrCode());
                if (rspTmp.hasErrCode() && rspTmp.getErrCode() == ServiceErrorCode.OK.getValue()) {
                    return ServiceErrorCode.OK;
                } else {
                    logger.error("comment code error, errorCode: {}", rspTmp.getErrCode());
                    return ServiceErrorCode.COMMENT;
                }
            } else {
                logger.error("comment code error, errorCode: {}", response.getClientErrorCode());
                return ServiceErrorCode.COMMENT;
            }
        } catch (Exception e) {
            logger.error("unexpected error: {}", e);
            return ServiceErrorCode.COMMENT;
        }
    }

    public BlinkResponse getCommentNumReq(PbFeedsComment.GetFeedCommentsRequest req ) {

        logger.debug("req:{}", req.toString());

        Command command = new Command(ModCall.BLINK_COMMENTNUM_HEADER_CMDID, ModCall.COMMENTNUM_L5ID);
        long beginTime = System.currentTimeMillis();

        BlinkResponse response = commentBlinkClient.invoke(command, req.toByteArray());
        try {
            if (response == null) {
                logger.error("blink response is null");
                return response;
            }

            logger.info("ServerErrorCode:{},ClientErrorCode:{},Endpoint:{}", response.getServerErrorCode(),
                    response.getClientErrorCode(), response.getEndpoint());
            String providerIp = "";
            if (response.getEndpoint() != null) {
                providerIp = response.getEndpoint().getHostName();
            }
            int retVal = 0;
            if (response.getClientErrorCode() != ServiceErrorCode.OK.getValue()) {
                retVal = response.getClientErrorCode();
            } else if (response.getServerErrorCode() != ServiceErrorCode.OK.getValue()) {
                retVal = response.getServerErrorCode();
            }
            if (retVal == 0) {
                ModuleCallUtil.successCall(ModCall.COMMENT_C2S_MODID, ModCall.COMMENT_MODID, ModCall.COMMENTNUM_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            } else {
                ModuleCallUtil.failCall(ModCall.COMMENT_C2S_MODID, ModCall.COMMENT_MODID, ModCall.COMMENTNUM_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            }

            ServiceErrorCode errorRsp = getCommentRsp(response);
            if (ServiceErrorCode.OK != errorRsp) {
                logger.error("recharge code error:{}", errorRsp.getMsg());
                return null;
            }
        }catch (Exception e){
            logger.error("response error");
        }

        return response;
    }


    public BlinkResponse delCommentsReq(PbFeedsComment.MultiDelCommentReq req) {

        logger.debug("req:{}", req.toString());

        Command command = new Command(ModCall.BLINK_DELCMNT_HEADER_CMDID, ModCall.DELCMNT_L5ID);
        long beginTime = System.currentTimeMillis();

        BlinkResponse response = commentBlinkClient.invoke(command, req.toByteArray());
        try {
            if (response == null) {
                logger.error("blink response is null");
                return response;
            }

            logger.info("ServerErrorCode:{},ClientErrorCode:{},Endpoint:{}", response.getServerErrorCode(),
                    response.getClientErrorCode(), response.getEndpoint());
            String providerIp = "";
            if (response.getEndpoint() != null) {
                providerIp = response.getEndpoint().getHostName();
            }
            int retVal = 0;
            if (response.getClientErrorCode() != ServiceErrorCode.OK.getValue()) {
                retVal = response.getClientErrorCode();
            } else if (response.getServerErrorCode() != ServiceErrorCode.OK.getValue()) {
                retVal = response.getServerErrorCode();
            }
            if (retVal == 0) {
                ModuleCallUtil.successCall(ModCall.COMMENT_C2S_MODID, ModCall.COMMENT_MODID, ModCall.DELCMNT_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            } else {
                ModuleCallUtil.failCall(ModCall.COMMENT_C2S_MODID, ModCall.COMMENT_MODID, ModCall.DELCMNT_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            }

            ServiceErrorCode errorRsp = getCommentRsp(response);
            if (ServiceErrorCode.OK != errorRsp) {
                logger.error("recharge code error:{}", errorRsp.getMsg());
                return null;
            }
        }catch (Exception e){
            logger.error("response error");
        }

        return response;
    }

    public ServiceErrorCode delCommentsRsp(BlinkResponse response) {

        try {
            if (response.isSuccess() && response.serverIsSuccess()) {
                PbFeedsComment.MultiDelCommentRsq rspTmp = PbFeedsComment.MultiDelCommentRsq.parseFrom(response.getBody());
                logger.debug("response_errcode:{}", rspTmp.getErrCode());
                if (rspTmp.hasErrCode() && rspTmp.getErrCode() == ServiceErrorCode.OK.getValue()) {
                    return ServiceErrorCode.OK;
                } else {
                    logger.error("comment code error, errorCode: {}", rspTmp.getErrCode());
                    return ServiceErrorCode.CMNT_DEL;
                }
            } else {
                logger.error("comment code error, errorCode: {}", response.getClientErrorCode());
                return ServiceErrorCode.CMNT_DEL;
            }
        } catch (Exception e) {
            logger.error("unexpected error: {}", e);
            return ServiceErrorCode.CMNT_DEL;
        }
    }
}
