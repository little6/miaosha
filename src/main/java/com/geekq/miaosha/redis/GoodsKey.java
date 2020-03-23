package com.geekq.miaosha.redis;

//redis中商品模块key的前缀
public class GoodsKey extends BasePrefix{

	private GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	//商品列表key前缀
	public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
	//商品详情页key前缀
	public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
	//秒杀商品库存key前缀
	public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0, "gs");

}
