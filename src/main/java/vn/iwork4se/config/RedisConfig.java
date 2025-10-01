package vn.iwork4se.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import java.time.Duration;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.data.host}")
    private String redisHost;

    @Value("${spring.redis.data.port}")
    private int redisPort;
    @Value("${spring.redis.data.username}")
    private String redisUsername;

    @Value("${spring.redis.data.password}")
    private String redisPassword;

    @Value("${spring.redis.data.database}")
    private int database;

    @Value("${spring.redis.data.ssl:false}")
    private boolean sslEnabled;



    @Bean
    public JedisConnectionFactory connectionFactory() {
        log.info("Configuring Redis connection to {}:{}", redisHost, redisPort);

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        configuration.setDatabase(database);

        if (redisUsername != null && !redisUsername.trim().isEmpty()) {
            configuration.setUsername(redisUsername.trim());
            log.info("Using Redis username: {}", redisUsername.trim());
        }

        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            configuration.setPassword(redisPassword.trim());
            log.info("Redis password configured");
        }

        JedisConnectionFactory factory = new JedisConnectionFactory(configuration);

        try {
            factory.afterPropertiesSet();
            log.info("Redis connection factory created successfully");
        } catch (Exception e) {
            log.error("Failed to create Redis connection factory: {}", e.getMessage(), e);
        }

        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        try {
            template.afterPropertiesSet();
            log.info("RedisTemplate configured successfully");
        } catch (Exception e) {
            log.error("Failed to configure RedisTemplate: {}", e.getMessage(), e);
        }

        return template;
    }
}
