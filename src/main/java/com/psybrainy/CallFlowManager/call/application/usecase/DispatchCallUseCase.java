package com.psybrainy.CallFlowManager.call.application.usecase;

import com.psybrainy.CallFlowManager.call.application.port.in.Dispatcher;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.share.AbstractLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.psybrainy.CallFlowManager.call.domain.EmployeeType.*;

@Component
public class DispatchCallUseCase
        extends AbstractLogger
        implements Dispatcher<CompletableFuture<String>> {

    private final GetAvailableEmployeeByType getAvailableEmployeeByType;
    private final HandleCall handleCall;
    private final BlockingQueue<Call> callQueue = new LinkedBlockingQueue<>();

    @Autowired
    public DispatchCallUseCase(GetAvailableEmployeeByType getAvailableEmployeeByType, HandleCall handleCall) {
        this.getAvailableEmployeeByType = getAvailableEmployeeByType;
        this.handleCall = handleCall;
        startQueueProcessor();
    }

    @Async
    @Override
    public CompletableFuture<String> dispatchCall(Call call) {
        log.info("Call received: {}", call);
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

        log.info("Search available employee by type: {}", OPERATOR.name());
        Employee employee = getAvailableEmployeeByType.execute(OPERATOR);
        if (employee != null) {
            log.info("Found available employee: {}", employee);
            return employee;
        }

        log.info("Search available employee by type: {}", SUPERVISOR.name());
        employee = getAvailableEmployeeByType.execute(SUPERVISOR);
        if (employee != null) {
            log.info("Found available employee: {}", employee);
            return employee;
        }

        log.info("Search available employee by type: {}", DIRECTOR.name());
        employee = getAvailableEmployeeByType.execute(DIRECTOR);
        if (employee != null) {
            log.info("Found available employee: {}", employee);
            return employee;
        }
        log.info("No available employee");
        return null;
    }

    @Async
    protected void startQueueProcessor() {
        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {
                    Call call = callQueue.take();
                    Employee employee;
                    while ((employee = getAvailableEmployee()) == null) {
                        log.info("Retry call queue: {}", call);
                        Thread.sleep(1000);
                    }
                    log.info("Call queue received: {}", call);
                    handleCall.execute(call, employee);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
