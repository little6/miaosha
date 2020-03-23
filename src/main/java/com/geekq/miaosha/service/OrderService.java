package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.OrderDao;
import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.OrderInfo;
import com.geekq.miaosha.redis.OrderKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.utils.DateTimeUtils;
import com.geekq.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.geekq.miaosha.common.Constanst.orderStaus.ORDER_NOT_PAY;

//订单service
@Service
public class OrderService {
	
	@Autowired
	OrderDao orderDao;

	@Autowired
	private RedisService redisService ;

	/**
	 * 根据用户id 商品id，查询秒杀订单信息(查询缓存系统)
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		return	redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userId+"_"+goodsId,MiaoshaOrder.class) ;
	}

	/**
	 * 根据订单id，查询秒杀订单信息
	 * @param orderId
	 * @return
	 */
	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}

	/**
	 * 创建订单
	 * @param user
	 * @param goods
	 * @return
	 */
	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
		OrderInfo orderInfo = new OrderInfo();
		//订单创建时间
		orderInfo.setCreateDate(new Date());
		//收件人地址id
		orderInfo.setDeliveryAddrId(0L);
		//商品数量
		orderInfo.setGoodsCount(1);
		//商品第
		orderInfo.setGoodsId(goods.getId());
		//商品名称
		orderInfo.setGoodsName(goods.getGoodsName());
		//商品价格(秒杀价格)
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		//订单来源渠道
		orderInfo.setOrderChannel(1);
		//订单状态
		orderInfo.setStatus(0);
		//用户id
		orderInfo.setUserId(Long.valueOf(user.getNickname()));
		orderDao.insert(orderInfo);
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		//商品id
		miaoshaOrder.setGoodsId(goods.getId());
		//订单id
		miaoshaOrder.setOrderId(orderInfo.getId());
		//用户id
		miaoshaOrder.setUserId(Long.valueOf(user.getNickname()));
		//插入秒杀订单到db
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		//将秒杀订单写入redis缓存(防止重复秒杀)
		redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+user.getNickname()+"_"+goods.getId(),miaoshaOrder) ;
		return orderInfo;
	}

	/**
	 * 关闭订单
	 * @param hour
	 */
	public void closeOrder(int hour){
		Date closeDateTime = DateUtils.addHours(new Date(),-hour);
		List<OrderInfo> orderInfoList = orderDao.selectOrderStatusByCreateTime(Integer.valueOf(ORDER_NOT_PAY.ordinal()), DateTimeUtils.dateToStr(closeDateTime));
		for (OrderInfo orderInfo:orderInfoList){
			System.out.println("orderinfo  infomation "+orderInfo.getGoodsName());
		}
	}

	
}
