package com.xu.Jerry.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存已登录的员工的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
