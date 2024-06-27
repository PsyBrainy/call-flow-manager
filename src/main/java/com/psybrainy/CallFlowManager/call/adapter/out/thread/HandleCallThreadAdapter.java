package com.psybrainy.CallFlowManager.call.adapter.out.thread;

import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.share.AbstractLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class HandleCallThreadAdapter
        extends AbstractLogger
        implements HandleCall {

    private final RedisTemplate<String, Boolean> redisTemplate;

    @Autowired
    public HandleCallThreadAdapter(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void execute(Call call, Employee employee) {
        try {
            log.info("Handling call thread with employee {}", employee);
            Thread.sleep(call.getDuration() * 1000L);

            String redisEmployeeId = "employee:"
                    .concat(employee.getId())
                    .concat(":")
                    .concat(employee.getType().name());
            log.info("Releasing employee: {}", employee);
            redisTemplate.opsForValue().set(redisEmployeeId, Boolean.TRUE);
            log.info("Call completed in {} seconds", call.getDuration());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
