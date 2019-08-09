package org.hys.workshop.statemachine.config;


import lombok.Data;

import org.hys.workshop.statemachine.persister.RedisRepository;
import org.hys.workshop.statemachine.persister.RedisRuntimePersister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@Data
public class BeanConfig {

    @Value("${redis.url}")
    private String redisUrl;
    @Value("${redis.port}")
    private String redisPost;
    @Value("${redis.explication}")
    private String redisExperation;

    @Bean
    public JedisConnectionFactory redisConnectionFactory()  {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisUrl, Integer.valueOf(redisPost));
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisRepository redisRepository()    {
        return new RedisRepository(redisConnectionFactory());
    }

    @Bean
    public RedisRuntimePersister redisRuntaymPersister()    {
        return new RedisRuntimePersister();
    }
}
