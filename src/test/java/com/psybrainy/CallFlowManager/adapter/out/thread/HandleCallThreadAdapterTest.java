package com.psybrainy.CallFlowManager.adapter.out.thread;

import com.psybrainy.CallFlowManager.call.adapter.out.thread.HandleCallThreadAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Operator;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(RedisTestConfig.class)
@DisplayName("Handle Call test")
public class HandleCallThreadAdapterTest {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplateTest;

    private HandleCall adapter;

    @BeforeEach
    void setUp() {
        adapter = new HandleCallThreadAdapter(redisTemplateTest);

        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERATOR", false);
    }

    @AfterEach
    public void tearDown() {
        Set<String> keys = redisTemplateTest.keys("employee:*:OPERATOR");
        if (keys != null) {
            keys.forEach(redisTemplateTest::delete);
        }
    }

    @Test
    @DisplayName("When executing a call with an operator, then operator status is set to available after processing")
    void whenExecutingCallWithOperator_thenOperatorStatusIsSetToAvailableAfterProcessing() throws InterruptedException {
        Call call = new Call();
        adapter.execute(call, new Operator("1"));

        Thread.sleep(11000);

        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        assertEquals(Boolean.TRUE, valueOperations.get("employee:1:OPERATOR"));
    }
}
