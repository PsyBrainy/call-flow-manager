package com.psybrainy.CallFlowManager.call.domain;

public abstract class Employee {

    protected String id;
    protected EmployeeType type;

    public Employee(String id, EmployeeType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", type=" + type +
                '}';
    }
}
