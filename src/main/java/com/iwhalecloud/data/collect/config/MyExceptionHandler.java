package com.iwhalecloud.data.collect.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//@ControllerAdvice
public class MyExceptionHandler {
    @Resource
    private ErrorAttributes errorAttributes;

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> methodHttpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpMessageNotReadableException exception) {
        //按需重新封装需要返回的错误信息
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> body = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        body.put("DATA", "convert exception message to JSON");
        body.put("STATUS", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("MESSAGE", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("SUCCESS", false);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
