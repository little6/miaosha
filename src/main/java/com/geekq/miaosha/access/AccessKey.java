package com.geekq.miaosha.access;

import com.geekq.miaosha.redis.BasePrefix;

/**
 * 防刷限流key
 */
public class AccessKey extends BasePrefix {

	private AccessKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
	
}
