package com.xiaomi.zhibo.crawler.cache;

import com.xiaomi.zhibo.crawler.pb.PbComment;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by hhy on 16-8-25.
 */
@Service
public class AddCommentQueue {

    private static final Logger logger = LoggerFactory.getLogger(AddCommentQueue.class);
    private static ConcurrentLinkedQueue<PbComment.MultiCreateFeedCommentsReq> queue = new ConcurrentLinkedQueue<PbComment.MultiCreateFeedCommentsReq>();

    public static boolean addComment(PbComment.MultiCreateFeedCommentsReq comment){
        if(null == comment){
            logger.error("null comment:{}", comment);
            return false;
        }
        return queue.add(comment);
    }

    public static int getSize(){ return queue.size(); }

    public static List<PbComment.MultiCreateFeedCommentsReq> poll(int size){
        List<PbComment.MultiCreateFeedCommentsReq> result = new ArrayList<PbComment.MultiCreateFeedCommentsReq>();
        for(int i=0; i < size && !queue.isEmpty(); i++){
            PbComment.MultiCreateFeedCommentsReq comment= queue.poll();
            if(null != comment){
                result.add(comment);
            }
        }
        return result;
    }
}
