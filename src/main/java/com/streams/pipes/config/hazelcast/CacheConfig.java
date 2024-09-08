package com.streams.pipes.config.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.map.IMap;
import com.streams.pipes.model.NewsPayload;
import com.streams.pipes.model.UserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Bean
    public CacheManager cacheManager() {
        return new com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance);
    }
    @Bean
    public IMap<String, NewsPayload> myNewsMap(HazelcastInstance instance) {
        return instance.getMap("myNewsMap");
    }
    @Bean
    public IMap<String, UserPayload> myUserMap(HazelcastInstance instance) {
        return instance.getMap("myUserMap");
    }
    @Bean
    public IMap<String, Long> myNewsCounts(HazelcastInstance instance) {
        return instance.getMap("myNewsCounts");
    }
    @Bean
    public IExecutorService getExecutorService(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getExecutorService("myExecutorService");
    }
}