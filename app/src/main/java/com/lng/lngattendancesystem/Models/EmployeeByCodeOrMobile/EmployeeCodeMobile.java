package com.lng.lngattendancesystem.Models.EmployeeByCodeOrMobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmployeeCodeMobile {

    @SerializedName("data1")
    @Expose
    private Data1 data1;
    @SerializedName("status")
    @Expose
    private Status1 status;

    public Data1 getData1() {
        return data1;
    }

    public void setData1(Data1 data1) {
        this.data1 = data1;
    }

    public Status1 getStatus() {
        return status;
    }

    public void setStatus(Status1 status) {
        this.status = status;
    }


    public class Status1 {

        @SerializedName("error")
        @Expose
        private Boolean error;
        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("message")
        @Expose
        private String message;

        public Boolean getError() {
            return error;
        }

        public void setError(Boolean error) {
            this.error = error;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public class Data1 {

        @SerializedName("empId")
        @Expose
        private Integer empId;
        @SerializedName("empName")
        @Expose
        private String empName;
        @SerializedName("empCode")
        @Expose
        private String empCode;
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

    }


}