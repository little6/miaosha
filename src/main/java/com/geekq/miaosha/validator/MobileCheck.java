package com.geekq.miaosha.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

//自定义手机号校验器
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MobileValidator.class})
public @interface MobileCheck {
    //必须存在
    boolean required() default true ;

    //错误信息
    String message() default "手机号码格式有误!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
