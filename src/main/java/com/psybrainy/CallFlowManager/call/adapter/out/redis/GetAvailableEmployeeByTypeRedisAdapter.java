package com.psybrainy.CallFlowManager.call.adapter.out.redis;

import com.psybrainy.CallFlowManager.share.exception.EmployeeServiceException;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.domain.*;
import com.psybrainy.CallFlowManager.share.AbstractLogger;
import io.lettuce.core.RedisConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GetAvailableEmployeeByTypeRedisAdapter
        extends AbstractLogger
        implements GetAvailableEmployeeByType {

    private final RedisTemplate<String, Boolean> redisTemplate;
    private final Lock lock = new ReentrantLock();

    @Autowired
    public GetAvailableEmployeeByTypeRedisAdapter(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Employee> execute(EmployeeType type) {
        lock.lock();
        try {
            Set<String> keys = redisTemplate.keys("employee:*:" + type.name());
            if (keys != null) {
                log.info("Searching employee for {}", type.name());
                for (String employee : keys) {
                    Boolean isAvailable = redisTemplate.opsForValue().get(employee);
                    if (Boolean.TRUE.equals(isAvailable)) {
                        redisTemplate.opsForValue().set(employee, Boolean.FALSE);

                        String idEmployee = employee
                                .replace("employee:", "")
                                .replace(":" + type.name(), "");
                        log.info("Employee {} available with type: {}", idEmployee, type.name());
                        return switch (type) {
                            case OPERATOR -> Optional.of(new Operator(idEmployee));
                            case SUPERVISOR -> Optional.of(new Supervisor(idEmployee));
                            case DIRECTOR -> Optional.of(new Director(idEmployee));
                            default -> Optional.empty();
                        };
                    }
                }
            }
            log.info("Employee {} not available", type.name());
            return Optional.empty();
        } catch (RedisConnectionException e) {
            log.error("Redis connection error", e);
            throw new EmployeeServiceException("Redis connection error", e);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw new EmployeeServiceException("Unexpected error", e);
        } finally {
            lock.unlock();
        }
    }
}
