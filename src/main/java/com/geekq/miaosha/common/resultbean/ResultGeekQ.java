package com.geekq.miaosha.common.resultbean;

import com.geekq.miaosha.common.enums.ResultStatus;

import java.io.Serializable;

//接口返回结果封装类
public class ResultGeekQ<T> extends AbstractResult implements Serializable {
    private static final long serialVersionUID = 867933019328199779L;
    //响应数据
    private T data;
    private Integer count;

    protected ResultGeekQ(ResultStatus status, String message) {
        super(status, message);
    }
    protected ResultGeekQ(ResultStatus status) {
        super(status);
    }
    public static <T> ResultGeekQ<T> build() {
        return new ResultGeekQ(ResultStatus.SUCCESS, (String)null);
    }

    public static <T> ResultGeekQ<T> build(String message) {
        return new ResultGeekQ(ResultStatus.SUCCESS, message);
    }

    public static <T> ResultGeekQ<T> error(ResultStatus status) {
        return new ResultGeekQ<T>(status);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void success(T value) {
        this.success();
        this.data = value;
        this.count = 0;
    }

}
