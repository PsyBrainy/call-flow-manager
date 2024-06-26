package com.psybrainy.CallFlowManager.call.adapter.out.thread;

import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class HandleCallThreadAdapter implements HandleCall {

    private final RedisTemplate<String, Boolean> redisTemplate;

    @Autowired
    public HandleCallThreadAdapter(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void execute(Call call, Employee employee) {
        try {
            Thread.sleep(call.getDuration() * 1000L);

            String redisEmployeeId = "employee:"
                    .concat(employee.getId())
                    .concat(":")
                    .concat(employee.getType().name());

            redisTemplate.opsForValue().set(redisEmployeeId, true);
            System.out.println("Call completed in " + call.getDuration() + " seconds by " + employee.getClass().getSimpleName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
