package com.xiaomi.zhibo.crawler.service.process;

import com.xiaomi.zhibo.crawler.biz.UserBiz;
import com.xiaomi.zhibo.crawler.pb.PbUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hhy on 16-8-24.
 */
@Service
public class GetNicknameMap {

    @Autowired
    UserBiz userBiz;

    private static final Logger logger = LoggerFactory.getLogger(GetNicknameMap.class);

    private static final int REQ_MAX_NUM = 200;//请求最大长度

    public Map<Long, String> getNicknameMap(JSONArray dataArray){
        if(null == dataArray ){
           logger.error("empty dataArray:{}", dataArray);
            return null;
        }

        Map<Long, String> nicknameMap = new HashMap<Long, String>();

        try {
            List<Long> uidList = new ArrayList<Long>();
            for(int i=0; i<dataArray.length();i++){
                JSONObject dataJson = (JSONObject) dataArray.get(i);

                if(null == dataJson || !dataJson.has("uid") || null == dataJson.get("uid")){
                    logger.error("empty dataJson:{}", dataJson);
                    continue;
                }
                uidList.add(Long.valueOf(dataJson.get("uid").toString()));
            }

            nicknameMap = getUserInfoList(uidList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nicknameMap;
    }

    public Map<Long, String> getUserInfoList(List<Long> uidList){
        Map<Long, String> userInfoMap = new HashMap<Long, String>();

        List<Long> idListTmp = new ArrayList<Long>();
        int count = 0;
        int uidListSize = uidList.size();
        for (int i = 0; i < uidListSize; i++) {
            if(0 >= uidList.get(i)){
                logger.error("error uid", uidList.get(i));
                continue;
            }
            if (count <= REQ_MAX_NUM) {
                idListTmp.add(uidList.get(i));
                count++;
            }

            if (count-1 == REQ_MAX_NUM || i == uidListSize-1) {
                List<PbUser.UserInfo> userInfoListTmp = userBiz.getUserInfo(idListTmp);
                for (PbUser.UserInfo userInfo : userInfoListTmp) {
                    if (!userInfo.hasZuid() || 0 >= userInfo.getZuid() || !userInfo.hasNickname()
                            || null == userInfo.getNickname() || userInfo.getNickname().isEmpty()) {
                        logger.error("error userInfo:{}", userInfo.toString());
                        continue;
                    }
                    userInfoMap.put(userInfo.getZuid(), userInfo.getNickname());
                }

                count = 0;
                idListTmp.clear();
            }
        }
        logger.debug("userInfoMap:{}", userInfoMap);
        return userInfoMap;
    }

}
