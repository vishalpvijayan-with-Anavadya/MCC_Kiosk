package com.lng.lngattendancesystem.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class UserSession {

    private static final String TAG = UserSession.class.getSimpleName();
    private static final String PREF_NAME = "lng_attendance";
    private static final String Key_Reg_Compleeted = "isRegCompleeted";
    private static final String KEY_OTP = "otp";
    private static final String KEY_CUSTOMER_Code = "customerCode";
    private static final String KEY_Branch_Code = "branchCode";

    private static final String KEY_BLOCK_ID = "blockId";
    private static final String KEY_BLOCK_NAME = "blockName";


    private static final String KEY_EMP_ID = "empId";
    private static final String KEY_EMP_NAME = "empName";
    private static final String KEY_EMp_PERSISTEDFACE_ID = "persistedFaceId";

    private static final String KEY_EMP_PIC = "empPic";
    private static final String KEY_SERVER_DATE_TIME = "serverDateTime";

    private static final String KEY_MOBILE_NO = "mobileno";

    private static final String KEY_SHIFT_TYPE = "shiftType";
    private static final String KEY_EMERGENCY_OUT = "emergencyOut";

    private static final String KEY_BR_CODE = "brCode";
    private static final String CUSTOMER_ID = "custID";
    private static final String KEY_TEMERATURE_THRUSHOLD = "TEMERATURE_THRUSHOLD";

    private static final String CHECK_TIME_TO_APICALL = "checkApiTime";

    private static final String KEY_KIOSK_NUMBER = "KIOSK_NUMBER";

    private static final String KEY_LOCATION_ADDRESS = "locationAddress";


    private static final String KEY_LATTITIDE = "lattitude";

    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_FEVO_DEVICE = "FEVO_DEVICE";
    private static final String KEY_FEVO_DEVICE_STATUS = "FEVO_DEVICE_Status";
    private static final String KEY_THERMAL_MODE = "THERMAL_MODE";
    private static final String KEY_ANDROID_ID = "ANDROID_ID";
    private static final String KEY_APP_STATUS = "appStatus";
    private static final String KEY_EMP_CODE = "empCode";

    private static final String KEY_DEVICE_ALLOWED = "deviceAllowed";

    private static final String KEY_ATTENDANCE_DATE = "attendanceDate";

    private static final String KEY_IS_FRESH_INSATALL = "isFreshInstall";

    private static final String KEY_PARGE_PASSWORD = "pergePassword";


    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public UserSession(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }


    public String getPergePassword() {
        return prefs.getString(KEY_PARGE_PASSWORD, null);
    }

    public void setPergePassword(String pergePassword) {
        editor.putString(KEY_PARGE_PASSWORD, pergePassword);
        editor.apply();
    }




    public  void setIsregistred(boolean isRegister) {
        editor.putBoolean(KEY_IS_FRESH_INSATALL, isRegister);
        editor.apply();
    }




    public boolean getIsRegistred() {
        return prefs.getBoolean(KEY_IS_FRESH_INSATALL, false);
    }


    public String getAttendanceDate() {
        return prefs.getString(KEY_ATTENDANCE_DATE, null);
    }

    public void setAttendanceDate(String attendanceDate) {
        editor.putString(KEY_ATTENDANCE_DATE, attendanceDate);
        editor.apply();
    }


    public String getDeviceAllow() {
        return prefs.getString(KEY_DEVICE_ALLOWED, null);
    }

    public void setDeviceAllow(String deviceAllow) {
        editor.putString(KEY_DEVICE_ALLOWED, deviceAllow);
        editor.apply();
    }


    public String getEmpCode() {
        return prefs.getString(KEY_EMP_CODE, null);
    }

    public void setEmpCode(String EmpCode) {
        editor.putString(KEY_EMP_CODE, EmpCode);
        editor.apply();
    }





    public boolean getAppStatus() {
        return prefs.getBoolean(KEY_APP_STATUS, false);
    }

    public void setAppStatus(boolean appStatus) {
        editor.putBoolean(KEY_APP_STATUS, appStatus);
        editor.apply();
    }





    public String getLattitude() {
        return prefs.getString(KEY_LATTITIDE, null);
    }

    public void setLattitude(String lattitude) {
        editor.putString(KEY_LATTITIDE, lattitude);
        editor.apply();
    }


    public boolean getThermalDeviceStatus() {
        return prefs.getBoolean(KEY_FEVO_DEVICE_STATUS, false);
    }

    public void setThermalDeviceStatus(boolean status) {
        editor.putBoolean(KEY_FEVO_DEVICE_STATUS, status);
        editor.apply();
    }

    public boolean isFevoDeviceConnected() {
        return prefs.getBoolean(KEY_FEVO_DEVICE, false);
    }

    public void setFevoDeviceStatus(boolean status) {
        editor.putBoolean(KEY_FEVO_DEVICE, status);
        editor.apply();
    }


    public String getLogitude() {
        return prefs.getString(KEY_LONGITUDE, null);
    }

    public void setLongitude(String longitude) {
        editor.putString(KEY_LONGITUDE, longitude);
        editor.apply();
    }


    public String getApiTime() {
        return prefs.getString(CHECK_TIME_TO_APICALL, null);
    }

    public void setApiTime(String apiTime) {
        editor.putString(CHECK_TIME_TO_APICALL, apiTime);
        editor.apply();
    }
public String getAndroidDeviceID() {
        return prefs.getString(KEY_ANDROID_ID, null);
    }

    public void setAndroidDeviceID(String deviceID) {
        editor.putString(KEY_ANDROID_ID, deviceID);
        editor.apply();
    }


    public String getLocationAddress() {
        return prefs.getString(KEY_LOCATION_ADDRESS, null);
    }

    public void setLocationAddress(String locationAddress) {
        editor.putString(KEY_LOCATION_ADDRESS, locationAddress);
        editor.apply();
    }


    public int getThermalDeviceMode() {
        return prefs.getInt(KEY_THERMAL_MODE, -1);
    }

    public void setThermalMode(int mode) {
        editor.putInt(KEY_THERMAL_MODE, mode);
        editor.apply();
    }


    public int getBrID() {
        return prefs.getInt(KEY_BR_CODE, -1);
    }

    public void setBrID(int brCode) {
        editor.putInt(KEY_BR_CODE, brCode);
        editor.apply();
    }


    public int getCustId() {
        return prefs.getInt(CUSTOMER_ID, -1);
    }

    public void setCustId(int custId) {
        editor.putInt(CUSTOMER_ID, custId);
        editor.apply();
    }


    public int getOTP() {
        return prefs.getInt(KEY_OTP, -1);
    }

    public void setOTP(int OTP) {
        editor.putInt(KEY_OTP, OTP);
        editor.apply();
    }

    public int getEmpId() {
        return prefs.getInt(KEY_EMP_ID, -1);
    }

    public void setEmpId(int empId) {
        editor.putInt(KEY_EMP_ID, empId);
        editor.apply();
    }

    public float getThreshold() {
        return prefs.getFloat(KEY_TEMERATURE_THRUSHOLD, 95.5f);
    }

    public void setThreshold(float value) {
        editor.putFloat(KEY_TEMERATURE_THRUSHOLD, value);
        editor.apply();
    }

    public String getMobileNo() {
        return prefs.getString(KEY_MOBILE_NO, null);
    }

    public void setMobileNO(String empMobileno) {
        editor.putString(KEY_MOBILE_NO, empMobileno);
        editor.apply();
    }


    public String getShiftType() {
        return prefs.getString(KEY_SHIFT_TYPE, null);
    }

    public void setShifttype(String shifttype) {
        editor.putString(KEY_SHIFT_TYPE, shifttype);
        editor.apply();
    }


    public String getServerDateAndTime() {
        return prefs.getString(KEY_SERVER_DATE_TIME, null);
    }

    public void setServerDateTime(String dateTime) {
        editor.putString(KEY_SERVER_DATE_TIME, dateTime);
        editor.apply();
    }


    public String getEmpName() {
        return prefs.getString(KEY_EMP_NAME, null);
    }

    public void setEmpName(String empName) {
        editor.putString(KEY_EMP_NAME, empName);
        editor.apply();
    }


    public void setKeyKioskNumber(String number) {
        editor.putString(KEY_KIOSK_NUMBER, number);
        editor.apply();

    }

    public String getKioskNumber() {
        return prefs.getString(KEY_KIOSK_NUMBER, null);
    }

    public String getPersistedFaceId() {
        return prefs.getString(KEY_EMp_PERSISTEDFACE_ID, null);
    }

    public void setPersistedFaceId(String persistedFaceId) {
        editor.putString(KEY_EMp_PERSISTEDFACE_ID, persistedFaceId);
        editor.apply();
    }


    public String getEmpPic() {
        return prefs.getString(KEY_EMP_PIC, null);
    }

    public void setEmpPic(String empPic) {
        editor.putString(KEY_EMP_PIC, empPic);
        editor.apply();
    }


    public String getBranchCode() {
        return prefs.getString(KEY_Branch_Code, null);
    }

    public void setBranchcode(String branchCode) {
        editor.putString(KEY_Branch_Code, branchCode);
        editor.apply();
    }


    /**
     * private static final String KEY_BLOCK_ID= "blockId";
     * private static final String KEY_BLOCK_NAME = "blockName";
     *
     * @return
     */

    public int getBlockId() {
        return prefs.getInt(KEY_BLOCK_ID, -1);
    }

    public void setBlockId(int blockId) {
        editor.putInt(KEY_BLOCK_ID, blockId);
        editor.apply();
    }


    public String getBlockName() {
        return prefs.getString(KEY_BLOCK_NAME, null);
    }

    public void setBlockName(String blockName) {
        editor.putString(KEY_BLOCK_NAME, blockName);
        editor.apply();
    }


    public String getCustomerCode() {
        return prefs.getString(KEY_CUSTOMER_Code, null);
    }

    public void setCustomerCode(String branchCode) {
        editor.putString(KEY_CUSTOMER_Code, branchCode);
        editor.apply();
    }


    public boolean isRegCompleeted() {
        return prefs.getBoolean(Key_Reg_Compleeted, false);
    }

    public void setRegiCompleeted(boolean isreg) {
        editor.putBoolean(Key_Reg_Compleeted, isreg);
        editor.apply();
    }


    public boolean isEmergencyOut() {
        return prefs.getBoolean(KEY_EMERGENCY_OUT, false);
    }

    public void setEmergencyOut(boolean isEmergencyOut) {
        editor.putBoolean(KEY_EMERGENCY_OUT, isEmergencyOut);
        editor.apply();
    }


    public String collectAllDetails() {

        StringBuilder details = new StringBuilder();
        Map<String, ?> keys = prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            details.append("\n" + entry.getKey() + " : " + entry.getValue().toString());
        }

        return new String(details);
    }

    public void clearUserSession() {
        editor.clear();
        editor.commit();
    }

}
