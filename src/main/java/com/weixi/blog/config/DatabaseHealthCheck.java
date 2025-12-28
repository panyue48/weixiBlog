package com.weixi.blog.config;

import com.weixi.blog.util.DatabaseConnectionTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * 数据库健康检查
 * 启动时检查数据库连接和表是否存在
 */
@Slf4j
@Component
@Order(1)
@ConditionalOnProperty(name = "app.database.health-check.enabled", havingValue = "true", matchIfMissing = false)
public class DatabaseHealthCheck implements CommandLineRunner {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("========== Database Health Check Started ==========");
        
        // First, test direct connection
        try {
            DatabaseConnectionTest.testConnection(dbUrl, dbUsername, dbPassword);
        } catch (Exception e) {
            log.error("Direct connection test failed. Please check your database configuration.");
            return;
        }
        
        // Then, test through DataSource
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbName = metaData.getDatabaseProductName();
            String dbVersion = metaData.getDatabaseProductVersion();
            String url = metaData.getURL();
            
            log.info("Database connection successful!");
            log.info("Database Type: {}", dbName);
            log.info("Database Version: {}", dbVersion);
            log.info("Connection URL: {}", url);
            
            // Check if required tables exist
            String[] requiredTables = {"t_type", "t_tag", "t_blog", "t_user", "t_blog_tags"};
            for (String tableName : requiredTables) {
                checkTableExists(connection, tableName);
            }
            
            // Check if t_type table has data
            if (jdbcTemplate != null) {
                try {
                    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_type", Integer.class);
                    log.info("t_type table record count: {}", count);
                } catch (Exception e) {
                    log.error("Failed to query t_type table record count: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("Database connection failed!", e);
            log.error("Error details: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                log.error("Root cause: {}", e.getCause().getMessage());
            }
            log.error("Please check the following configuration:");
            log.error("1. Database connection info in application.yml");
            log.error("   - URL: jdbc:mysql://localhost:3306/blog_db");
            log.error("   - Username: root");
            log.error("   - Password: (check if correct)");
            log.error("2. Is MySQL service running?");
            log.error("3. Does database 'blog_db' exist?");
            log.error("4. Are username and password correct?");
            log.error("5. Can you connect to MySQL using command line or MySQL Workbench?");
        }
        
        log.info("========== Database Health Check Completed ==========");
    }
    
    private void checkTableExists(Connection connection, String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, null);
            if (tables.next()) {
                log.info("[OK] Table '{}' exists", tableName);
            } else {
                log.error("[FAIL] Table '{}' does not exist! Please run blog_db.sql to create tables", tableName);
            }
            tables.close();
        } catch (Exception e) {
            log.error("Error checking table '{}': {}", tableName, e.getMessage());
        }
    }
}

