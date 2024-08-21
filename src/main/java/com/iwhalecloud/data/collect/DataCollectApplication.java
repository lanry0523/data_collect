package com.iwhalecloud.data.collect;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

//@ServletComponentScan("com.iwhalecloud.data.collect.filter")
@MapperScan(basePackages = "com.iwhalecloud.data.collect.dao")
@SpringBootApplication
public class DataCollectApplication extends SpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataCollectApplication.class, args);
    }

}

