package com.geekq.miaosha.vo;

import com.geekq.miaosha.domain.MiaoshaUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//商品详情页的传值对象
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailVo {
	private int miaoshaStatus = 0;
	private int remainSeconds = 0;
	private GoodsVo goods ;
	private MiaoshaUser user;

}
