package com.psybrainy.CallFlowManager.call.application.port.out;

import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.call.domain.Employee;

public interface HandleCall {

    void execute(Call call, Employee employee);
}
