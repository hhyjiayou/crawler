package com.xiaomi.zhibo.crawler.constant;

/**
 * @author : zhongxiankui
 * @time : 2016-08,19 12:20:下午12:20
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-crawler
 */
public enum PostBarType {
    UNKNOWN(0, "unknown"),
    HOT(1, "hot"),
    NEW(2, "new");
    
    private PostBarType(int id, String name){
        this.id = id;
        this.name = name;
    }
    private int id;
    private String name;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public static PostBarType getPostBarType(int id){
        PostBarType[] postBarTypes = PostBarType.class.getEnumConstants();
        if(id >= postBarTypes.length || 0 >= id){
            return UNKNOWN;
        }else {
            return postBarTypes[id];
        }
    }
}
