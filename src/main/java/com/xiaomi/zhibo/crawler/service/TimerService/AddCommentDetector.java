package com.xiaomi.zhibo.crawler.service.TimerService;

import com.xiaomi.zhibo.crawler.cache.AddCommentQueue;
import com.xiaomi.zhibo.crawler.pb.PbComment;
import com.xiaomi.zhibo.crawler.rpc.CommentService;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hhy on 16-8-25.
 */
@Service
public class AddCommentDetector implements InitializingBean{

    @Autowired
    CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(AddCommentDetector.class);
    private volatile boolean running = false;


    public void execute(){
        logger.info("AddCommentDetector begin");
        if(running){
            logger.info("AddCommentDetector is running");
            return;
        }
        running = true;

        try {
            int size = AddCommentQueue.getSize();
            if(0 == size){
                running = false;
                return;
            }

            List<PbComment.MultiCreateFeedCommentsReq> commentList = AddCommentQueue.poll(size);

            for(PbComment.MultiCreateFeedCommentsReq commentReq : commentList){

                PbComment.MultiCreateFeedCommentsReq.Builder addQueueBuilder = PbComment.MultiCreateFeedCommentsReq.newBuilder();
                PbComment.MultiCreateFeedCommentsReq.Builder sendQueueBuilder = PbComment.MultiCreateFeedCommentsReq.newBuilder();
                List<PbComment.Comment> comments = commentReq.getCommentsList();
                if(null == comments || !commentReq.hasFeedId() || 0 >= commentReq.getCommentsCount()){
                    logger.error("null comments:{}", comments);
                    continue;
                }

                String feedId = commentReq.getFeedId();
                addQueueBuilder.setFeedId(feedId);
                sendQueueBuilder.setFeedId(feedId);

                for(PbComment.Comment commentTmp : comments){
                    if(commentTmp.hasCreateTime()){
                        Long createTime = commentTmp.getCreateTime();
                        if(System.currentTimeMillis() <= createTime){
                            addQueueBuilder.addComments(commentTmp);
                        }else {
                            commentTmp = commentTmp.toBuilder().setCreateTime(System.currentTimeMillis()).build();
                            sendQueueBuilder.addComments(commentTmp);
                        }
                    }
                }
                if(0 < addQueueBuilder.build().getCommentsCount()){
                    AddCommentQueue.addComment(addQueueBuilder.build());
                }

                if(0 < sendQueueBuilder.build().getCommentsCount()){
                    commentService.getCommentReq(sendQueueBuilder.build());
                }
            }
        } catch (Throwable e) {
            logger.error("AddCommentDetector error occur", e);
        }
        running = false;
        logger.info("WelcomSendDetector end");
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        Runtime.getRuntime().addShutdownHook(new Thread(){
           @Override
           public void run() {
               int i = 0;
               int maxTimes = 3;
               try {
                   Thread.currentThread().setName("WelcomSendDetector_shutdownHook");
                   while (running && i++ < maxTimes) {
                       Thread.sleep(100);
                   }
               } catch (Exception e) {
                   logger.error("Meet exception during shutdown", e);
               }
               if (i < maxTimes) {
                   logger.info("WelcomSendDetector exit graceful");
               } else {
                   logger.info("WelcomSendDetector exit not graceful");
               }
           }
        });
    }

}
