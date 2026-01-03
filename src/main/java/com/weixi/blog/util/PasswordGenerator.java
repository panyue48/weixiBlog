package com.weixi.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 * 用于生成BCrypt密码哈希值
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin123 的 BCrypt 哈希
        String password = "admin123";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("========================================");
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt 哈希: " + hashedPassword);
        System.out.println("========================================");
        System.out.println();
        System.out.println("请执行以下 SQL 更新数据库：");
        System.out.println("UPDATE t_user SET password = '" + hashedPassword + "' WHERE username = 'admin';");
        System.out.println();
        
        // 验证密码
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("密码验证结果: " + (matches ? "✓ 成功" : "✗ 失败"));
    }
}








