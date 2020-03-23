package com.geekq.miaosha.validator;

import com.geekq.miaosha.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//校验器
public class MobileValidator implements ConstraintValidator<MobileCheck, String> {

    private boolean require = false ;

    //初始化
    @Override
    public void initialize(MobileCheck isMobile) {
        require = isMobile.required() ;
    }

    //手机号是否合法
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(require){
            return ValidatorUtil.isMobile(value) ;
        }else{
            if(StringUtils.isEmpty(value)){
                return true ;
            }else {
                return ValidatorUtil.isMobile(value) ;
            }
        }
    }
}
