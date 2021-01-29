package com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule;

public class getShiftRecords {
    String shiftType;
    String empId;
    String shiftStart;
    String shiftEnd;
    String custId;
    String outPermissibleTime;


    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
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

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getOutPermissibleTime() {
        return outPermissibleTime;
    }

    public void setOutPermissibleTime(String outPermissibleTime) {
        this.outPermissibleTime = outPermissibleTime;
    }
}
