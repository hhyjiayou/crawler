
package com.xiaomi.zhibo.crawler.util;

import com.xiaomi.miliao.zookeeper.EnvironmentType;
import com.xiaomi.miliao.zookeeper.ZKClient;
import com.xiaomi.miliao.zookeeper.ZKDataChangeListener;
import com.xiaomi.miliao.zookeeper.ZKFacade;
import com.xiaomi.zhibo.crawler.entity.CrawlerZkConf;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ZkUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ZkUtil.class);
    
    private static volatile CrawlerZkConf crawlerZkConfig = new CrawlerZkConf();
    private static final String ZK_PATH = "/com/xiaomi/commons/xconfig/properties/zhibo-crawler.properties";
    private static final String ROBOT_ID = "robot.id";
    private static final String DELAY_TIME = "delay.time";
    private static final String MAX_CMNT_LIMIT = "maxCmnt.limit";
    private static final String MIN_CMNT_LEFT = "minCmnt.left";

    static {

        ZKClient zkclient = ZKFacade.getAbsolutePathClient();
        Properties properties = zkclient.getData(Properties.class, ZK_PATH);
        setOlympicProperties(properties);
        zkclient.registerDataChanges(Properties.class, ZK_PATH, new ZKDataChangeListener<Properties>() {
            @Override
            public void onChanged(String path, Properties data) {
                logger.info("{} changed, properties: {}", ZK_PATH, data);
                setOlympicProperties(data);
            }
        });
    }

    private static void setOlympicProperties(Properties properties) {
        Validate.notNull(properties, "properties should not null");
        logger.info("before set live-oss properties, crawlerZkConfig: {}, properties： {}", crawlerZkConfig, properties);
        CrawlerZkConf newCrawlerZkConf = new CrawlerZkConf();
        try{
            List<CrawlerZkConf.RobotUser> robotUserList = getRobotUser(properties);
            if(robotUserList != null){
                newCrawlerZkConf.setRobotUsers(robotUserList);
                newCrawlerZkConf.setDelayTime(Integer.parseInt(properties.getProperty(DELAY_TIME)));
                newCrawlerZkConf.setMaxCmntLimit(Integer.parseInt(properties.getProperty(MAX_CMNT_LIMIT)));
                newCrawlerZkConf.setMinCmntLeft(Integer.parseInt(properties.getProperty(MIN_CMNT_LEFT)));
            }
        }catch (Exception e){
            logger.error("parse error:{} !", e);
        }

        crawlerZkConfig = newCrawlerZkConf;
        logger.info("update crawlerZkConfig:{} ok!", crawlerZkConfig);
    }
    
    private static List<CrawlerZkConf.RobotUser> getRobotUser(Properties properties){
        
        String robotIds = properties.getProperty(ROBOT_ID);
        if(null != robotIds && !robotIds.isEmpty()){
            String[] robotIdArray = robotIds.split(",");
            if(null != robotIdArray && robotIdArray.length > 0){
                List<CrawlerZkConf.RobotUser> robotUserList =
                        new ArrayList<CrawlerZkConf.RobotUser>(robotIdArray.length);
                for (String robotId : robotIdArray){
                    if(null == robotId || robotId.isEmpty()){
                        logger.error("empty robotId:{}", robotId);
                        continue;
                    }
                    String[] robotArray = robotId.split(":");
                    if(2 != robotArray.length || !NumberUtils.isNumber(robotArray[0]) || 
                            !NumberUtils.isNumber(robotArray[1])){
                        logger.error("error robotId:{}", robotId);
                        continue;
                    }
                
                    robotUserList.add(new CrawlerZkConf.RobotUser(
                            Long.valueOf(robotArray[0]), Long.valueOf(robotArray[1])));
                }
                
                return robotUserList;
            }else {
                logger.error("empty robotIdArray:{}", robotIdArray);
            }
        }else {
            logger.error("empty robotIds:{}", robotIds);
        }
        
        return null;
    }

    public static CrawlerZkConf getCrawlerZkConfig() {
        return crawlerZkConfig;
    }

}
