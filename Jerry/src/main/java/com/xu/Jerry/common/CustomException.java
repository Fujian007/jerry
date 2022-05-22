package com.xu.Jerry.common;

/**
 * 自定义的业务异常
 * 处理删除菜品分类问题
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}
