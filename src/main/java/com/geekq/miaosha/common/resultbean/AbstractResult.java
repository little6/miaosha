package com.geekq.miaosha.common.resultbean;

import com.geekq.miaosha.common.enums.ResultStatus;

public class AbstractResult {
    //响应状态码和响应信息
    private ResultStatus status;
    //响应码
    private int code;
    //响应信息
    private String message;

    protected AbstractResult(ResultStatus status, String message) {
        this.code = status.getCode();
        this.status = status;
        this.message = message;
    }

    protected AbstractResult(ResultStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.status = status;
    }

    public static boolean isSuccess(AbstractResult result) {
        return result != null && result.status == ResultStatus.SUCCESS && result.getCode() == ResultStatus.SUCCESS.getCode();
    }

    public AbstractResult withError(ResultStatus status) {
        this.status = status;
        return this;
    }

    public AbstractResult withError(String message) {
        this.status = ResultStatus.SYSTEM_ERROR;
        this.message = message;
        return this;
    }

    public AbstractResult withError(int code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    public AbstractResult success() {
        this.status = ResultStatus.SUCCESS;
        return this;
    }
    public ResultStatus getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message == null ? this.status.getMessage() : this.message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
