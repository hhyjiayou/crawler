package com.xiaomi.zhibo.crawler.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by hhy on 16-8-19.
 */
public class CommentJson {

    public static class Comment {
        private Long fromUid;
        private String content;
        private Long createTime;
        private Long toUid;
        private String fromNickname;
        private String toNickname;
        private Boolean isGood;



        @Override
        public String toString(){
            return ToStringBuilder.reflectionToString(this);
        }

        public Long getFromUid() {
            return fromUid;
        }

        public void setFromUid(Long fromUid) {
            this.fromUid = fromUid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public Long getToUid() {
            return toUid;
        }

        public void setToUid(Long toUid) {
            this.toUid = toUid;
        }

        public String getFromNickname() {
            return fromNickname;
        }

        public void setFromNickname(String fromNickname) {
            this.fromNickname = fromNickname;
        }

        public String getToNickname() {
            return toNickname;
        }

        public void setToNickname(String toNickname) {
            this.toNickname = toNickname;
        }

        public Boolean getGood() {
            return isGood;
        }

        public void setGood(Boolean good) {
            isGood = good;
        }
    }



}
