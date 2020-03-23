package com.geekq.miaosha.redis;

public class MiaoshaKey extends BasePrefix{

	private MiaoshaKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	/**
	 * 秒杀是否结束的标识key
	 */
	public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
	/**
	 * 秒杀url缓存key前缀：过期时间60秒
	 */
	public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
	public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");
	public static MiaoshaKey getMiaoshaVerifyCodeRegister = new MiaoshaKey(300, "register");

}
