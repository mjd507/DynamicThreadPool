package com.github.mjd507.dynamicthreadpool.admin.config;

import com.github.mjd507.util.exception.BusinessException;
import com.github.mjd507.util.http.ApiCode;
import com.github.mjd507.util.http.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by mjd on 2020/4/17 14:12
 */
@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler({BusinessException.class})
    public ApiResponse handleException(BusinessException e) {
        log.error("捕获全局异常:", e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ApiResponse handleException(Exception e) {
        log.error("捕获全局异常:", e);
        return ApiResponse.error(ApiCode.INTERNAL_ERROR.getCode(), e.getMessage());
    }
}
