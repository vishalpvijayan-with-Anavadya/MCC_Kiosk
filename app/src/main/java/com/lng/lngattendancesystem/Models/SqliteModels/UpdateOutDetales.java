package com.lng.lngattendancesystem.Models.SqliteModels;

public class UpdateOutDetales {
    String persistanceFaceID;
    int refEmpId;
    String empAttendanceDate;
    String empAttendanceOutMode;
    String empAttendanceOutDatetime;
    double empAttendanceOutConfidence;
    String empAttendanceOutLatLong;

    public UpdateOutDetales() {

    }

    public UpdateOutDetales(String persistanceFaceID, int refEmpId, String empAttendanceDate, String empAttendanceOutMode, String empAttendanceOutDatetime, double empAttendanceOutConfidence, String empAttendanceOutLatLong) {
        this.persistanceFaceID = persistanceFaceID;
        this.refEmpId = refEmpId;
        this.empAttendanceDate = empAttendanceDate;
        this.empAttendanceOutMode = empAttendanceOutMode;
        this.empAttendanceOutDatetime = empAttendanceOutDatetime;
        this.empAttendanceOutConfidence = empAttendanceOutConfidence;
        this.empAttendanceOutLatLong = empAttendanceOutLatLong;
    }

    public String getPersistanceFaceID() {
        return persistanceFaceID;
    }

    public void setPersistanceFaceID(String persistanceFaceID) {
        this.persistanceFaceID = persistanceFaceID;
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
