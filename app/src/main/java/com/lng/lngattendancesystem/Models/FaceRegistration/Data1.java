package com.lng.lngattendancesystem.Models.FaceRegistration;

import com.google.gson.annotations.SerializedName;

public class Data1 {

    //    "empId": 6,
    //        "empName": "Rahul",
    //        "shiftType": "D1D1",
    //        "empPresistedFaceId": "71f25aa6-ceb5-4ddc-b7e7-7a0286b994a3"


    @SerializedName("empId")
    private Integer empId;

    @SerializedName("empName")
    private String empName;

    @SerializedName("shiftType")
    private String shiftType;

    @SerializedName("empPresistedFaceId")
    private String empPresistedFaceId;

    @SerializedName("empCode")
    private String empCode;

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
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
}
