package com.xiaomi.zhibo.crawler.Timer;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hhy on 16-8-25.
 */
public class CrawlerTimer extends Timer{
    private static final Logger logger = LoggerFactory.getLogger(CrawlerTimer.class);

    private CrawlerTimer(){}
    private static CrawlerTimer crawlerTimer = null;
    public static CrawlerTimer getInstance(){
        if(null == crawlerTimer){
            crawlerTimer = new CrawlerTimer();
            crawlerTimer.schedule(new CrawlerTask(), 1000, 2000);
        }
        return crawlerTimer;
    }
    public static class CrawlerTask extends TimerTask{
        @Override
        public void run(){
            logger.debug("Timer run");
        }
    }
}
