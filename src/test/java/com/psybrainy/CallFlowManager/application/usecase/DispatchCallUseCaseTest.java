package com.psybrainy.CallFlowManager.application.usecase;

import com.psybrainy.CallFlowManager.call.adapter.out.redis.GetAvailableEmployeeByTypeRedisAdapter;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.application.usecase.DispatchCallUseCase;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(RedisTestConfig.class)
public class DispatchCallUseCaseTest {

    @Mock
    private HandleCall handleCall;

    @InjectMocks
    private DispatchCallUseCase dispatchCallUseCase;

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplate;

    private GetAvailableEmployeeByType getAvailableEmployeeByType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ValueOperations<String, Boolean> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("employee:1:OPERADOR", true);
        valueOperations.set("employee:2:OPERADOR", true);
        valueOperations.set("employee:3:OPERADOR", true);
        valueOperations.set("employee:4:OPERADOR", true);
        valueOperations.set("employee:5:SUPERVISOR", true);
        valueOperations.set("employee:6:SUPERVISOR", true);
        valueOperations.set("employee:7:SUPERVISOR", true);
        valueOperations.set("employee:8:DIRECTOR", true);
        valueOperations.set("employee:9:DIRECTOR", true);
        valueOperations.set("employee:10:DIRECTOR", true);

        getAvailableEmployeeByType = new GetAvailableEmployeeByTypeRedisAdapter(redisTemplate);
        dispatchCallUseCase = new DispatchCallUseCase(getAvailableEmployeeByType, handleCall);

        doNothing().when(handleCall).execute(any(Call.class), any());
    }

    @Test
    void testDispatchCall() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Call mockCall = new Call();
            CompletableFuture<String> future = dispatchCallUseCase.dispatchCall(mockCall);
            futures.add(future);
        }

        for (CompletableFuture<String> future : futures) {
            assertEquals("Call dispatched successfully", future.get());
        }

        verify(handleCall, times(10)).execute(any(Call.class), any());

        executorService.shutdown();
    }
}