package com.lng.lngattendancesystem.Models.ShiftDetails;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.Expose;

public class ShiftData {

    @SerializedName("shiftType")
    @Expose
    private String shiftType;
    @SerializedName("empId")
    @Expose
    private Integer empId;
    @SerializedName("shiftStart")
    @Expose
    private String shiftStart;
    @SerializedName("shiftEnd")
    @Expose
    private String shiftEnd;
    @SerializedName("custId")
    @Expose
    private Integer custId;
    @SerializedName("outPermissibleTime")
    @Expose
    private String outPermissibleTime;
    @SerializedName("isBreakShift")
    @Expose
    private Boolean isBreakShift;


    @SerializedName("noOfBreakShift")
    @Expose
    private Integer noOfBreakShift;

    @SerializedName("empAppSetupStatus")
    @Expose
    private boolean empAppSetupStatus;


    @SerializedName("attDeviceAllowed")
    @Expose
    private String attDeviceAllowed;

    public String getAttDeviceAllowed() {
        return attDeviceAllowed;
    }

    public void setAttDeviceAllowed(String attDeviceAllowed) {
        this.attDeviceAllowed = attDeviceAllowed;
    }

    public boolean isEmpAppSetupStatus() {
        return empAppSetupStatus;
    }

    public void setEmpAppSetupStatus(boolean empAppSetupStatus) {
        this.empAppSetupStatus = empAppSetupStatus;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(String shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public String getOutPermissibleTime() {
        return outPermissibleTime;
    }

    public void setOutPermissibleTime(String outPermissibleTime) {
        this.outPermissibleTime = outPermissibleTime;
    }

    public Boolean getIsBreakShift() {
        return isBreakShift;
    }

    public void setIsBreakShift(Boolean isBreakShift) {
        this.isBreakShift = isBreakShift;
    }

    public Integer getNoOfBreakShift() {
        return noOfBreakShift;
    }

    public void setNoOfBreakShift(Integer noOfBreakShift) {
        this.noOfBreakShift = noOfBreakShift;
    }

}
