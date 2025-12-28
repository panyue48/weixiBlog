package com.weixi.blog.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接测试工具
 * 用于诊断数据库连接问题
 */
@Slf4j
public class DatabaseConnectionTest {
    
    /**
     * 测试数据库连接
     */
    public static void testConnection(String url, String username, String password) {
        log.info("Testing database connection...");
        log.info("URL: {}", url);
        log.info("Username: {}", username);
        log.info("Password: {}", password != null && !password.isEmpty() ? "***" : "(empty)");
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            log.info("Connection successful!");
            log.info("Database Product: {}", connection.getMetaData().getDatabaseProductName());
            log.info("Database Version: {}", connection.getMetaData().getDatabaseProductVersion());
            log.info("Driver Name: {}", connection.getMetaData().getDriverName());
            log.info("Driver Version: {}", connection.getMetaData().getDriverVersion());
            return;
        } catch (SQLException e) {
            log.error("Connection failed!");
            log.error("SQL State: {}", e.getSQLState());
            log.error("Error Code: {}", e.getErrorCode());
            log.error("Error Message: {}", e.getMessage());
            
            // 提供具体的错误提示
            if (e.getMessage().contains("Access denied")) {
                log.error(">>> POSSIBLE ISSUE: Wrong username or password!");
            } else if (e.getMessage().contains("Unknown database")) {
                log.error(">>> POSSIBLE ISSUE: Database does not exist!");
            } else if (e.getMessage().contains("Communications link failure") || 
                       e.getMessage().contains("Connection refused")) {
                log.error(">>> POSSIBLE ISSUE: MySQL service is not running or wrong port!");
            } else if (e.getMessage().contains("timeout")) {
                log.error(">>> POSSIBLE ISSUE: Connection timeout - check network/firewall!");
            }
            
            throw new RuntimeException("Database connection test failed", e);
        }
    }
}

