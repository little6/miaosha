package com.geekq.miaosha.config;

import com.geekq.miaosha.access.UserContext;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

//自定义方法参数处理器
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoShaUserService userService;

    //支持controller方法参数类型
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
      Class<?> clazz =    methodParameter.getParameterType() ;
      return clazz == MiaoshaUser.class ;
    }

    //是上面支持的参数类型时，才进行真正的参数解析
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        /**
         *  threadlocal 存储线程副本 保证线程不冲突
         *
         *  在拦截器中，将当前用户放入ThreadLocal中
         */
        return UserContext.getUser();
    }

}
