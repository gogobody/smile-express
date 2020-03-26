package com.express.exception;

public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 2489175147955201912L;

    private String code;

	public BusinessException(String code) {
		this.code = code;
	}

	public BusinessException(String code, String message) {
		super(message);
		this.code = code;
	}

	public BusinessException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}