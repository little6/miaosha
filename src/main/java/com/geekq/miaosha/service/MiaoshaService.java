package com.geekq.miaosha.service;

import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.OrderInfo;
import com.geekq.miaosha.redis.MiaoshaKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.utils.MD5Utils;
import com.geekq.miaosha.utils.UUIDUtil;
import com.geekq.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 秒杀服务
 */
@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	@Autowired
	RedisService redisService;

	/**
	 * 秒杀(添加事务)
	 * @param user
	 * @param goods
	 * @return
	 */
	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		boolean success = goodsService.reduceStock(goods);
		if(success){
			//减库存成功，生成订单
			return orderService.createOrder(user,goods) ;
		}else {
			//如果库存不存在则内存标记为true
			setGoodsOver(goods.getId());
			return null;
		}
	}


	/**
	 * 获取秒杀结果
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1;
			}else {
				return 0;
			}
		}
	}

	/**
	 * 在redis缓存中
	 * 秒杀标识设置为：秒杀结束
	 *
	 * @param goodsId
	 */
	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}

	/**
	 * 在redis缓存中
	 * 查询给定的商品是否秒杀结束
	 * @param goodsId
	 * @return
	 */
	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}

	/**
	 * 验证商品的秒杀url(缓存中的path和用户传递的url比较)
	 * @param user
	 * @param goodsId
	 * @param path
	 * @return
	 */
	public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
		if(user == null || path == null) {
			return false;
		}
		String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, ""+user.getNickname() + "_"+ goodsId, String.class);
		return path.equals(pathOld);
	}

	/**
	 * 创建秒杀url地址(创建完后，放入redis缓存)
	 * @param user
	 * @param goodsId
	 * @return
	 */
	public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
		if(user == null || goodsId <=0) {
			return null;
		}
		String str = MD5Utils.md5(UUIDUtil.uuid()+"123456");
		redisService.set(MiaoshaKey.getMiaoshaPath, ""+user.getNickname() + "_"+ goodsId, str);
		return str;
	}

	/**
	 *  生成秒杀验证码
	 * @param user
	 * @param goodsId
	 * @return
	 */
	public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
		if(user == null || goodsId <=0) {
			return null;
		}
		int width = 80;
		int height = 32;
		//create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = calc(verifyCode);
		redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname()+","+goodsId, rnd);
		//输出图片
		return image;
	}

	/**
	 * 注册时用的验证码
	 * @param verifyCode
	 * @return
	 */
	public boolean checkVerifyCodeRegister(int verifyCode) {
		Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCodeRegister,"regitser", Integer.class);
		if(codeOld == null || codeOld - verifyCode != 0 ) {
			return false;
		}
		redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, "regitser");
		return true;
	}


	public BufferedImage createVerifyCodeRegister() {
		int width = 80;
		int height = 32;
		//create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = calc(verifyCode);
		redisService.set(MiaoshaKey.getMiaoshaVerifyCodeRegister,"regitser",rnd);
		//输出图片
		return image;
	}

	/**
	 * 计算公式结果
	 * 使用scriptEngine
	 * @param exp
	 * @return
	 */
	private static int calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			Integer catch1 = (Integer)engine.eval(exp);
			return catch1.intValue();
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 校验秒杀验证码
	 * @param user
	 * @param goodsId
	 * @param verifyCode
	 * @return
	 */
	public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
		if(user == null || goodsId <=0) {
			return false;
		}
		Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname()+","+goodsId, Integer.class);
		if(codeOld == null || codeOld - verifyCode != 0 ) {
			return false;
		}
		redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname()+","+goodsId);
		return true;
	}

	/**
	 * 公式操作符
	 */
	private static char[] ops = new char[] {'+', '-', '*'};
	/**
	 * + - *
	 * 生成公式
	 * */
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
		int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = ""+ num1 + op1 + num2 + op2 + num3;
		return exp;
	}
	
}
