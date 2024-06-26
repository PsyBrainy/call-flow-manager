package com.psybrainy.CallFlowManager.adapter.out.redis;

import com.psybrainy.CallFlowManager.call.adapter.out.redis.GetAvailableEmployeeByTypeRedisAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.domain.EmployeeType;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {RedisTestConfig.class})
public class GetAvailableEmployeeByTypeRedisAdapterTest {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplateTest;

    private GetAvailableEmployeeByType adapter;

    @BeforeEach
    void setUp() {
        adapter = new GetAvailableEmployeeByTypeRedisAdapter(redisTemplateTest);

        Set<String> keys = redisTemplateTest.keys("employee:*:OPERADOR");
        if (keys != null) {
            keys.forEach(redisTemplateTest::delete);
        }

        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERADOR", true);
        valueOperations.set("employee:2:OPERADOR", false);
    }

    @Test
    void testExecute() {
        String result = adapter.execute(EmployeeType.OPERADOR);

        assertNotNull(result);
        assertEquals("employee:1:OPERADOR", result);
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        assertEquals(Boolean.FALSE, valueOperations.get("employee:1:OPERADOR"));
    }

    @Test
    void testNoAvailableEmployee() {
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERADOR", false);
        valueOperations.set("employee:2:OPERADOR", false);

        String result = adapter.execute(EmployeeType.OPERADOR);

        assertEquals(null, result);
    }
}
