package com.psybrainy.CallFlowManager.adapter.out.redis;

import com.psybrainy.CallFlowManager.call.adapter.out.redis.GetAvailableEmployeeByTypeRedisAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.call.domain.Operator;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static com.psybrainy.CallFlowManager.call.domain.EmployeeType.OPERATOR;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(RedisTestConfig.class)
public class GetAvailableEmployeeByTypeRedisAdapterTest {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplateTest;

    private GetAvailableEmployeeByType adapter;

    @BeforeEach
    void setUp() {
        adapter = new GetAvailableEmployeeByTypeRedisAdapter(redisTemplateTest);

        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERATOR", true);
        valueOperations.set("employee:2:OPERATOR", false);
    }

    @AfterEach
    public void tearDown() {
        Set<String> keys = redisTemplateTest.keys("employee:*:OPERATOR");
        if (keys != null) {
            keys.forEach(redisTemplateTest::delete);
        }
    }

    @Test
    void testExecute() {
        Employee result = adapter.execute(OPERATOR);

        assertNotNull(result);
        assertInstanceOf(Operator.class, result);
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        assertEquals(Boolean.FALSE, valueOperations.get("employee:1:OPERATOR"));
    }

    @Test
    void testNoAvailableEmployee() {
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERATOR", false);
        valueOperations.set("employee:2:OPERATOR", false);

        Employee result = adapter.execute(OPERATOR);

        assertNull(result);
    }
}
