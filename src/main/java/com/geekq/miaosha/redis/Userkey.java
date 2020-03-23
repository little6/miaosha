package com.geekq.miaosha.redis;

public class Userkey extends BasePrefix {

    //私有方法
    private Userkey(String prefix) {
        super( prefix);
    }

    public static Userkey getById = new Userkey("id") ;

    public static Userkey getByName = new Userkey("name") ;

}
