package com.xiaomi.zhibo.crawler.controllers;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.constant.ErrorCode;
import com.xiaomi.zhibo.crawler.executor.ExecutorFactory;
import com.xiaomi.zhibo.crawler.service.WebCrawlService;
import net.paoding.rose.web.Invocation;
import net.paoding.rose.web.annotation.Param;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;
import net.paoding.rose.web.annotation.rest.Post;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Path("/crawler")
public class WebCrawlerController {
    private final static Logger logger = LoggerFactory.getLogger(WebCrawlerController.class);


    private static final String indexSkipUrl = "/crawler/views/resource_man/index.jsp";

    @Autowired
    WebCrawlService webCrawlService;

    @Get("/api")
    @Post("/api")
    public String getContentByApi(Invocation inv, @Param("netType") Integer netType, @Param("feedId") String feedId,
                                  @Param("crawlerUrl") String crawlerUrl, @Param("postBarType") List<Integer> postBarTypeList,
                                  @Param("start") Integer start, @Param("limit") Integer limit) {
        
        logger.info("netType:{}, feedId:{}, crawlerUrl:{}, postBarTypeList:{}, start:{}, limit:{}",
                netType, feedId, crawlerUrl, postBarTypeList, start, limit);

        String resp = webCrawlService.getPostBarByApi(netType, feedId, crawlerUrl, postBarTypeList, start, limit);
        
        logger.info("resp:{}", resp);
        
        return "@json:" + resp;
    }

    @Get("/add_postbar")
    @Post("/add_postbar")
    public String addCommentApi(Invocation inv, @Param("feedOwnerId") String feedOwnerId, @Param("feedId") String feedId, @Param("content") String content){

        logger.info("feedOwnerId:{}, feedId:{}, content:{}", feedOwnerId, feedId, content);
        String resp = webCrawlService.publishPostBar(feedOwnerId, feedId, content, true);
        logger.info("resp:{}", resp);

        return "@json:" + resp;
    }

    @Get("/add/index")
    @Post("/add/index")
    public String addIndex(){
        logger.info("indexSkipUrl:{}", indexSkipUrl);
        return indexSkipUrl;
    }

    @Get("/add_allpostbar")
    @Post("/add_allpostbar")
    public String addAllCommentApi(Invocation inv, @Param("feedOwnerId") final String feedOwnerId, @Param("feedId") final String feedId,
                                   @Param("crawlerUrl") final String crawlerUrl, @Param("postBarType") final List<Integer> postBarTypeList, @Param("netType") final Integer netType){
        logger.info("feedId:{}, feedOwnereId:{}, crawlerUrl:{}, netType:{}, postBarTypeList:{}", feedId, feedOwnerId, crawlerUrl, netType, postBarTypeList);

        JSONObject json = new JSONObject();
        ErrorCode errorCode = ErrorCode.OK;
        try {
            json.put(Constant.KEY_CODE, errorCode.getValue());
            json.put(Constant.KEY_MSG, "评论抓取发布异步进行中，请稍后验证！");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //异步操作
        ExecutorFactory.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                webCrawlService.getAllPostBarByApi(netType, feedId, crawlerUrl, postBarTypeList, feedOwnerId);
            }
        });

        return "@json:" + json.toString();
    }

    @Get("/load_feedComments")
    @Post("/load_feedComments")
    public String loadFeedCommentsApi(Invocation inv, @Param("feedId") String feedId, @Param("start") Integer start, @Param("limit") Integer limit){

        logger.info("feedId:{}, start:{}, limit:{}", feedId, start, limit);
        String resp = webCrawlService.loadFeedComments(feedId, start, limit);
        return "@json:" + resp;

    }

    @Get("/sticky_feedComments")
    @Post("/sticky_feedComments")
    public String stickyFeedCommentsApi(Invocation inv, @Param("feedOwnerId") String feedOwnerId, @Param("feedId") String feedId, @Param("commentId") Long commentId){
        logger.info("feedId:{}, feedOwnerId:{}, commentId:{}", feedId,feedOwnerId, commentId);
        String resp = webCrawlService.stickyFeedComments(feedId, feedOwnerId, commentId);
        return "@json:" + resp;
    }

    @Get("/del_feedComments")
    @Post("/del_feedComments")
    public String delFeedCommentsApi(Invocation inv, @Param("feedId") String feedId, @Param("commentIds") List<Long> commentIds){
        logger.info("feedId:{}, commentIds:{}", feedId, commentIds);
        String resp = webCrawlService.delFeedComments(feedId, commentIds);
        return "@json:" + resp;
    }

}