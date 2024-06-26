package com.psybrainy.CallFlowManager.call.adapter.out.redis;

import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GetAvailableEmployeeByTypeRedisAdapter implements GetAvailableEmployeeByType {

    private final RedisTemplate<String, Boolean> redisTemplate;
    private final Lock lock = new ReentrantLock();

    @Autowired
    public GetAvailableEmployeeByTypeRedisAdapter(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Employee execute(EmployeeType type) {
        lock.lock();
        try {
            Set<String> keys = redisTemplate.keys("employee:*:" + type.name());
            if (keys != null) {
                for (String employee : keys) {
                    Boolean isAvailable = redisTemplate.opsForValue().get(employee);
                    if (Boolean.TRUE.equals(isAvailable)) {
                        redisTemplate.opsForValue().set(employee, false);

                        String idEmployee = employee
                                .replace("employee:", "")
                                .replace(":" + type.name(), "");

                        return switch (type) {
                            case OPERADOR -> new Operator(idEmployee);
                            case SUPERVISOR -> new Supervisor(idEmployee);
                            case DIRECTOR -> new Director(idEmployee);
                            default -> null;
                        };
                    }
                }
            }
            return null;
        } catch (Exception e) {
            //TODO
            e.printStackTrace();
            return null;
        }finally {
            lock.unlock();
        }
    }
}
