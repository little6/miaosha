package com.geekq.miaosha.redis;

public class MiaoShaUserKey extends  BasePrefix{
    //token失效时间：2天
    public static final int TOKEN_EXPIRE = 3600 *24*2;
    //秒杀用户token
    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE,"tk") ;
    public static MiaoShaUserKey getByNickName = new MiaoShaUserKey(0, "nickName");

    public MiaoShaUserKey(int expireSeconds ,String prefix) {
        super(expireSeconds,prefix);
    }
}
