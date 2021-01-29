package com.lng.lngattendancesystem.Models.RestoreEmployeeDetales;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmployeeDetail {

    @SerializedName("empId")
    @Expose
    private Integer empId;
    @SerializedName("empName")
    @Expose
    private String empName;
    @SerializedName("shiftType")
    @Expose
    private String shiftType;
    @SerializedName("empPresistedFaceId")
    @Expose
    private String empPresistedFaceId;

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getEmpPresistedFaceId() {
        return empPresistedFaceId;
    }

    public void setEmpPresistedFaceId(String empPresistedFaceId) {
        this.empPresistedFaceId = empPresistedFaceId;
    }
}
