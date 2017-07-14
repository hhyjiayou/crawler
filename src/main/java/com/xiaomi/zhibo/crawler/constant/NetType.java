package com.xiaomi.zhibo.crawler.constant;

/**
 * @author : zhongxiankui
 * @time : 2016-08,19 11:11:上午11:11
 * @mail : zhongxiankui@xiaomi.com
 * @project zhibo-crawler
 */
public enum NetType {
    
    UNKNOWN(0, "unknown"),
    NET_EASE(1, "net_ease"),
    SINA(2, "sina"),
    IFENG(3, "ifeng"),
    SOHU(4, "sohu"),
    TECENT(5, "tecent"),
    TOUTIAO(6, "toutiao"),
    WEIBO(7, "weibo");
    
    private Integer id;
    private String name;
    private NetType(Integer id, String name){
       this.id = id;
        this.name = name;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public static NetType getNetType(int id){
        NetType[] netTypes = NetType.class.getEnumConstants();
        if(id >= netTypes.length || 0 >= id){
            return UNKNOWN;
        }else {
            return netTypes[id];
        }
    }

    public static NetType getNetType(String url){
        int id = NetType.UNKNOWN.getId();
        if(url.contains(".163.com")){
            id = NetType.NET_EASE.getId();
        }else if(url.contains(".sina.com")){
            id = NetType.SINA.getId();
        }else if(url.contains(".ifeng.com")){
            id = NetType.IFENG.getId();
        }else if(url.contains("sohu.com")){
            id = NetType.SOHU.getId();
        }else if(url.contains("qq.com")){
            id = NetType.TECENT.getId();
        }else if(url.contains("toutiao.com")){
            id = NetType.TOUTIAO.getId();
        }else if(url.contains("weibo.com")){
            id = NetType.WEIBO.getId();
        }
        NetType[] netTypes = NetType.class.getEnumConstants();
        if(id >= netTypes.length || 0 >= id){
            return UNKNOWN;
        }else {
            return netTypes[id];
        }
    }
}
