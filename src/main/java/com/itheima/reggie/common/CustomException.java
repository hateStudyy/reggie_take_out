package com.itheima.reggie.common;

/**
 * 自定义异常
 * @author coldwind
 * @version 1.0
 */
public class CustomException extends RuntimeException {

    public CustomException(String message){
        super(message);
    }
}
