package com.stylefeng.guns.rest.modular.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/8 22:34
 */
@Component
public class CacheService {
    private Cache cache;

    @PostConstruct
    public void init(){
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(10)
                .maximumSize(100l)
                .build();
    }

    public void put (String key, Object object){
        cache.put(key, object);
    }

    public Object get(String key){
        Object object = cache.getIfPresent(key);
        return object;
    }
}
