package com.wy.TongZhi.exception;

import com.wy.TongZhi.common.BaseResponse;
import com.wy.TongZhi.common.ErrorCode;
import com.wy.TongZhi.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ",e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,error.getDefaultMessage(),"");
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public BaseResponse<?> unexpectedTypeExceptionHandler(UnexpectedTypeException e) {
        log.error("unexpectedTypeExceptionHandler: ",e);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,e.getMessage(),"");
    }


}
