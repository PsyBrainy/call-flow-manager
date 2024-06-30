package com.psybrainy.CallFlowManager.adapter.out.thread;

import com.psybrainy.CallFlowManager.call.adapter.out.thread.HandleCallThreadAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Operator;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import com.psybrainy.CallFlowManager.share.exception.CallHandlingException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Import(RedisTestConfig.class)
@EmbeddedKafka(partitions = 1, topics = "call-topic")
@DisplayName("Handle Call test")
public class HandleCallThreadAdapterTest {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplateTest;

    @Mock
    private RedisTemplate<String, Boolean> redisTemplateMock;

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

    @Test
    @DisplayName("When Redis operation throws exception, then CallHandlingException is thrown")
    void whenRedisOperationThrowsException_thenCallHandlingExceptionIsThrown() {
        Call call = new Call();
        Operator operator = new Operator("1");

        doThrow(new RuntimeException("Redis error")).when(redisTemplateMock).opsForValue();

        HandleCallThreadAdapter adapterWithMock = new HandleCallThreadAdapter(redisTemplateMock);

        CallHandlingException exception = assertThrows(CallHandlingException.class, () -> {
            adapterWithMock.execute(call, operator);
        });

        assertEquals("Unexpected error while handling call", exception.getMessage());
    }

    @Test
    @DisplayName("When thread is interrupted, then CallHandlingException is thrown")
    void whenThreadIsInterrupted_thenCallHandlingExceptionIsThrown() {
        Call call = new Call() {
            @Override
            public int getDuration() {
                return 10;
            }
        };
        Operator operator = new Operator("1");

        Thread.currentThread().interrupt();

        CallHandlingException exception = assertThrows(CallHandlingException.class, () -> {
            adapter.execute(call, operator);
        });

        assertEquals("Thread was interrupted while handling call", exception.getMessage());

        Thread.interrupted();
    }
}
