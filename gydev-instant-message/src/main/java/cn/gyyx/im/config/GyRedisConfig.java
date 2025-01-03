package cn.gyyx.im.config;

import cn.gydev.lib.gyredis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zxw
 * @Description:
 * @create 2024/4/9/000916:35
 * @Version 1.0
 **/
@Configuration
public class GyRedisConfig {

    private final String ACTION_REDIS="im_main_redis";

    /**
     * redis配置类
     */
    @Autowired
    RedisConfig redisConfig;

    /**
     * 不显示指定bean名称时会使用方法名
     * @return
     */
    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConfig.getRCF(ACTION_REDIS,8));
        StringRedisSerializer serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}

