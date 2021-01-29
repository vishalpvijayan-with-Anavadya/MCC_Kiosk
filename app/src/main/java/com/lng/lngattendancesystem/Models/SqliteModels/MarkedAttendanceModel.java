package com.lng.lngattendancesystem.Models.SqliteModels;

public class MarkedAttendanceModel {
    int refEmpId;
    String empAttendanceDate;
    String empAttendanceInMode;
    String empAttendanceInDatetime;
    double empAttendanceInConfidence;
    String empAttendanceInLatLong;
    String persistanceFaceiD;

    public MarkedAttendanceModel() {

    }

    public MarkedAttendanceModel(int refEmpId, String empAttendanceDate, String empAttendanceInMode, String empAttendanceInDatetime, double empAttendanceInConfidence, String empAttendanceInLatLong) {
        this.refEmpId = refEmpId;
        this.empAttendanceDate = empAttendanceDate;
        this.empAttendanceInMode = empAttendanceInMode;
        this.empAttendanceInDatetime = empAttendanceInDatetime;
        this.empAttendanceInConfidence = empAttendanceInConfidence;
        this.empAttendanceInLatLong = empAttendanceInLatLong;
    }

    public String getPersistanceFaceiD() {
        return persistanceFaceiD;
    }

    public void setPersistanceFaceiD(String persistanceFaceiD) {
        this.persistanceFaceiD = persistanceFaceiD;
    }

    public int getRefEmpId() {
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
