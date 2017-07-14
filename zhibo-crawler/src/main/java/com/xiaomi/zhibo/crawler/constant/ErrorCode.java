package com.xiaomi.zhibo.crawler.constant;

/**
 * @author : zhongxiankui
 * @time : 2016-07,06 10:47:上午10:47
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */
public enum ErrorCode {

    OK(0, "成功"),
    PARAM_ERROR(1, "参数错误"),
    POSTBAR_ERROR(2, "获取数据失败"),
    ADDCMT_ERROR(3, "添加评论失败"),
    LOADCMT_ERROR(4,"加载评论失败"),
    CON_TIMEOUT(5, "连接超时,请重试！"),
    INEFFCT_FEEDID(6,"无效的feedId"),
    DELCMT_ERROR(7, "删除评论失败");
    
    private int value;
    private String msg;
    
    
    public int getValue() {
        return value;
    }
    
    public String getMsg() {
        return msg;
    }
    
    private ErrorCode(int value, String msg){
        this.value = value;
        this.msg = msg;
    }
}
