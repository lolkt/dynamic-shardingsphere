package com.lolkt.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan({"com.lolkt.demo.mapper", "com.baidu.fsg.uid.worker.dao"})
@SpringBootApplication
public class TestshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestshardingApplication.class, args);
    }

}
