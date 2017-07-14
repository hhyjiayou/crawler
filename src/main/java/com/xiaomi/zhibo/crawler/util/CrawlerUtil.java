package com.xiaomi.zhibo.crawler.util;

import com.xiaomi.zhibo.crawler.entity.CrawlerZkConf;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * @author : zhongxiankui
 * @time : 2016-07,11 17:00:下午5:00
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */
public class CrawlerUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlerUtil.class);
    private static final String APP_VIDEO_ADDR_FORMAT = "walilive://room/join?liveid=%s&playerid=%d";
    private static final String DEFAULT_PLAYBACK_TIME = "00:30";
    private static final String SINA_TIME = "time";
    private static final String EAST_TIME = "createTime";
    private static final String IFENG_TIME = "create_time";
    private static final String TECENT_TIME = "timeDifference";
    private static final String SOHU_TIME = "create_time";
    private static final String TOUTIAO_TIME = "create_time";
    
    public static String getAppVideoAddr(String roomId, long userId){
        return String.format(APP_VIDEO_ADDR_FORMAT, roomId, userId);
    }
    
    public static String convertMin2Hour(String minTime){
        try {
            int time = Integer.parseInt(minTime);
            int hour = time / 60;
            int min = time % 60;
            if(hour > 24){
                return DEFAULT_PLAYBACK_TIME;
            }else if(hour > 9){
                return min > 9 ? hour + ":" + min : hour + ":0" + min;
            }else {
                return min > 9 ? "0" + hour + ":" + min : "0" + hour + ":0" + min;
            }
    
        }catch (Exception e){
            logger.error("convert error:{}", e);
            return DEFAULT_PLAYBACK_TIME;
        }
    }
    
    public static long randomByRange(long start, long end){
        if(start == end){
            logger.error("start:{} bigger than end:{}!", start, end);
            return start;
        }else if(start > end){
            logger.error("exchange start:{], end:{}", start, end);
            long temp = start;
            start = end;
            end =temp;
            logger.error("after exchange start:{], end:{}", start, end);
        }
        
        return start + (long)(Math.random() * (end - start + 1));
    }
    
    public static long getRobotId(){
        List<CrawlerZkConf.RobotUser> robotUserList = ZkUtil.getCrawlerZkConfig().getRobotUsers();
        if(null == robotUserList || 0 == robotUserList.size()){
            logger.error("empty robotUserList:{}", robotUserList);
            return 400001L;
        }
        
        int size = robotUserList.size();
        int index = (int)randomByRange(0, size - 1);
        if(0 > index || index >= size){
            logger.error("error index:{}", index);
            return 400001L;
        }
    
        CrawlerZkConf.RobotUser robotUser = robotUserList.get(index);
        
        if(null == robotUser){
            logger.error("error robotUser:{}", robotUser);
            return 400001L;
        }
     
        if(0 >= robotUser.start || 0 >= robotUser.end){
            logger.error("error start:{}, end:{}", robotUser.start, robotUser.end);
            return 400001L;
        }
        
        return randomByRange(robotUser.start ,robotUser.end);
    }

    public static class SortBySINA implements Comparator {
        public int compare(Object o1, Object o2) {
            JSONObject s1 = (JSONObject) o1;
            JSONObject s2 = (JSONObject) o2;

            try {
                String time1 = (String)s1.get(SINA_TIME);
                String time2 = (String)s2.get(SINA_TIME);
                if (0 < time1.compareTo(time2)){
                    return -1;
                } else if (0 > time1.compareTo(time2)) {
                    return 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    public static class SortByIfeng implements Comparator {
        public int compare(Object o1, Object o2) {
            JSONObject s1 = (JSONObject) o1;
            JSONObject s2 = (JSONObject) o2;

            try {
                String time1 = (String)s1.get(IFENG_TIME);
                String time2 = (String)s2.get(IFENG_TIME);
                if (0 < time1.compareTo(time2)){
                    return -1;
                } else if (0 > time1.compareTo(time2)) {
                    return 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    public static class SortBySohu implements Comparator {
        public int compare(Object o1, Object o2) {
            JSONObject s1 = (JSONObject) o1;
            JSONObject s2 = (JSONObject) o2;

            try {
                Long time1 = (Long)s1.get(SOHU_TIME);
                Long time2 = (Long)s2.get(SOHU_TIME);
                if(time1 > time2){
                    return -1;
                }else if(time1 < time2){
                    return 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    public static class SortByToutiao implements Comparator {
        public int compare(Object o1, Object o2) {
            JSONObject s1 = (JSONObject) o1;
            JSONObject s2 = (JSONObject) o2;

            try {
                Integer time1 = (Integer)s1.get(TOUTIAO_TIME);
                Integer time2 = (Integer)s2.get(TOUTIAO_TIME);
                if(time1 > time2){
                    return -1;
                }else if(time1 < time2){
                    return 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    public static class SortByEast implements Comparator {
        public int compare(Object o1, Object o2) {
            JSONObject s1 = (JSONObject) o1;
            JSONObject s2 = (JSONObject) o2;

            try {
                String time1 = (String)s1.get(EAST_TIME);
                String time2 = (String)s2.get(EAST_TIME);
                if (0 < time1.compareTo(time2)){
                    return -1;
                } else if (0 > time1.compareTo(time2)) {
                    return 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    public static class SortByTecent implements Comparator {
        public int compare(Object o1, Object o2) {
            JSONObject s1 = (JSONObject) o1;
            JSONObject s2 = (JSONObject) o2;

            try {
                String time1 = (String)s1.get(TECENT_TIME);
                String time2 = (String)s2.get(TECENT_TIME);
                if (0 < time1.compareTo(time2)){
                    return -1;
                } else if (0 > time1.compareTo(time2)) {
                    return 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }


}
