package com.geekq.miaosha.exception;

import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.geekq.miaosha.common.enums.ResultStatus.BIND_ERROR;
import static com.geekq.miaosha.common.enums.ResultStatus.SESSION_ERROR;
import static com.geekq.miaosha.common.enums.ResultStatus.SYSTEM_ERROR;

//全局异常处理器
@ControllerAdvice
@ResponseBody
public class GlobleExceptionHandler {

    private static Logger logger =  LoggerFactory.getLogger(GlobleExceptionHandler.class);

    //异常处理
    @ExceptionHandler(value=Exception.class)
    public ResultGeekQ<String> exceptionHandler(HttpServletRequest request , Exception e){
        e.printStackTrace();
        if(e instanceof GlobleException){
            //
            GlobleException ex= (GlobleException)e;
            return ResultGeekQ.error(ex.getStatus());
        }else if( e instanceof BindException){
            //合法性校验器校验异常处理
            BindException ex = (BindException) e  ;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            /**
             * 打印堆栈信息
             */
            logger.error(String.format(msg, msg));
            return ResultGeekQ.error(BIND_ERROR);
        }else {
            return ResultGeekQ.error(SYSTEM_ERROR);
        }
    }
}
