package com.lng.lngattendancesystem.Models;

public class AttendenceDetails {

    int refEmpId;
    String empAttendanceDate;
    String empAttendanceInMode;
    String empAttendanceInDatetime;
    double empAttendanceInConfidence;
    String empAttendanceInLatLong;
    String empAttendanceInLocation;
    String empAttendanceOutMode;
    String empAttendanceOutDatetime;
    double empAttendanceOutConfidence;
    String empAttendanceOutLatLong;
    String empAttendanceOutLocation;
    String empAttendanceDateTime;
    double temperatureValue;
    int inSync, outSync;

    /**
     *  used Variables For new Attendance Logic
     */

    private String attendanceDate;
    private int attendanceID;
    private int empId;
    private String attendanceDateTime;
    private String attendanceMode;
    private String latLong;
    private String address;
    private double empTemp;
    private String emergency;
    private String inOrOut;
    private String custId;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getAttendanceDateTime() {
        return attendanceDateTime;
    }

    public void setAttendanceDateTime(String attendanceDateTime) {
        this.attendanceDateTime = attendanceDateTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getEmpTemp() {
        return empTemp;
    }

    public void setEmpTemp(double empTemp) {
        this.empTemp = empTemp;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }

    public int getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(int attendanceID) {
        this.attendanceID = attendanceID;
    }


    public String getAttendanceMode() {
        return attendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        this.attendanceMode = attendanceMode;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }


    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    /********************************************Above Values are  used For New Attendance Logic***********
     */











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

    public String getEmpAttendanceInLocation() {
        return empAttendanceInLocation;
    }

    public void setEmpAttendanceInLocation(String empAttendanceInLocation) {
        this.empAttendanceInLocation = empAttendanceInLocation;
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

    public String getEmpAttendanceOutLocation() {
        return empAttendanceOutLocation;
    }

    public void setEmpAttendanceOutLocation(String empAttendanceOutLocation) {
        this.empAttendanceOutLocation = empAttendanceOutLocation;
    }

    public int getInSync() {
        return inSync;
    }

    public void setInSync(int inSync) {
        this.inSync = inSync;
    }

    public int getOutSync() {
        return outSync;
    }

    public void setOutSync(int outSync) {
        this.outSync = outSync;
    }

    public String getEmpAttendanceDateTime() {
        return empAttendanceDateTime;
    }

    public void setEmpAttendanceDateTime(String empAttendanceDateTime) {
        this.empAttendanceDateTime = empAttendanceDateTime;
    }

    public double getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(double temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    @Override
    public String toString() {
        return "AttendenceDetails{" +
                "empAttendanceDateTime='" + empAttendanceDateTime + '\'' +
                ", temperatureValue=" + temperatureValue +
                ", attendanceID=" + attendanceID +
                ", empId=" + empId +
                ", attendanceDateTime='" + attendanceDateTime + '\'' +
                ", attendanceMode='" + attendanceMode + '\'' +
                ", latLong='" + latLong + '\'' +
                ", address='" + address + '\'' +
                ", empTemp=" + empTemp +
                ", InOrOut='" + inOrOut + '\'' +
                ", custId='" + custId + '\'' +
                '}';
    }
}
