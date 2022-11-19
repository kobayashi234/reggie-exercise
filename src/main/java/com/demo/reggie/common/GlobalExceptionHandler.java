package com.demo.reggie.common;

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
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 字段重复异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] s = message.split(" ");
            String msg = s[2] + "已存在";
            return R.error(msg);
        }

        return R.error(ex.getMessage());
    }

    /**
     * 自定义异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> CustomExceptionHandler(CustomException ex){
        log.info(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
