package com.geekq.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.controller.LoginController;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.MiaoShaUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

import static com.geekq.miaosha.common.enums.ResultStatus.ACCESS_LIMIT_REACHED;
import static com.geekq.miaosha.common.enums.ResultStatus.SESSION_ERROR;

/**
 * 防刷限流拦截器
 */
@Service
public class AccessInterceptor  extends HandlerInterceptorAdapter{

	private static Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

	@Autowired
	MiaoShaUserService userService;

	@Autowired
	RedisService redisService;

	/**
	 * 方法执行前，进行访问次数限制
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/**
		 * 获取调用 获取主要方法
		 */
		if(handler instanceof HandlerMethod) {
			logger.info("打印拦截方法handler ：{} ",handler);
			HandlerMethod hm = (HandlerMethod)handler;
			//方便mybatis 测试
//			if(hm.getMethod().getName().startsWith("test")){
//				return true;
//			}
			MiaoshaUser user = getUser(request, response);
			//将当前用户放入threadLocal中
			UserContext.setUser(user);
			//获取方法上的注解
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if(accessLimit == null) {
				//没有限制
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			//获取请求url
			String key = request.getRequestURI();
			if(needLogin) {
				if(user == null) {
					render(response, SESSION_ERROR);
					return false;
				}
				key += "_" + user.getNickname();
			}else {
				//do nothing
			}
			//动态设置key的有效期
			AccessKey ak = AccessKey.withExpire(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
	    	if(count  == null) {
	    		//初始化访问次数1
	    		 redisService.set(ak, key, 1);
	    	}else if(count < maxCount) {
	    		//访问次数+1
	    		 redisService.incr(ak, key);
	    	}else {
	    		//超出访问次数
	    		render(response, ACCESS_LIMIT_REACHED);
	    		return false;
	    	}
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		//从threadlocal中移除当前用户
		UserContext.removeUser();
	}

	/**
	 * 响应错误json数据
	 * @param response
	 * @param cm
	 * @throws Exception
	 */
	private void render(HttpServletResponse response, ResultStatus cm)throws Exception {
		//指定输出的编码格式，防止乱码
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str  = JSON.toJSONString(ResultGeekQ.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	/**
	 * 获取用户信息(从cookie中)
	 * @param request
	 * @param response
	 * @return
	 */
	private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(MiaoShaUserService.COOKIE_NAME_TOKEN);
		String cookieToken = getCookieValue(request, MiaoShaUserService.COOKIE_NAME_TOKEN);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return userService.getByToken(response, token);
	}

	private String getCookieValue(HttpServletRequest request, String cookiName) {
		Cookie[]  cookies = request.getCookies();
		if(cookies == null || cookies.length <= 0){
			return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookiName)) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
