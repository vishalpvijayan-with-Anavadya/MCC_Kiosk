package com.lng.lngattendancesystem.Models.RestoreEmployeeDetales;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

import java.util.List;

public class RestoreEmpMainResponce {

    @SerializedName("employeeDetails")
    @Expose
    private List<EmployeeDetail> employeeDetails = null;
    @SerializedName("status")
    @Expose
    private Status status;

    public List<EmployeeDetail> getEmployeeDetails() {
        return employeeDetails;
    }

    public void setEmployeeDetails(List<EmployeeDetail> employeeDetails) {
        this.employeeDetails = employeeDetails;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
