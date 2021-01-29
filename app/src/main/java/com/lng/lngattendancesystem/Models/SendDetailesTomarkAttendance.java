package com.lng.lngattendancesystem.Models;

public class SendDetailesTomarkAttendance {


    int refEmpId;
    String empAttendanceDate;
    String empAttendanceInMode;
    String empAttendanceInDatetime;
    double empAttendanceInConfidence;
    String empAttendanceInLatLong;
    String empAttendanceInLocation;

    public String getEmpAttendanceInLocation() {
        return empAttendanceInLocation;
    }

    public void setEmpAttendanceInLocation(String empAttendanceInLocation) {
        this.empAttendanceInLocation = empAttendanceInLocation;
    }

    public int getRefEmpId(){
        return refEmpId;
    }

    public void setRefEmpId(int refEmpId) {
        this.refEmpId = refEmpId;
    }

    public String getEmpAttendanceDate() {
        return empAttendanceDate;
    }

    public void setEmpAttendanceDate(String empAttendanceDate) {
        this.empAttendanceDate = empAttendanceDate;
    }

    public String getEmpAttendanceInMode() {
        return empAttendanceInMode;
    }

    public void setEmpAttendanceInMode(String empAttendanceInMode) {
        this.empAttendanceInMode = empAttendanceInMode;
    }

    public String getEmpAttendanceInDatetime() {
        return empAttendanceInDatetime;
    }

    public void setEmpAttendanceInDatetime(String empAttendanceInDatetime) {
        this.empAttendanceInDatetime = empAttendanceInDatetime;
    }

    public double getEmpAttendanceInConfidence() {
        return empAttendanceInConfidence;
    }

    public void setEmpAttendanceInConfidence(double empAttendanceInConfidence) {
        this.empAttendanceInConfidence = empAttendanceInConfidence;
    }

    public String getEmpAttendanceInLatLong() {
        return empAttendanceInLatLong;
    }

    public void setEmpAttendanceInLatLong(String empAttendanceInLatLong) {
        this.empAttendanceInLatLong = empAttendanceInLatLong;
    }
}
