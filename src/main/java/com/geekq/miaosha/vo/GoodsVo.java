package com.geekq.miaosha.vo;

import com.geekq.miaosha.domain.Goods;
import lombok.*;

import java.util.Date;

//商品信息+秒杀商品信息封装类
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {
	private Double miaoshaPrice;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;
}
