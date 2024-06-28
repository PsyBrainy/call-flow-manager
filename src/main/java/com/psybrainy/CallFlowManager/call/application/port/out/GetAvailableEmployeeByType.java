package com.psybrainy.CallFlowManager.call.application.port.out;

import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.call.domain.EmployeeType;

public interface GetAvailableEmployeeByType {

    Employee execute(EmployeeType type);
}
