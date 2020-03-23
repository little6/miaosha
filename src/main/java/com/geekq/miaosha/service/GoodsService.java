package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.GoodsDao;
import com.geekq.miaosha.domain.MiaoshaGoods;
import com.geekq.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//商品服务
@Service
public class GoodsService {
	
	@Autowired
	GoodsDao goodsDao;


	//商品列表
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	//根据id，查询商品信息
	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	//减库存
	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int ret = goodsDao.reduceStock(g);
		return ret > 0;
	}
	
	
	
}
