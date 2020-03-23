package com.geekq.miaosha.access;

import com.geekq.miaosha.domain.MiaoshaUser;

/**
 * 当前登录用户存放的threadLocal,线程变量
 */
public class UserContext {
	
	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();
	
	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	}

	public static void removeUser() {
		userHolder.remove();
	}

}
