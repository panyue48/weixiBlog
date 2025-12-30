package com.weixi.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

import java.nio.charset.StandardCharsets;

/**
 * 个人博客系统启动类
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@MapperScan("com.weixi.blog.mapper")
public class BlogApplication {
    public static void main(String[] args) {
        // 设置控制台输出编码为 UTF-8，解决中文乱码问题
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));
        
        SpringApplication.run(BlogApplication.class, args);
    }
}

