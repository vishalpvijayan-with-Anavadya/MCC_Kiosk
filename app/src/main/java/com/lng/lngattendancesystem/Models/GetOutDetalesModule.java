package com.lng.lngattendancesystem.Models;

public class GetOutDetalesModule {

    int  refEmpId;
    String empAttendanceDate;
    String empAttendanceOutMode;
    String empAttendanceOutDatetime;
    double empAttendanceOutConfidence;
    String empAttendanceOutLatLong;
    String empAttendanceOutLocation;

    public String getEmpAttendanceOutLocation() {
        return empAttendanceOutLocation;
    }

    public void setEmpAttendanceOutLocation(String empAttendanceOutLocation) {
        this.empAttendanceOutLocation = empAttendanceOutLocation;
    }

    public int getRefEmpId() {
        return refEmpId;
    }

    public void setRefEmpId(int refEmpId) {
        this.refEmpId = refEmpId;
    }

    public String getEmpAttendanceDate(){
        return empAttendanceDate;
    }

    public void setEmpAttendanceDate(String empAttendanceDate) {
        this.empAttendanceDate = empAttendanceDate;
    }

    public String getEmpAttendanceOutMode() {
        return empAttendanceOutMode;
    }

    public void setEmpAttendanceOutMode(String empAttendanceOutMode) {
        this.empAttendanceOutMode = empAttendanceOutMode;
    }

    public String getEmpAttendanceOutDatetime() {
        return empAttendanceOutDatetime;
    }

    public void setEmpAttendanceOutDatetime(String empAttendanceOutDatetime) {
        this.empAttendanceOutDatetime = empAttendanceOutDatetime;
    }

    public double getEmpAttendanceOutConfidence() {
        return empAttendanceOutConfidence;
    }

    public void setEmpAttendanceOutConfidence(double empAttendanceOutConfidence) {
        this.empAttendanceOutConfidence = empAttendanceOutConfidence;
    }

    public String getEmpAttendanceOutLatLong() {
        return empAttendanceOutLatLong;
    }

    public void setEmpAttendanceOutLatLong(String empAttendanceOutLatLong) {
        this.empAttendanceOutLatLong = empAttendanceOutLatLong;
    }
}
