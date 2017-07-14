package com.xiaomi.zhibo.crawler.entity;

import java.util.List;

/**
 * @author : zhongxiankui
 * @time : 2016-07,06 12:15:下午12:15
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */
public class CrawlerZkConf {
    private int delayTime = 0;
    private int maxCmntLimit = 0;
    private int minCmntLeft = 0;

    
    public List<RobotUser> getRobotUsers() {
        return robotUsers;
    }
    
    public void setRobotUsers(List<RobotUser> robotUsers) {
        this.robotUsers = robotUsers;
    }
    
    private List<RobotUser> robotUsers;

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getMaxCmntLimit() {
        return maxCmntLimit;
    }

    public void setMaxCmntLimit(int maxCmntLimit) {
        this.maxCmntLimit = maxCmntLimit;
    }

    public int getMinCmntLeft() {
        return minCmntLeft;
    }

    public void setMinCmntLeft(int minCmntLeft) {
        this.minCmntLeft = minCmntLeft;
    }

    public static class RobotUser{
        public long start;
        public long end;
        
        public RobotUser(long start, long end){
            this.start = start;
            this.end = end;
        }
    }
}
