package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author Jerry
 * @create 2022-02-17 11:41
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result erro(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result erro(YyghException e){
        e.printStackTrace();
        return Result.fail();
    }


}
