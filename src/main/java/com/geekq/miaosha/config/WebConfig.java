package com.geekq.miaosha.config;

import com.geekq.miaosha.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 添加自定义mvc功能
 */
@Configuration
public class WebConfig  extends WebMvcConfigurerAdapter {

    //用户参数处理器
    @Autowired
    UserArgumentResolver resolver;

    //访问拦截器
    @Autowired
    private AccessInterceptor interceptor;

    //添加自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        //注册防刷限流拦截器
        registry.addInterceptor(interceptor);
    }


    /**
     * 添加自定义参数处理器
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(resolver);
    }
}
