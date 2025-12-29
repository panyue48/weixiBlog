package com.weixi.blog.config;

import com.weixi.blog.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login/**",           // 登录接口
                        "/blog/list",          // 博客列表（前台）
                        "/blog/*",             // 博客详情（前台，使用通配符）
                        "/type/list",          // 分类列表（前台，如果需要）
                        "/tag/list"            // 标签列表（前台，如果需要）
                );
    }
}

