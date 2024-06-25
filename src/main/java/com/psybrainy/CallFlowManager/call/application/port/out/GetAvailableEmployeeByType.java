package com.psybrainy.CallFlowManager.call.application.port.out;

import com.psybrainy.CallFlowManager.call.domain.EmployeeType;

public interface GetAvailableEmployeeByType {

    String execute(EmployeeType type);
}
