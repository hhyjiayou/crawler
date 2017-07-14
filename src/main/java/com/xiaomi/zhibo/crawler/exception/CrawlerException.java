package com.xiaomi.zhibo.crawler.exception;

/**
 * Created by orion on 15/5/25.
 */
public class CrawlerException extends Exception {
    private int errorCode;
    public CrawlerException(int errorCode) {
        super(String.format(">> push error. error code:%d.", errorCode));
        this.errorCode = errorCode;
    }

    public CrawlerException(int errorCode, String errorMessage) {
        super(String.format(">> push error. error code:%d, reason:%s", errorCode, errorMessage));
        this.errorCode = errorCode;
    }

    public CrawlerException(int errorCode, Throwable t) {
        super(String.format(">> push error, error code:%d.", errorCode), t);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
