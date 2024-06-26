package com.psybrainy.CallFlowManager.call.application.usecase;

import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.psybrainy.CallFlowManager.call.domain.EmployeeType.*;

@Component
public class DispatchCallUseCase {

    private final GetAvailableEmployeeByType getAvailableEmployeeByType;
    private final HandleCall handleCall;
    private final BlockingQueue<Call> callQueue = new LinkedBlockingQueue<>();

    public DispatchCallUseCase(GetAvailableEmployeeByType getAvailableEmployeeByType, HandleCall handleCall) {
        this.getAvailableEmployeeByType = getAvailableEmployeeByType;
        this.handleCall = handleCall;
        startQueueProcessor();
    }

    @Async
    public CompletableFuture<String> dispatchCall(Call call) {
        return CompletableFuture.supplyAsync(() -> {
            Employee employee = getAvailableEmployee();
            if (employee != null) {
                handleCall.execute(call, employee);
                return "Call dispatched successfully";
            } else {
                callQueue.offer(call);
                return "No employee available, call added to the queue";
            }
        });
    }

    private Employee getAvailableEmployee() {

        Employee employee = getAvailableEmployeeByType.execute(OPERATOR);
        if (employee != null) {
            return employee;
        }

        employee = getAvailableEmployeeByType.execute(SUPERVISOR);
        if (employee != null) {
            return employee;
        }

        employee = getAvailableEmployeeByType.execute(DIRECTOR);
        if (employee != null) {
            return employee;
        }

        return null;
    }

    @Async
    public void startQueueProcessor() {
        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {
                    Call call = callQueue.take();
                    Employee employee;
                    while ((employee = getAvailableEmployee()) == null) {
                        Thread.sleep(1000);
                    }
                    handleCall.execute(call, employee);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
