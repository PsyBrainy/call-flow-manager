package com.psybrainy.CallFlowManager.call.application.usecase;

import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.psybrainy.CallFlowManager.call.domain.EmployeeType.*;

@Component
public class DispatchCallUseCase {

    private final GetAvailableEmployeeByType getAvailableEmployeeByType;
    private final HandleCall handleCall;

    public DispatchCallUseCase(GetAvailableEmployeeByType getAvailableEmployeeByType, HandleCall handleCall) {
        this.getAvailableEmployeeByType = getAvailableEmployeeByType;
        this.handleCall = handleCall;
    }

    @Async
    public CompletableFuture<String> dispatchCall(Call call) {
        return CompletableFuture.supplyAsync(() -> {
            Employee employee = getAvailableEmployee();
            if (employee != null) {
                handleCall.execute(call, employee);
                return "Call dispatched successfully";
            } else {
                return "No employee available to handle the call";
            }

        });
    }

    private Employee getAvailableEmployee() {

        Employee employee = getAvailableEmployeeByType.execute(OPERADOR);
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
}
