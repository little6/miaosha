package com.geekq.miaosha.controller;

import com.geekq.miaosha.redis.KeyPrefix;
import com.geekq.miaosha.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Controller
public class BaseController {


    //加一个配置项：是否开启页面缓存
    @Value("#{'${pageCache.enbale}'}")
    private boolean pageCacheEnable;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    RedisService redisService;


    /**
     * 手动渲染html，响应客户端，设置页面缓存
     * @param request
     * @param response
     * @param model
     * @param tplName
     * @param prefix
     * @param key
     * @return
     */
    public String render(HttpServletRequest request, HttpServletResponse response, Model model, String tplName, KeyPrefix prefix, String key) {
        if(!pageCacheEnable) {
            return tplName;
        }
        //1、取缓存
        String html = redisService.get(prefix, key, String.class);
        if(!StringUtils.isEmpty(html)) {
            out(response, html);
            return null;
        }
        //2、没有缓存，手动渲染
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(tplName, ctx);
        if(!StringUtils.isEmpty(html)) {
            //3、手动设置缓存
            redisService.set(prefix, key, html);
        }
        out(response, html);
        return null;
    }

    /**
     * 输出响应html
     */
    public static void out(HttpServletResponse res, String html){
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        try{
            OutputStream out = res.getOutputStream();
            out.write(html.getBytes("UTF-8"));
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
