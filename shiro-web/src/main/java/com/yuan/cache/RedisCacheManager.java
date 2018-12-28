package com.yuan.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import javax.annotation.Resource;

/**
 * Created by wangy on 2018/10/16.
 * <p>
 * shiro自定义缓存管理
 */
public class RedisCacheManager implements CacheManager {

    @Resource
    private RedisCache redisCache;

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        return redisCache;
    }
}
