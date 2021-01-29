package com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule;

public class GetAttendanceData {

    String EmpId;
    String empPersistedFaceId;
    String attendanceDate;
    String attendanceIndateTime;
    String attendanceOutdateTime;
    String attendanceInConfidence;
    String attendanceOutConfidence;
    String insync;
    String outSync;

    public String getEmpId() {
        return EmpId;
    }

    public void setEmpId(String empId) {
        EmpId = empId;
    }

    public String getEmpPersistedFaceId() {
        return empPersistedFaceId;
    }

    public void setEmpPersistedFaceId(String empPersistedFaceId) {
        this.empPersistedFaceId = empPersistedFaceId;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getAttendanceIndateTime() {
        return attendanceIndateTime;
    }

    public void setAttendanceIndateTime(String attendanceIndateTime) {
        this.attendanceIndateTime = attendanceIndateTime;
    }

    public String getAttendanceOutdateTime() {
        return attendanceOutdateTime;
    }

    public void setAttendanceOutdateTime(String attendanceOutdateTime) {
        this.attendanceOutdateTime = attendanceOutdateTime;
    }

    public String getAttendanceInConfidence() {
        return attendanceInConfidence;
    }

    public void setAttendanceInConfidence(String attendanceInConfidence) {
        this.attendanceInConfidence = attendanceInConfidence;
    }

    public String getAttendanceOutConfidence() {
        return attendanceOutConfidence;
    }

    public void setAttendanceOutConfidence(String attendanceOutConfidence) {
        this.attendanceOutConfidence = attendanceOutConfidence;
    }

    public String getInsync() {
        return insync;
    }

    public void setInsync(String insync) {
        this.insync = insync;
    }

    public String getOutSync() {
        return outSync;
    }

    public void setOutSync(String outSync) {
        this.outSync = outSync;
    }
}
