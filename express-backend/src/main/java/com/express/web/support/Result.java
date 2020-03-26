package com.express.web.support;

import java.util.Map;

public interface Result<T> {
    /** 请求结果状态：成功. */
    public static final int STATUS_SUCCESS = 0;

    /** 请求结果状态：失败. */
    public static final int STATUS_FAIL = 1;

    /** 获取请求状态 */
    int getStatus();

    boolean isSuccess();

    /**
	 * @return 响应码
	 */
	String getResponseCode();

	/**
	 * @return 响应消息
	 */
    String getResponseMessage();
    
    /**
     * 响应数据
     * @return
     */
    T getPayload();

    /**
	 * @return 消息头承载的数据
	 */
	Map<String, Object> getHeaders();

	/**
	 * 向结果中添加 header
	 * 
	 * @param key 添加 header 的 key
	 * @param value 添加 header 的值
	 * @return 当前结果对象
	 */
	Result<T> setHeader(String key, Object value);

	/**
	 * 取指定 key 的头的值
	 * 
	 * @param key 取值的 key
	 * @return header 的值
	 */
	Object getHeader(String key);
}