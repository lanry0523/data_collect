package com.iwhalecloud.data.collect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

//@Component
public class MyErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
//        return super.getErrorAttributes(webRequest, options);
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        errorAttributes.put("DATA", "convert exception message to JSON");
        errorAttributes.put("STATUS", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorAttributes.put("MESSAGE", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorAttributes.put("SUCCESS", false);

        return errorAttributes;
    }

//    @Autowired
//    public Map<String, Object> getErrorAttributes(WebRequest request, ErrorAttributeOptions options) {
//        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
//        errorAttributes.put("DATA", "convert exception message to JSON");
//        errorAttributes.put("STATUS", HttpStatus.INTERNAL_SERVER_ERROR.value());
//        errorAttributes.put("MESSAGE", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//        errorAttributes.put("SUCCESS", false);
//
//        return errorAttributes;
//    }

//    @Resource
//    private ErrorAttributes SerrorAttributes;

//    @ExceptionHandler(value = HttpMessageNotReadableException.class)
//    public ResponseEntity<Map<String, Object>> methodHttpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpMessageNotReadableException exception) {
//        //按需重新封装需要返回的错误信息
//        WebRequest webRequest = new ServletWebRequest(request);
//        Map<String, Object> body = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
//        body.put("DATA", "convert exception message to JSON");
//        body.put("STATUS", HttpStatus.INTERNAL_SERVER_ERROR.value());
//        body.put("MESSAGE", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//        body.put("SUCCESS", false);
//        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
