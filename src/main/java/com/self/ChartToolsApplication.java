package com.self;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动入口
 * @author liuyong
 */
@SpringBootApplication
@MapperScan(basePackages = "com.self.repository.mapper")
public class ChartToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChartToolsApplication.class, args);
    }
}
