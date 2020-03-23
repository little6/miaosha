package com.geekq.miaosha.redis;

//redis中设置key的前缀：
public interface KeyPrefix {

    //过期时间
    public int expireSeconds() ;

    //获取key的前缀(根据模块区分)
    public String getPrefix() ;

}
