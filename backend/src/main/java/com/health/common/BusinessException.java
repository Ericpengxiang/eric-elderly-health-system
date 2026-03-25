package com.health.common;

import lombok.Getter;

/**
 * 业务异常，由全局处理器转换为友好 JSON
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
