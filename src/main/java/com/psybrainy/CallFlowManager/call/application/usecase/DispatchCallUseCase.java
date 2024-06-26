package com.psybrainy.CallFlowManager.call.application.usecase;

import com.psybrainy.CallFlowManager.call.application.port.out.GetAvailableEmployeeByType;
import com.psybrainy.CallFlowManager.call.application.port.out.HandleCall;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;
import org.springframework.stereotype.Component;

@Component
public class DispatchCallUseCase {

    private GetAvailableEmployeeByType getAvailableEmployeeByType;
    private HandleCall handleCall;

    public DispatchCallUseCase(GetAvailableEmployeeByType getAvailableEmployeeByType, HandleCall handleCall) {
        this.getAvailableEmployeeByType = getAvailableEmployeeByType;
        this.handleCall = handleCall;
    }

    public String dispatchCall(Call call) {

        Employee employee = getAvailableEmployeeByType.execute();
        handleCall.execute(call, employee);

        return "";
    }
}
