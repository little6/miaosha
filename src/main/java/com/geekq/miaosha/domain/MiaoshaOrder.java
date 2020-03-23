package com.geekq.miaosha.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//秒杀订单信息
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MiaoshaOrder {
	private Long id;
	private Long userId;
	private Long  orderId;
	private Long goodsId;
}
