package com.geekq.miaosha.rabbitmq;

import com.geekq.miaosha.domain.MiaoshaUser;

/**
 * 秒杀消息封装类
 */
public class MiaoshaMessage {
	//用户
	private MiaoshaUser user;
	//秒杀商品id
	private long goodsId;
	public MiaoshaUser getUser() {
		return user;
	}
	public void setUser(MiaoshaUser user) {
		this.user = user;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
}
