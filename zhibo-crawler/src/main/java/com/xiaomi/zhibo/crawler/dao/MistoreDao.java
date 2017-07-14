package com.xiaomi.zhibo.crawler.dao;

import com.google.protobuf.GeneratedMessage;
import com.xiaomi.huyu.mistore.client.IMClient;
import com.xiaomi.huyu.mistore.client.impl.MistoreClientFactory;
import com.xiaomi.zhibo.crawler.pb.PbFeedsStatStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author : zhongxiankui
 * @time : 2016-07,04 11:46:上午11:46
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */
@Service
public class MistoreDao {
    private static final Logger logger = LoggerFactory.getLogger(MistoreDao.class);
    
    private IMClient mistore = MistoreClientFactory.getMistoreClient("zhibo-crawler");

    public static final String CRAWLER_FEED_KEY = "STAT_";

    public PbFeedsStatStore.FeedsStatStore getFeedsStatStore(String key){
        if(null == key || key.isEmpty()){
            logger.error("empty key:{}", key);
            return null;
        }

        logger.debug("key:{},mistoreScheduleClient:{}",key,mistore);
       PbFeedsStatStore.FeedsStatStore value = mistore.get(key, PbFeedsStatStore.FeedsStatStore.PARSER);
        if(null != value){
            logger.debug("value:{}", value);
            return value;
        }

        logger.error("empty value:{}", value);
        return null;
    }

    public boolean set(String key, GeneratedMessage value){
        logger.debug("empty key:{}, value:{}", key, value);
        if(null == key || key.isEmpty()){
            logger.error("empty key:{}, value:{}", key, value);
            return false;
        }

        if(null == value){
            logger.error("empty value:{}", value);
            return false;
        }

        return mistore.set(key, value);
    }
}
