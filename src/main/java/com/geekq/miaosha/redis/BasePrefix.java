package com.geekq.miaosha.redis;

//前缀抽象类
public abstract class BasePrefix implements  KeyPrefix {

    //过期时间，0代表永不过期
    private int expireSeconds;

    //前缀名称
    private String prefix ;

    public BasePrefix(int expireSeconds ,  String prefix ){

        this.expireSeconds = expireSeconds ;
        this.prefix = prefix;
    }

    //永不过期的prefix
    public BasePrefix(String prefix) {
       this(0,prefix);
    }

    @Override
    public int expireSeconds() {//默认0代表永远过期
        return expireSeconds;
    }

    /**
     * 可确定获取唯一key
     * @return 类名+前缀
     */
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":" +prefix;
    }
}
