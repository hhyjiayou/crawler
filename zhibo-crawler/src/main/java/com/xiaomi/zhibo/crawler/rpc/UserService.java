package com.xiaomi.zhibo.crawler.rpc;

import com.xiaomi.huyu.blink.client.BlinkClientFactory;
import com.xiaomi.huyu.blink.client.Command;
import com.xiaomi.huyu.blink.client.IBlinkClient;
import com.xiaomi.huyu.blink.client.model.BlinkResponse;
import com.xiaomi.huyu.monitor.ModuleCallUtil;
import com.xiaomi.zhibo.crawler.constant.ModCall;
import com.xiaomi.zhibo.crawler.constant.ServiceErrorCode;
import com.xiaomi.zhibo.crawler.pb.PbUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by hhy on 16-8-24.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private IBlinkClient userBlinkClient = BlinkClientFactory.getBlinkClient(ModCall.BLINK_USER_CATALOG);


    public BlinkResponse getUserInfoReq(PbUser.MutiGetUserInfoReq req) {

        logger.debug("req:{}", req.toString());

        Command command = new Command(ModCall.BLINK_USER_INFO_ONLINE_HEADER_CMDID, ModCall.USER_INFO_ONLINE_L5ID);
        long beginTime = System.currentTimeMillis();

        BlinkResponse response = userBlinkClient.invoke(command, req.toByteArray());
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
                ModuleCallUtil.successCall(ModCall.COMMENT_C2S_MODID, ModCall.USER_MODID, ModCall.USER_INFO_ONLINE_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            } else {
                ModuleCallUtil.failCall(ModCall.COMMENT_C2S_MODID, ModCall.USER_MODID, ModCall.USER_INFO_ONLINE_CMDID,
                        providerIp, retVal, (int) (System.currentTimeMillis() - beginTime));
            }

            ServiceErrorCode errorRsp = getUserInfoRsp(response);
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
    public ServiceErrorCode getUserInfoRsp(BlinkResponse response) {

        try {
            if (response.isSuccess() && response.serverIsSuccess()) {
                PbUser.MutiGetUserInfoRsp rspTmp = PbUser.MutiGetUserInfoRsp.parseFrom(response.getBody());
                if (rspTmp.hasRetCode() && rspTmp.getRetCode() == ServiceErrorCode.OK.getValue()) {
                    return ServiceErrorCode.OK;
                } else {
                    logger.error("User code error, errorCode: {}", rspTmp.getRetCode());
                    return ServiceErrorCode.USER;
                }
            } else {
                logger.error("user code error, errorCode: {}", response.getClientErrorCode());
                return ServiceErrorCode.USER;
            }
        } catch (Exception e) {
            logger.error("unexpected error: {}", e);
            return ServiceErrorCode.USER;
        }
    }
}
