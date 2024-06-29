package com.psybrainy.CallFlowManager.call.application.usecase;

import com.psybrainy.CallFlowManager.call.application.port.in.Dispatcher;
import com.psybrainy.CallFlowManager.call.application.port.out.AddCallToQueue;
import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.share.AbstractLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.psybrainy.CallFlowManager.call.domain.EmployeeType.*;

@Component
public class DispatchCallUseCase
        extends AbstractLogger
        implements Dispatcher {

    private final GetAvailableEmployeeByType getAvailableEmployeeByType;
    private final HandleCall handleCall;
    private final AddCallToQueue addCallToQueue;

    @Autowired
    public DispatchCallUseCase(GetAvailableEmployeeByType getAvailableEmployeeByType, HandleCall handleCall, AddCallToQueue addCallToQueue) {
        this.getAvailableEmployeeByType = getAvailableEmployeeByType;
        this.handleCall = handleCall;
        this.addCallToQueue = addCallToQueue;
    }

    @Async
    @Override
    public CompletableFuture<String> dispatchCall(Call call) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Employee> employee = getAvailableEmployee();
                if (employee.isPresent()) {
                    log.info("Employee found: {}", employee);
                    handleCall.execute(call, employee.get());
                    return "Call dispatched successfully";
                } else {
                    log.info("No employee available, adding call to queue");
                    addCallToQueue.send(call);
                    return "No employee available, call added to the queue";
                }
            } catch (Exception e) {
                log.error("Error dispatching call", e);
                return "Error dispatching call";
            }
        });
    }

    private Optional<Employee> getAvailableEmployee() {

        log.info("Search available employee by type: {}", OPERATOR.name());
        Optional<Employee> employee = getAvailableEmployeeByType.execute(OPERATOR);
        if (employee.isPresent()) {
            log.info("Found available employee: {}", employee);
            return employee;
        }

        log.info("Search available employee by type: {}", SUPERVISOR.name());
        employee = getAvailableEmployeeByType.execute(SUPERVISOR);
        if (employee.isPresent()) {
            log.info("Found available employee: {}", employee);
            return employee;
        }

        log.info("Search available employee by type: {}", DIRECTOR.name());
        employee = getAvailableEmployeeByType.execute(DIRECTOR);
        if (employee.isPresent()) {
            log.info("Found available employee: {}", employee);
            return employee;
        }
        log.info("No available employee");
        return Optional.empty();
    }
}
