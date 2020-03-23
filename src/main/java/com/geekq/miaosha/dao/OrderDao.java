package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单dao
 */
@Mapper
public interface OrderDao {

	//根据用户id和商品id，查询秒杀订单
	@Select("select * from miaosha_order where user_id=#{userNickName} and goods_id=#{goodsId}")
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userNickName") long userNickName, @Param("goodsId") long goodsId);

	//添加订单信息+返回订单id(因为id是自增的)
	@Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
			+ "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
	@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
	public long insert(OrderInfo orderInfo);

	//添加秒杀订单
	@Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
	public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

	//根据订单id查询订单信息
	@Select("select * from order_info where id = #{orderId}")
	public OrderInfo getOrderById(@Param("orderId")long orderId);

	//根据创建时间和订单状态，查询订单信息
	@Select("select * from order_info where status=#{status} and create_Date<=#{createDate}")
	public List<OrderInfo> selectOrderStatusByCreateTime(@Param("status")Integer status, @Param("createDate") String createDate);

	//根据订单id更新订单状态
	@Select("update order_info set status=0 where id=#{id}")
	public int closeOrderByOrderInfo();
}
