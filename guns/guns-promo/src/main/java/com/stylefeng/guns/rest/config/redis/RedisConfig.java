package com.stylefeng.guns.rest.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;

/**
 * @author: jia.xue
 * @create: 2019-06-24 14:47
 * @Description
 **/
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    private transient static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(":");
                sb.append(method.getName());
                for (Object object : objects) {
                    sb.append(object.toString());
                }
                logger.info("自动生成的Redis key ---> {}",sb.toString());
                return sb.toString();
            }
        };
    }


    @Bean
    public CacheManager cacheManager() {
        logger.info("初始化 -> [{}]", "CacheManager RedisCacheManager Start");
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(jedisConnectionFactory);
        return builder.build();
    }


    /**
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(JedisConnectionFactory factory){

        /**设置序列化*/
        Jackson2JsonRedisSerializer jacksonredisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL,JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonredisSerializer.setObjectMapper(mapper);

        /**为redisTemplate 配置相应的序列化方式*/
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);


        redisTemplate.setValueSerializer(jacksonredisSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jacksonredisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        logger.info("初始化 -> [{}]", "Redis CacheErrorManager");
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                logger.info("Redis occur handleCacheGetError: key -> [{}]",key,e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                logger.info("Redis occur handleCachePutError: key -> [{}], value ->[{}]",key,value,e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                logger.info("Redis occur handleCacheEvictError: key -> [{}]",key,e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                logger.info("Redis occur handleCacheClearError: ",e);
            }
        };
        return cacheErrorHandler;
    }


    /**
     * 此内部类就是把yml的配置数据，进行读取，创建JedisConnectionFactory和JedisPool，以供外部类初始化缓存管理器使用
     * 不了解的同学可以去看@ConfigurationProperties和@Value的作用
     *
     */
    @ConfigurationProperties
    class DataJedisProperties{
        @Value("${spring.redis.host}")
        private  String host;
        @Value("${spring.redis.password}")
        private  String password;
        @Value("${spring.redis.port}")
        private  int port;
        @Value("${spring.redis.timeout}")
        private  int timeout;
        @Value("${spring.redis.jedis.pool.max-idle}")
        private int maxIdle;
        @Value("${spring.redis.jedis.pool.max-wait}")
        private long maxWaitMillis;

        @Bean
        JedisConnectionFactory jedisConnectionFactory() {
            logger.info("Create JedisConnectionFactory successful");
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName(host);
            factory.setPort(port);
            factory.setTimeout(timeout);
            factory.setPassword(password);
            return factory;
        }
        @Bean
        public JedisPool redisPoolFactory() {
            logger.info("JedisPool init successful，host -> [{}]；port -> [{}]", host, port);
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(maxIdle);
            jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
            return jedisPool;
        }
    }
}