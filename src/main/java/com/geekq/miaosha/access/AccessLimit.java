package com.geekq.miaosha.access;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 秒杀防刷限流注解
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
	//多少秒之内
	int seconds();
	//最大访问次数
	int maxCount();
	//是否需要登录
	boolean needLogin() default true;
}
