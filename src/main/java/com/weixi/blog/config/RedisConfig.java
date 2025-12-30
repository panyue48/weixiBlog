package com.weixi.blog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类（可选配置，Redis 不可用时自动降级）
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.data.redis.port:6380}")
    private int redisPort;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    /**
     * 手动创建 Redis 连接工厂（尝试连接，失败则抛出异常由降级方案处理）
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.setValidateConnection(false); // 不验证连接，避免启动时阻塞
        factory.afterPropertiesSet();
        
        // 异步测试连接（不阻塞启动）
        try {
            factory.getConnection().ping();
            log.info("Redis 连接成功: {}:{}", redisHost, redisPort);
        } catch (Exception e) {
            log.warn("Redis 连接失败: {}，将使用降级缓存（直接查询数据库）", e.getMessage());
            // 不抛出异常，让应用继续启动，使用降级缓存
        }
        
        return factory;
    }
    
    /**
     * 配置 RedisTemplate（仅在 Redis 可用时创建）
     */
    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.host")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisConnectionFactory connectionFactory = redisConnectionFactory();
        if (connectionFactory == null) {
            return null;
        }
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 Jackson2JsonRedisSerializer 序列化
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // 配置 JavaTimeModule 以支持 LocalDateTime 等 Java 8 时间类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(java.time.LocalDateTime.class, 
            LocalDateTimeSerializer.INSTANCE);
        javaTimeModule.addDeserializer(java.time.LocalDateTime.class, 
            LocalDateTimeDeserializer.INSTANCE);
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        // 设置 key 和 value 的序列化规则
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 Redis 缓存管理器（仅在 Redis 可用时创建）
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        RedisConnectionFactory connectionFactory = redisConnectionFactory();
        
        // 测试 Redis 连接是否真的可用
        if (connectionFactory != null) {
            try {
                // 测试连接
                connectionFactory.getConnection().ping();
                log.info("Redis 连接成功，使用 Redis 缓存管理器");
                
                // 配置序列化
                Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
                ObjectMapper mapper = new ObjectMapper();
                mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                
                // 配置 JavaTimeModule 以支持 LocalDateTime 等 Java 8 时间类型
                JavaTimeModule javaTimeModule = new JavaTimeModule();
                javaTimeModule.addSerializer(java.time.LocalDateTime.class, 
                    LocalDateTimeSerializer.INSTANCE);
                javaTimeModule.addDeserializer(java.time.LocalDateTime.class, 
                    LocalDateTimeDeserializer.INSTANCE);
                mapper.registerModule(javaTimeModule);
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                
                mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
                serializer.setObjectMapper(mapper);

                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1)) // 设置缓存过期时间为1小时
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                        .disableCachingNullValues(); // 不缓存空值

                return RedisCacheManager.builder(connectionFactory)
                        .cacheDefaults(config)
                        .build();
            } catch (Exception e) {
                log.warn("Redis 连接测试失败: {}，将使用降级缓存（直接查询数据库）", e.getMessage());
            }
        }
        
        // 降级：使用 NoOpCacheManager（直接查询数据库，不缓存）
        log.warn("Redis 不可用，使用 NoOpCacheManager（禁用缓存，直接查询数据库）");
        return new NoOpCacheManager();
    }
}

