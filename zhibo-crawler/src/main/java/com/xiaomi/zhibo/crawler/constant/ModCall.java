package com.xiaomi.zhibo.crawler.constant;

/**
 * @author : zhongxiankui
 * @time : 2016-07,06 11:13:上午11:13
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */
public interface ModCall {
    

    //self modcall id/
    public static final int COMMENT_C2S_MODID = 1201090000;
    
    //rpc client conf//
    public static final String BLINK_COMMENT_CATALOG = "zhibo.crawler.comment";
    public static final int BLINK_COMMENT_HEADER_CMDID = 1004;
    public static final int COMMENT_L5ID = 1004;
    public static final int COMMENT_MODID = 1200680000;
    public static final int COMMENT_CMDID = 1200680004;

    public static final int BLINK_COMMENTNUM_HEADER_CMDID = 1001;
    public static final int COMMENTNUM_L5ID = 1001;
    public static final int COMMENTNUM_CMDID = 1200680001;



    public static final int BLINK_DELCMNT_HEADER_CMDID = 1005;
    public static final int DELCMNT_L5ID = 1005;
    public static final int DELCMNT_CMDID = 1200680005;



    //user
    public static final String BLINK_USER_CATALOG = "act.olympic.user";
    public static final int BLINK_USER_INFO_ONLINE_HEADER_CMDID = 10002;
    public static final int USER_INFO_ONLINE_L5ID = 1002;
    public static final int USER_MODID = 1200020000;
    public static final int USER_INFO_ONLINE_CMDID = 1200020002;

}
