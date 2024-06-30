package com.psybrainy.CallFlowManager.adapter.out.redis;

import com.psybrainy.CallFlowManager.call.adapter.out.redis.GetAvailableEmployeeByTypeRedisAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.call.domain.Operator;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import com.psybrainy.CallFlowManager.share.exception.EmployeeServiceException;
import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Optional;
import java.util.Set;

import static com.psybrainy.CallFlowManager.call.domain.EmployeeType.OPERATOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(RedisTestConfig.class)
@EmbeddedKafka(partitions = 1, topics = "call-topic")
@DisplayName("Get Available Employee By Type test")
public class GetAvailableEmployeeByTypeRedisAdapterTest {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplateTest;

    private GetAvailableEmployeeByType adapter;

    @Mock
    private RedisTemplate<String, Boolean> redisTemplateMock;

    @BeforeEach
    void setUp() {
        adapter = new GetAvailableEmployeeByTypeRedisAdapter(redisTemplateTest);

        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERATOR", true);
        valueOperations.set("employee:2:OPERATOR", false);
        valueOperations.set("employee:3:SUPERVISOR", true);
        valueOperations.set("employee:4:SUPERVISOR", true);
    }

    @AfterEach
    public void tearDown() {
        Set<String> keys = redisTemplateTest.keys("employee:*:OPERATOR");
        if (keys != null) {
            keys.forEach(redisTemplateTest::delete);
        }
    }

    @Test
    @DisplayName("When assigning call, then first available employee with priority to operators is chosen")
    void whenAssigningCall_thenFirstAvailableEmployeeWithPriorityToOperatorsIsChosen() {
        Optional<Employee> result = adapter.execute(OPERATOR);

        assertTrue(result.isPresent());
        assertInstanceOf(Operator.class, result.get());
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        assertEquals(Boolean.FALSE, valueOperations.get("employee:1:OPERATOR"));
    }

    @Test
    @DisplayName("When no operators are available, then result is empty")
    void whenNoAvailableOperators_thenResultIsEmpty() {
        ValueOperations<String, Boolean> valueOperations = redisTemplateTest.opsForValue();
        valueOperations.set("employee:1:OPERATOR", false);
        valueOperations.set("employee:2:OPERATOR", false);

        Optional<Employee> result = adapter.execute(OPERATOR);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("When Redis connection error occurs, then EmployeeServiceException is thrown")
    void whenRedisConnectionErrorOccurs_thenEmployeeServiceExceptionIsThrown() {
        when(redisTemplateMock.keys("employee:*:OPERATOR")).thenThrow(new RedisConnectionException("Redis connection error"));

        GetAvailableEmployeeByTypeRedisAdapter adapterWithMock = new GetAvailableEmployeeByTypeRedisAdapter(redisTemplateMock);

        EmployeeServiceException exception = assertThrows(EmployeeServiceException.class, () -> {
            adapterWithMock.execute(OPERATOR);
        });

        assertEquals("Redis connection error", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("When an unexpected error occurs, then EmployeeServiceException is thrown")
    void whenUnexpectedErrorOccurs_thenEmployeeServiceExceptionIsThrown() {
        when(redisTemplateMock.keys("employee:*:OPERATOR")).thenThrow(new RuntimeException("Unexpected error"));

        GetAvailableEmployeeByTypeRedisAdapter adapterWithMock = new GetAvailableEmployeeByTypeRedisAdapter(redisTemplateMock);

        EmployeeServiceException exception = assertThrows(EmployeeServiceException.class, () -> {
            adapterWithMock.execute(OPERATOR);
        });

        assertEquals("Unexpected error", exception.getCause().getMessage());
    }
}
