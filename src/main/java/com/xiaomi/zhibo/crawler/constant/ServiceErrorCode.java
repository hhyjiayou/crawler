package com.xiaomi.zhibo.crawler.constant;

/**
 * @author : zhongxiankui
 * @time : 2016-07,06 10:57:上午10:57
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-olympic
 */
public enum ServiceErrorCode {
    OK(0, "成功"),
    COMMENT(1, "COMMENT服务错误"),
    USER(2, "USER服务错误"),
    CMNT_NUM(3,"CMNT_NUM服务错误"),
    CMNT_DEL(4, "CMNT_DEL服务错误");
    
    private int value;
    private String msg;
    
    
    public int getValue() {
        return value;
    }
    
    public String getMsg() {
        return msg;
    }
    
    private ServiceErrorCode(int value, String msg){
        this.value = value;
        this.msg = msg;
    }
}
