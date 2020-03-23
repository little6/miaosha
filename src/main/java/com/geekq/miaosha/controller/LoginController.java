package com.geekq.miaosha.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.redis.redismanager.RedisLua;
import com.geekq.miaosha.service.MiaoShaUserService;
import com.geekq.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.geekq.miaosha.common.Constanst.COUNTLOGIN;

//登录控制器
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MiaoShaUserService userService;

    /**
     * 登录页面
     * @param loginVo
     * @param model
     * @return
     */
    @Reference
    @RequestMapping("/to_login")
    public String tologin(LoginVo loginVo, Model model) {
        logger.info(loginVo.toString());
        //未完成
          RedisLua.vistorCount(COUNTLOGIN);
        String count = RedisLua.getVistorCount(COUNTLOGIN).toString();
        logger.info("访问网站的次数为:{}",count);
        model.addAttribute("count",count);
        return "login";
    }

    /**
     * 登录
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public ResultGeekQ<Boolean> dologin(HttpServletResponse response, @Valid LoginVo loginVo) {
        ResultGeekQ<Boolean> result = ResultGeekQ.build();
        logger.info(loginVo.toString());
        userService.login(response, loginVo);
        return result;
    }


    /**
     * 生成token的测试方法
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/create_token")
    @ResponseBody
    public String createToken(HttpServletResponse response, @Valid LoginVo loginVo) {
        logger.info(loginVo.toString());
        String token = userService.createToken(response, loginVo);
        return token;
    }
}
