package com.xiaomi.zhibo.crawler.biz;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.huyu.blink.client.model.BlinkResponse;
import com.xiaomi.zhibo.crawler.constant.ServiceErrorCode;
import com.xiaomi.zhibo.crawler.pb.PbUser;
import com.xiaomi.zhibo.crawler.rpc.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hhy on 16-8-24.
 */
@Service
public class UserBiz {

    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserBiz.class);

    public List<PbUser.UserInfo> getUserInfo(List<Long> zuids){
        List<PbUser.UserInfo> userInfoList = new ArrayList<PbUser.UserInfo>();
        PbUser.MutiGetUserInfoReq.Builder multiGetUserInfoReqBuilder = PbUser.MutiGetUserInfoReq.newBuilder();
        multiGetUserInfoReqBuilder.addAllZuid(zuids);
        BlinkResponse blinkResponse = userService.getUserInfoReq(multiGetUserInfoReqBuilder.build());

        if(null == blinkResponse || null == blinkResponse.getBody()){
            logger.error("empty blinkResponse:{}",blinkResponse);

        }else {
            try {
                PbUser.MutiGetUserInfoRsp mutiGetUserInfoRsp = PbUser.MutiGetUserInfoRsp.parseFrom(blinkResponse.getBody());
                logger.debug("rsp：{}",mutiGetUserInfoRsp);
                if(null==mutiGetUserInfoRsp) {
                    logger.error("empty rsp:{}",mutiGetUserInfoRsp);
                }
                else{
                    int retCode = mutiGetUserInfoRsp.getRetCode();
                    if(ServiceErrorCode.OK.getValue() != retCode){
                        logger.error("error retCode:{} !", retCode);
                    }else{
                        userInfoList=mutiGetUserInfoRsp.getUserInfosList();
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                logger.error("modcall error:{} !", e);
            }
        }
        return userInfoList;
    }
}
