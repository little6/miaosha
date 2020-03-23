package com.geekq.miaosha.exception;

import com.geekq.miaosha.common.enums.ResultStatus;

//全局自定义异常
public class GlobleException extends RuntimeException {


    private ResultStatus status;

    public GlobleException(ResultStatus status){
        super();
        this.status = status;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}
