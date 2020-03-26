package com.express.web.support;

import com.express.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;
public class ResponseResult<T> implements Result<T> {

    /*--定义公共状态常量--*/
    //未登录
    public static final int NO_LOGIN=301;
    //登陆过期
    public static final int LOGIN_EXPIRED = 302;
    //没有权限
    public static final int NO_PERMISSION=400;


    private Map<String, Object> headers = new HashMap<>();

    private int status;

    private T payload;

    private String responseCode;

    private String responseMessage;



    /**
     * 创建一个成功的结果，不含 payload
     */
    public static Result<?> newResult() {
        ResponseResult<?> result = new ResponseResult<>();
        result.status = Result.STATUS_SUCCESS;
        result.responseCode = ResponseCodes.RESPONSE_CODE_SUCCESS;
        return result;
    }

    /**
     * 创建一个成功的结果
     *
     * @param payload 结果中的数据
     * @return 新创建的结果
     */
    public static <T> Result<T> newResult(T payload) {
        ResponseResult<T> result = new ResponseResult<>();
        result.status = Result.STATUS_SUCCESS;
        result.responseCode = ResponseCodes.RESPONSE_CODE_SUCCESS;
        result.payload = payload;
        return result;
    }

    /**
     * 创建一个失败的结果
     *
     * @param ex 导致失败的具体异常
     * @return 新创建的结果
     */
    public static <T> Result<T> newFailResult(Throwable ex) {
        ResponseResult<T> result = new ResponseResult<>();
        result.status = Result.STATUS_FAIL;
        result.responseMessage = ex.getMessage();
        result.responseCode = (ex instanceof BusinessException) ? ((BusinessException) ex).getCode()
                : ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR;
        return result;
    }

    /**
     * 创建一个具有指定错误消息的失败结果
     *
     * @param message 错误消息
     * @return 新创建的结果
     */
    public static <T> Result<T> newFailResult(String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.status = Result.STATUS_FAIL;
        result.responseMessage = message;
        result.responseCode = ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR;
        return result;
    }

    /**
     * 含提示信息和状态码的错误类型
     * @param message 错误说明
     * @param status  错误状态码
     * @param <T>
     * @return
     */
    public static <T> Result<T> newFailResult(String message, int status) {
        ResponseResult<T> result = new ResponseResult<>();
        result.status = status;
        result.responseMessage = message;
        result.responseCode = ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR;
        return result;
    }



    private ResponseResult() {
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean isSuccess() {
        return Result.STATUS_SUCCESS == status;
    }

    @Override
    public String getResponseCode() {
        return responseCode;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public T getPayload() {
        return payload;
    }



    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public Result<T> setHeader(String key, Object value) {
        headers.put(key, value);
        return this;
    }

    @Override
    public Object getHeader(String key) {
        return headers.get(key);
    }
}