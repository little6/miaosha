package com.geekq.miaosha.utils;

import java.util.UUID;

//uuid生成工具类
public class UUIDUtil {

    //去除uuid中间的-
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
