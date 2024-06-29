package com.psybrainy.CallFlowManager.call.application.port.out;

import com.psybrainy.CallFlowManager.call.domain.Employee;
import com.psybrainy.CallFlowManager.call.domain.EmployeeType;

import java.util.Optional;

public interface GetAvailableEmployeeByType {

    Optional<Employee> execute(EmployeeType type);
}
