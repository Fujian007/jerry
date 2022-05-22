package com.xu.Jerry.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */

//拦截带有@RestController或者@Controller注解的类
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //异常处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //判断异常信息
        if (ex.getMessage().contains("Duplicate entry")){
            String[] strings = ex.getMessage().split(" ");
            String string = strings[2];
            return R.error(  string + " 已存在!");
        }
        return R.error("未知错误!");
    }




    //自定义异常处理，删除菜品分类时，可能关联了其他菜品
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
       //
        return R.error(ex.getMessage());
    }
}
