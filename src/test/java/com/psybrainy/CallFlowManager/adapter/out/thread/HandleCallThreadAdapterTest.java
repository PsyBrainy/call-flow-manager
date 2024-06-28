package com.psybrainy.CallFlowManager.adapter.out.thread;

import com.psybrainy.CallFlowManager.call.adapter.out.thread.HandleCallThreadAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Operator;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(RedisTestConfig.class)
@EmbeddedKafka(partitions = 1, topics = "call-topic")
@DisplayName("Handle Call test")
public class HandleCallThreadAdapterTest {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplateTest;

    private HandleCall adapter;

    @BeforeEach
    void setUp() {
        Set<String> keys = redisTemplateTest.keys("employee:*:OPERATOR");
        if (keys != null) {
            keys.forEach(redisTemplateTest::delete);
        }

        adapter = new HandleCallThreadAdapter(redisTemplateTest);
    }


    @Test
    @DisplayName("When executing a call with an operator, then operator status is set to available after processing")
    void whenExecutingCallWithOperator_thenOperatorStatusIsSetToAvailableAfterProcessing() throws InterruptedException {
        Call call = new Call();
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERATOR", false);
        assertEquals(Boolean.FALSE, valueOperations.get("employee:1:OPERATOR"));
        adapter.execute(call, new Operator("1"));

        Thread.sleep(11000);

        assertEquals(Boolean.TRUE, valueOperations.get("employee:1:OPERATOR"));
    }
}
