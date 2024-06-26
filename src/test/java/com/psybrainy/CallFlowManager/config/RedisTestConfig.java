package com.psybrainy.CallFlowManager.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
@EnableRedisRepositories
public class RedisTestConfig {


    private final RedisServer redisServer;

    public RedisTestConfig() throws IOException {
        this.redisServer = new RedisServer();
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }

}
