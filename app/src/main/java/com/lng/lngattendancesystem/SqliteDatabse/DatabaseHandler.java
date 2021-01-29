package com.lng.lngattendancesystem.SqliteDatabse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.lng.lngattendancesystem.Models.AttendenceDetails;
import com.lng.lngattendancesystem.Models.RestoreEmployeeDetales.EmployeeDetail;
import com.lng.lngattendancesystem.Models.ShiftDetails.ShiftData;
import com.lng.lngattendancesystem.Models.SqliteModels.MarkedAttendanceModel;
import com.lng.lngattendancesystem.Models.SqliteModels.UpdateOutDetales;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.GetAttendanceData;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.InsertCustomerDitales;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AttendanceTabDatabase.db";

    //make it 5 when pushing Playstore
    private static final int DATABASE_VERSION = 5;
    private static final String KEY_ID = "id";


    // create tmcustomer
    public static final String TABLE_CUSTOMER = "tmCustomer";
    public static final String KEY_CUSTOMER_ID = "customerId";
    public static final String KEY_BRANCH_ID = "branchId";
    public static final String KEY_CUSTOMER_LOGO = "customerLogo";
    public static final String KEY_CUSTOMER_NAME = "customerName";
    public static final String KEY_BRANCH_NAME = "branchName";
    public static final String KEY_EMP_GROUP_FACE_LIST = "empGroupFaceList";


    //creating employeee table

    private static final String TABLE_EMPLOYEE = "tmEmployee";
    private static final String KEY_EMP_NAME = "empName";
    private static final String KEY_EMP_ID = "EmpId";
    private static final String KEY_PERSISTED_ID = "empPersistedFaceId";
    private static final String KEY_SHIFT_TYPE = "shiftType";
    private static final String KEY_EMPCODE = "empCode";


    //creating tmAttendance table

    private static final String TABLE_ATTENDANCE = "tmAttendance";
    private static final String KEY_TM_EMP_ID = "EmpId";
    private static final String KEY_TM_PERSISTED_ID = "empPersistedFaceId";
    private static final String KEY_ATTENDANCE_DATE = "attendanceDate";
    private static final String KEY_ATTENDANCE_IN_DATE_TIME = "attendanceIndateTime";
    private static final String KEY_ATTENDANCE_OUT_DATE_TIME = "attendanceOutdateTime";
    private static final String KEY_IN_CONFIDENCE = "attendanceInConfidence";
    private static final String KEY_OUT_CONFIDENCE = "attendanceOutConfidence";
    private static final String KEY_IN_SYNC = "insync";
    private static final String KEY_OUT_SYNC = "outSync";
    private static final String TABLE_SHIFT = "tmShift";
    private static final String KEY_SHIFT_EMP_ID = "shiftEmpId";
    private static final String KEY_SHIFT_CUST_ID = "shiftCustId";
    private static final String KEY_SHIFTT_TYPE = "shiftType";
    private static final String KEY_SHIFT_START_TIME = "shiftStart";
    private static final String KEY_SHIFT_END_TIME = "shiftEnd";
    private static final String KEY_OUT_PERMISSIBLE_TIME = "outPermissibleTime";


    private static final String TABLE_ATTENDANCE_FAILED = "tmNonShiftAttendance";

    private static final String KEY_ATTENDANCE_DATE_TIME = "attendanceDateTime";
    private static final String KEY_IS_EMERGENCY_OUT = "isEmergencyOut";
    private static final String KEY_TEMPERATURE = "temperature";


    /**
     * Created By Nagaraj on 31-12-2020
     * New Table For Failed Attendance
     */

    private static final String TABLE_FAILED_NEW_ATTENDANCE = "tmFailedAttendance";
    private static final String KEY_FAILED_EMP_ID = "FailedEmpId";
    private static final String KEY_ATTENDANCE_FAILED_DATE = "attendanceFailedDate";
    private static final String KEY_ATTENDANCE_FAILED_DATE_TIME = "attendanceFailedDateTime";
    private static final String KEY_IS_FAILED_EMERGENCY_OUT = "isFailedEmergencyOut";
    private static final String KEY_FAILED_TEMPERATURE = "failedTemperature";
    private static final String KEY_FAILED_IN_OR_OUT = "failedInOrOut";
    private static final String KEY_NEW_LAT_LONG = "latLong";
    private static final String KEY_NEW_ADDRESS = "address";
    private static final String KEY_NEW_ATTENDANCE_MODE = "attendanceMode";
    private static final String KEY_FAILED_CUSTOMER_ID = "failedCustomerId";


    Context mcontex;
    SQLiteDatabase db;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mcontex = context;
        Log.i("DVB onCreate", "onCreate: ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {


            String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_CUSTOMER_ID + " TEXT,"
                    + KEY_BRANCH_ID + " TEXT,"
                    + KEY_CUSTOMER_LOGO + " BLOB,"
                    + KEY_CUSTOMER_NAME + " TEXT,"
                    + KEY_BRANCH_NAME + " TEXT,"
                    + KEY_EMP_GROUP_FACE_LIST + " TEXT" + ")";


            String CREATE_EMPLOYEE_TABLE = "CREATE TABLE " + TABLE_EMPLOYEE + "("
                    + KEY_EMP_ID + " TEXT PRIMARY KEY,"
                    + KEY_EMP_NAME + " TEXT,"
                    + KEY_PERSISTED_ID + " TEXT,"
                    + KEY_EMPCODE + " TEXT,"
                    + KEY_SHIFT_TYPE + " TEXT" + ")";

            String CREATE_ATTENDANCE_TABLE = "CREATE TABLE " + TABLE_ATTENDANCE + "("
                    + KEY_TM_EMP_ID + " TEXT PRIMARY KEY,"
                    + KEY_TM_PERSISTED_ID + " TEXT,"
                    + KEY_ATTENDANCE_DATE + " TEXT,"
                    + KEY_ATTENDANCE_IN_DATE_TIME + " TEXT,"
                    + KEY_ATTENDANCE_OUT_DATE_TIME + " TEXT,"
                    + KEY_IN_CONFIDENCE + " TEXT,"
                    + KEY_OUT_CONFIDENCE + " TEXT,"
                    + KEY_IN_SYNC + " TEXT,"
                    + KEY_OUT_SYNC + " TEXT" + ")";


            String CREATE_SHIFT_TABLE = "CREATE TABLE " + TABLE_SHIFT + "("
                    + KEY_SHIFT_EMP_ID + " TEXT PRIMARY KEY,"
                    + KEY_SHIFT_CUST_ID + " TEXT,"
                    + KEY_SHIFT_START_TIME + " TEXT,"
                    + KEY_SHIFT_END_TIME + " TEXT,"
                    + KEY_SHIFTT_TYPE + " TEXT,"
                    + KEY_OUT_PERMISSIBLE_TIME + " TEXT" + ")";


            String CREATE_FAILED_ATTENDANCE_TABLE = "CREATE TABLE " + TABLE_ATTENDANCE_FAILED + "("
                    + KEY_TM_EMP_ID + " TEXT PRIMARY KEY,"
                    + KEY_ATTENDANCE_DATE + " TEXT,"
                    + KEY_ATTENDANCE_DATE_TIME + " TEXT,"
                    + KEY_IS_EMERGENCY_OUT + " TEXT,"
                    + KEY_TEMPERATURE + " TEXT" + ")";


            String CREATE_ATTENDANCE_NEW_FAILED_TABLE = "CREATE TABLE " + TABLE_FAILED_NEW_ATTENDANCE + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + KEY_FAILED_EMP_ID + " TEXT,"
                    + KEY_ATTENDANCE_FAILED_DATE + " TEXT,"
                    + KEY_ATTENDANCE_FAILED_DATE_TIME + " TEXT,"
                    + KEY_IS_FAILED_EMERGENCY_OUT + " TEXT,"
                    + KEY_FAILED_IN_OR_OUT + " TEXT,"
                    + KEY_NEW_LAT_LONG + " TEXT,"
                    + KEY_NEW_ADDRESS + " TEXT,"
                    + KEY_FAILED_CUSTOMER_ID + " TEXT,"
                    + KEY_NEW_ATTENDANCE_MODE + " TEXT,"
                    + KEY_FAILED_TEMPERATURE + " TEXT" + ")";


            db.execSQL(CREATE_CUSTOMER_TABLE);
            db.execSQL(CREATE_EMPLOYEE_TABLE);
            db.execSQL(CREATE_ATTENDANCE_TABLE);
            db.execSQL(CREATE_SHIFT_TABLE);
            db.execSQL(CREATE_FAILED_ATTENDANCE_TABLE);
            db.execSQL(CREATE_ATTENDANCE_NEW_FAILED_TABLE);


        } catch (Exception e) {
            Toast.makeText(mcontex, "Databse NOT created" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL(CREATE_FAILED_ATTENDANCE_TABLE);
        onCreate(db);*/
        Log.d("TAG", "onUpgrade: table");

        try {

            String CREATE_ATTENDANCE_NEW_FAILED_TABLE = "CREATE TABLE " + TABLE_FAILED_NEW_ATTENDANCE + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + KEY_FAILED_EMP_ID + " TEXT,"
                    + KEY_ATTENDANCE_FAILED_DATE + " TEXT,"
                    + KEY_ATTENDANCE_FAILED_DATE_TIME + " TEXT,"
                    + KEY_IS_FAILED_EMERGENCY_OUT + " TEXT,"
                    + KEY_FAILED_IN_OR_OUT + " TEXT,"
                    + KEY_NEW_LAT_LONG + " TEXT,"
                    + KEY_NEW_ADDRESS + " TEXT,"
                    + KEY_FAILED_CUSTOMER_ID + " TEXT,"
                    + KEY_NEW_ATTENDANCE_MODE + " TEXT,"
                    + KEY_FAILED_TEMPERATURE + " TEXT" + ")";

            db.execSQL(CREATE_ATTENDANCE_NEW_FAILED_TABLE);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "onUpgrade: e");
        }

        try {

            db.execSQL("ALTER table tmNonShiftAttendance add COLUMN temperature TEXT");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.d("TAG", "TESTTABLE");
            db.execSQL("ALTER table tmEmployee add COLUMN empCode TEXT");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean addCustomerDitailes(InsertCustomerDitales insertCustomerDitales) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long result;
        Cursor c = null;
        try {
            int NoOfRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_CUSTOMER);
            if (NoOfRows == 0) {
                values.put(KEY_CUSTOMER_ID, insertCustomerDitales.getCustomerId());
                values.put(KEY_BRANCH_ID, insertCustomerDitales.getBranchId());
                values.put(KEY_CUSTOMER_LOGO, insertCustomerDitales.getCustomerLogo());
                values.put(KEY_CUSTOMER_NAME, insertCustomerDitales.getCustomerName());
                values.put(KEY_BRANCH_NAME, insertCustomerDitales.getBranchName());
                values.put(KEY_EMP_GROUP_FACE_LIST, insertCustomerDitales.getEmpGroupFaceList());
                result = db.insert(TABLE_CUSTOMER, null, values);
                if (result != -1) {
                    db.close();
                    return true;
                } else {
                    db.close();
                    return false;
                }
            } else {
                values.put(KEY_CUSTOMER_ID, insertCustomerDitales.getCustomerId());
                values.put(KEY_BRANCH_ID, insertCustomerDitales.getBranchId());
                values.put(KEY_CUSTOMER_LOGO, insertCustomerDitales.getCustomerLogo());
                values.put(KEY_CUSTOMER_NAME, insertCustomerDitales.getCustomerName());
                values.put(KEY_BRANCH_NAME, insertCustomerDitales.getBranchName());
                values.put(KEY_EMP_GROUP_FACE_LIST, insertCustomerDitales.getEmpGroupFaceList());
                result = db.update(TABLE_CUSTOMER, values, "id=1", null);
                if (result != -1) {
                    db.close();
                    return true;
                } else {
                    db.close();
                    return false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;

    }


    public boolean updateCustomerLogo(byte[] logo) {
        try {
            ContentValues values = new ContentValues();
            SQLiteDatabase db = this.getWritableDatabase();
            values.put(KEY_CUSTOMER_LOGO, logo);
            db.update(TABLE_CUSTOMER, values, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    public String geCustomerId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.query(TABLE_CUSTOMER, new String[]{KEY_CUSTOMER_ID}, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String customerId = cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_ID));
                db.close();
                return customerId;
            } else {
                db.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(mcontex, "Something went wrong while getting data", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;

    }

    public List<getCustomerAllDitailes> getCustomerAllrecords() {
        ArrayList<getCustomerAllDitailes> listofCustomerRecords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "Select * from " + TABLE_CUSTOMER;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                getCustomerAllDitailes customerRecords = new getCustomerAllDitailes();
                customerRecords.setCustomerId(cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                customerRecords.setCustomerLogo(cursor.getBlob(cursor.getColumnIndex(KEY_CUSTOMER_LOGO)));
                customerRecords.setBranchId(cursor.getString(cursor.getColumnIndex(KEY_BRANCH_ID)));
                customerRecords.setCustomerName(cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_NAME)));
                customerRecords.setBranchName(cursor.getString(cursor.getColumnIndex(KEY_BRANCH_NAME)));
                listofCustomerRecords.add(customerRecords);
                db.close();
                return listofCustomerRecords;
            } else {
                return null;
            }
        } catch (Exception e) {
            //Toast.makeText(mcontex, "went wrong with database", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;


    }

    public String getEmployeeGroupFacelist() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.query(TABLE_CUSTOMER, new String[]{KEY_EMP_GROUP_FACE_LIST}, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String persistetFaceId = cursor.getString(cursor.getColumnIndex(KEY_EMP_GROUP_FACE_LIST));
                db.close();
                return persistetFaceId;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(mcontex, "Something went wrong while getting data", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;

    }


    public String getEmpIdByFaceId(String FaceID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + KEY_EMP_ID + " from " + TABLE_EMPLOYEE + " where " + KEY_PERSISTED_ID + "='" + FaceID + "'";

        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    return cursor.getString(0);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }// Closing database connection
        return null;


    }


    public String getEmployeeNameByEmpID(String employee) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + KEY_EMP_NAME + " from " + TABLE_EMPLOYEE + " where " + KEY_EMP_ID + "='" + employee + "'";

        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    return cursor.getString(0);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }// Closing database connection
        return null;


    }


    public boolean insertEmprecords(String empId, String empName, String userParsistedFaceId, String shiftType, String empCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long result;
        try {
            values.put(KEY_EMP_ID, empId);
            values.put(KEY_EMP_NAME, empName);
            values.put(KEY_PERSISTED_ID, userParsistedFaceId);
            values.put(KEY_SHIFT_TYPE, shiftType);
            values.put(KEY_EMPCODE, empCode);
            result = db.insert(TABLE_EMPLOYEE, null, values);

            Log.i("Test", "insertEmprecords: " + result);
            return result != -1;
        } catch (Exception e) {
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // Closing database connection
        return false;

    }


    public boolean isEmployeeExist(String empId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + TABLE_EMPLOYEE + " where " + KEY_EMP_ID + "='" + empId + "'";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                return cursor.getCount() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // Closing database connection
        return false;

    }

    public GetAttendanceData isEmployeeExistInAttendance(String empId) {
        SQLiteDatabase db = this.getWritableDatabase();
        GetAttendanceData getAttendanceData;
        String query = "Select * from " + TABLE_ATTENDANCE + " where " + KEY_EMP_ID + "='" + empId + "'";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    getAttendanceData = new GetAttendanceData();
                    getAttendanceData.setEmpId(cursor.getString(cursor.getColumnIndex(KEY_TM_EMP_ID)));
                    getAttendanceData.setEmpPersistedFaceId(cursor.getString(cursor.getColumnIndex(KEY_TM_PERSISTED_ID)));
                    getAttendanceData.setAttendanceDate(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_DATE)));
                    getAttendanceData.setAttendanceIndateTime(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_IN_DATE_TIME)));
                    getAttendanceData.setAttendanceOutdateTime(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_OUT_DATE_TIME)));
                    getAttendanceData.setAttendanceInConfidence(cursor.getString(cursor.getColumnIndex(KEY_IN_CONFIDENCE)));
                    getAttendanceData.setAttendanceOutConfidence(cursor.getString(cursor.getColumnIndex(KEY_OUT_CONFIDENCE)));
                    getAttendanceData.setInsync(cursor.getString(cursor.getColumnIndex(KEY_IN_SYNC)));
                    getAttendanceData.setOutSync(cursor.getString(cursor.getColumnIndex(KEY_OUT_SYNC)));
                    return getAttendanceData;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // Closing database connection
        return null;

    }


    public boolean updateOutDetalsISqlite(UpdateOutDetales updateOutDetales) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //long result = -1;
        Cursor c = null;
        try {
            //int NoOfRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_ATTENDANCE);
            int NoOfRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_ATTENDANCE, KEY_TM_EMP_ID + "='" + updateOutDetales.getRefEmpId() + "'");

            if (NoOfRows == 0) {
                values.put(KEY_TM_EMP_ID, updateOutDetales.getRefEmpId());
                values.put(KEY_ATTENDANCE_DATE, updateOutDetales.getEmpAttendanceDate());
                values.put(KEY_ATTENDANCE_OUT_DATE_TIME, updateOutDetales.getEmpAttendanceOutDatetime());
                values.put(KEY_OUT_CONFIDENCE, updateOutDetales.getEmpAttendanceOutConfidence());
                values.put(KEY_TM_PERSISTED_ID, updateOutDetales.getPersistanceFaceID());
                long result = db.insert(TABLE_ATTENDANCE, null, values);
                if (result == -1) {
                    return false;
                } else {
                    db.close();
                    return true;
                }
            } else {
                values.put(KEY_TM_EMP_ID, updateOutDetales.getRefEmpId());
                values.put(KEY_ATTENDANCE_DATE, updateOutDetales.getEmpAttendanceDate());
                values.put(KEY_ATTENDANCE_OUT_DATE_TIME, updateOutDetales.getEmpAttendanceOutDatetime());
                values.put(KEY_TM_PERSISTED_ID, updateOutDetales.getPersistanceFaceID());
                long result = db.update(TABLE_ATTENDANCE, values, KEY_TM_EMP_ID + "=" + updateOutDetales.getRefEmpId(), null);
                if (result == -1) {
                    return false;
                } else {
                    db.close();
                    return true;
                }

            }
        } catch (Exception e) {
            // Toast.makeText(mcontex, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "DATABASE" + e.getMessage());

            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }


    public boolean addIndetaliesInsqlite(MarkedAttendanceModel markedAttendanceModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //long result = -1;
        Cursor c = null;
        try {
            int NoOfRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_ATTENDANCE, KEY_TM_EMP_ID + "='" + markedAttendanceModel.getRefEmpId() + "'");
            if (NoOfRows == 0) {
                values.put(KEY_TM_EMP_ID, markedAttendanceModel.getRefEmpId());
                values.put(KEY_ATTENDANCE_DATE, markedAttendanceModel.getEmpAttendanceDate());
                values.put(KEY_ATTENDANCE_IN_DATE_TIME, markedAttendanceModel.getEmpAttendanceInDatetime());
                values.put(KEY_IN_CONFIDENCE, markedAttendanceModel.getEmpAttendanceInConfidence());
                values.put(KEY_TM_PERSISTED_ID, markedAttendanceModel.getPersistanceFaceiD());
                long result = db.insert(TABLE_ATTENDANCE, null, values);
                if (result == -1) {
                    return false;
                } else {
                    db.close();
                    return true;
                }
            } else {
                values.put(KEY_TM_EMP_ID, markedAttendanceModel.getRefEmpId());
                values.put(KEY_ATTENDANCE_DATE, markedAttendanceModel.getEmpAttendanceDate());
                values.put(KEY_ATTENDANCE_IN_DATE_TIME, markedAttendanceModel.getEmpAttendanceInDatetime());
                values.put(KEY_TM_PERSISTED_ID, markedAttendanceModel.getPersistanceFaceiD());
                long result = db.update(TABLE_ATTENDANCE, values, KEY_TM_EMP_ID + "=" + markedAttendanceModel.getRefEmpId(), null);
                if (result == -1) {
                    return false;
                } else {
                    db.close();
                    return true;
                }

            }
        } catch (Exception e) {
            // Toast.makeText(mcontex, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "DATABASE" + e.getMessage());

            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    public String getCustomerId() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.query(TABLE_CUSTOMER, new String[]{KEY_CUSTOMER_ID}, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String customerId = cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_ID));
                db.close();
                return customerId;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;

    }

    /**
     * private static final String TABLE_SHIFT = "tmShift";
     * private static final String KEY_SHIFT_EMP_ID = "shiftEmpId";
     * private static final String KEY_SHIFT_CUST_ID = "shiftCustId";
     * private static final String KEY_SHIFT_START_TIME = "shiftStart";
     * private static final String KEY_SHIFT_END_TIME = "shiftEnd";
     * private static final String KEY_OUT_PERMISSIBLE_TIME ="outPermissibleTime";
     *
     * @param shiftData
     * @return
     */

    public boolean insertShiftDetales(ShiftData shiftData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor c = null;
        try {
            int NoOfRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_SHIFT, KEY_SHIFT_EMP_ID + "='" + shiftData.getEmpId() + "'");
            if (NoOfRows == 0) {
                values.put(KEY_SHIFT_EMP_ID, shiftData.getEmpId());
                values.put(KEY_SHIFT_CUST_ID, shiftData.getCustId());
                values.put(KEY_SHIFT_START_TIME, shiftData.getShiftStart());
                values.put(KEY_SHIFT_END_TIME, shiftData.getShiftEnd());
                values.put(KEY_SHIFTT_TYPE, shiftData.getShiftType());
                values.put(KEY_OUT_PERMISSIBLE_TIME, shiftData.getOutPermissibleTime());
                long result = db.insert(TABLE_SHIFT, null, values);
                if (result == -1) {
                    return false;
                } else {
                    db.close();
                    return true;
                }
            } else {
                values.put(KEY_SHIFT_EMP_ID, shiftData.getEmpId());
                values.put(KEY_SHIFT_CUST_ID, shiftData.getCustId());
                values.put(KEY_SHIFT_START_TIME, shiftData.getShiftStart());
                values.put(KEY_SHIFT_END_TIME, shiftData.getShiftEnd());
                values.put(KEY_SHIFTT_TYPE, shiftData.getShiftType());
                values.put(KEY_OUT_PERMISSIBLE_TIME, shiftData.getOutPermissibleTime());
                long result = db.update(TABLE_SHIFT, values, KEY_SHIFT_EMP_ID + "=" + shiftData.getEmpId(), null);
                if (result == -1) {
                    return false;
                } else {
                    db.close();
                    return true;
                }

            }
        } catch (Exception e) {
            // Toast.makeText(mcontex, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "DATABASE" + e.getMessage());

            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;

    }


    public String getPersistedFaceIDByEmpId(String empId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + KEY_PERSISTED_ID + " from " + TABLE_EMPLOYEE + " where " + KEY_EMP_ID + "= " + "'" + empId + "'";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String persistedFaceID = cursor.getString(cursor.getColumnIndex(KEY_PERSISTED_ID));
                    db.close();
                    return persistedFaceID;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // Closing database connection
        return null;


    }


    public int updatePersitedFaecByEmpID(String empId, String persistedFaceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        try {
            String strSQL = "UPDATE " + TABLE_EMPLOYEE + " SET " + KEY_PERSISTED_ID + "=" + "'" + persistedFaceId + "'" + " WHERE " + KEY_EMP_ID + "=" + "'" + empId + "'";
            Log.d("TAG", "QUERY UPDATe" + strSQL);
            db.execSQL(strSQL);
            db.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return -1;


    }


    public int deleteEmpAttendanceRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        int value;
        try {
            value = db.delete(TABLE_ATTENDANCE_FAILED, null, null);
            value = db.delete(TABLE_ATTENDANCE, null, null);
            Log.i("test", "deleteEmpAttendanceRecords: cleaed");
            if (value == 1) {
                return 1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return 0;
    }

    public boolean deleteEmployee(int empID) {
        Log.i("Test", "deleteEmployee: ");
        Toast.makeText(mcontex, "Delete employee called", Toast.LENGTH_SHORT).show();
        String where = KEY_EMP_ID + "=" + empID;
        SQLiteDatabase db = this.getReadableDatabase();
        int value;
        try {
            value = db.delete(TABLE_EMPLOYEE, where, null);
            return value == 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }


    public boolean updateDBOutSync(int outSync, String attendanceDate, String empID) {

        String query = "UPDATE " + TABLE_ATTENDANCE + " set " + KEY_OUT_SYNC + " =" + outSync + " WHERE " + KEY_TM_EMP_ID + "='" + empID + "' AND " + KEY_ATTENDANCE_DATE + " ='" + attendanceDate + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("query ", "updateDBOutSync: " + query);
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }


    public boolean updateDBInsync(int inSync, String attendanceDate, String empID) {
        String query = "UPDATE " + TABLE_ATTENDANCE + " set " + KEY_IN_SYNC + " =" + inSync + " WHERE " + KEY_TM_EMP_ID + "='" + empID + "' AND " + KEY_ATTENDANCE_DATE + " ='" + attendanceDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }


    public boolean saveFailedAttendanceDetails(String empId, String attendaceDate, String attendanceDateTime, String emergency, String tempValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(KEY_EMP_ID, empId);
            values.put(KEY_ATTENDANCE_DATE, attendaceDate);
            values.put(KEY_ATTENDANCE_DATE_TIME, attendanceDateTime);
            values.put(KEY_TEMPERATURE, tempValue);
            if (emergency == null)
                emergency = "0";
            values.put(KEY_IS_EMERGENCY_OUT, emergency);

            long result = db.insert(TABLE_ATTENDANCE_FAILED, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    public boolean isAttendanceExist(String empId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + TABLE_ATTENDANCE_FAILED + " where " + KEY_EMP_ID + "='" + empId + "'";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                return cursor.getCount() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;

    }


    public ArrayList<AttendenceDetails> getAllFailedAttendanceDetails() {
        ArrayList<AttendenceDetails> allDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "Select * from " + TABLE_ATTENDANCE_FAILED;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    AttendenceDetails attendeceDetails = new AttendenceDetails();
                    attendeceDetails.setRefEmpId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TM_EMP_ID))));
                    attendeceDetails.setEmpAttendanceDate(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_DATE)));
                    attendeceDetails.setEmpAttendanceDateTime(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_DATE_TIME)));
                    attendeceDetails.setEmergency(cursor.getString(cursor.getColumnIndex(KEY_IS_EMERGENCY_OUT)));
                    attendeceDetails.setTemperatureValue(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_TEMPERATURE))));
                    allDetails.add(attendeceDetails);
                } while (cursor.moveToNext());
                db.close();
                return allDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;


    }

    public ArrayList<AttendenceDetails> getAllFailedAttendanceOfThisEmployee(String empId) {
        ArrayList<AttendenceDetails> allDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        // String query = "Select * from " + TABLE_ATTENDANCE_FAILED;

        String query = "Select * from " + TABLE_FAILED_NEW_ATTENDANCE + " where " + KEY_FAILED_EMP_ID + " = " + empId;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    AttendenceDetails attendeceDetails = new AttendenceDetails();
                    attendeceDetails.setAttendanceID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                    attendeceDetails.setEmpId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FAILED_EMP_ID))));
                    attendeceDetails.setAttendanceDate(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_FAILED_DATE)));
                    attendeceDetails.setAttendanceDateTime(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_FAILED_DATE_TIME)));
                    attendeceDetails.setEmergency(cursor.getString(cursor.getColumnIndex(KEY_IS_FAILED_EMERGENCY_OUT)));
                    attendeceDetails.setEmpTemp(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_FAILED_TEMPERATURE))));
                    attendeceDetails.setLatLong(cursor.getString(cursor.getColumnIndex(KEY_NEW_LAT_LONG)));
                    attendeceDetails.setAddress(cursor.getString(cursor.getColumnIndex(KEY_NEW_ADDRESS)));
                    attendeceDetails.setCustId(cursor.getString(cursor.getColumnIndex(KEY_FAILED_CUSTOMER_ID)));
                    allDetails.add(attendeceDetails);
                } while (cursor.moveToNext());
                db.close();
                return allDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;


    }


    public ArrayList<AttendenceDetails> getAllFailedAttendanceOfAllEmployee() {
        ArrayList<AttendenceDetails> allDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        // String query = "Select * from " + TABLE_ATTENDANCE_FAILED;

        String query = "Select * from " + TABLE_FAILED_NEW_ATTENDANCE;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    AttendenceDetails attendeceDetails = new AttendenceDetails();
                    attendeceDetails.setAttendanceID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                    attendeceDetails.setEmpId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FAILED_EMP_ID))));
                    attendeceDetails.setAttendanceDate(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_FAILED_DATE)));
                    attendeceDetails.setAttendanceDateTime(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_FAILED_DATE_TIME)));

                    Log.d("TAB", "TESTDB set " + cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_FAILED_DATE_TIME)));
                    Log.d("TAB", "TESTDB get " + attendeceDetails.getAttendanceDateTime());

                    attendeceDetails.setEmergency(cursor.getString(cursor.getColumnIndex(KEY_IS_FAILED_EMERGENCY_OUT)));
                    attendeceDetails.setEmpTemp(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_FAILED_TEMPERATURE))));
                    attendeceDetails.setLatLong(cursor.getString(cursor.getColumnIndex(KEY_NEW_LAT_LONG)));
                    attendeceDetails.setAddress(cursor.getString(cursor.getColumnIndex(KEY_NEW_ADDRESS)));
                    attendeceDetails.setCustId(cursor.getString(cursor.getColumnIndex(KEY_FAILED_CUSTOMER_ID)));
                    attendeceDetails.setAttendanceMode(cursor.getString(cursor.getColumnIndex(KEY_NEW_ATTENDANCE_MODE)));
                    allDetails.add(attendeceDetails);
                } while (cursor.moveToNext());
                db.close();
                return allDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;


    }

    public boolean savedNewFailedAttendanceDetails(AttendenceDetails attendenceDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(KEY_FAILED_EMP_ID, attendenceDetails.getEmpId());
            values.put(KEY_ATTENDANCE_FAILED_DATE, attendenceDetails.getAttendanceDate());
            values.put(KEY_ATTENDANCE_FAILED_DATE_TIME, attendenceDetails.getAttendanceDateTime());
            values.put(KEY_NEW_ATTENDANCE_MODE, attendenceDetails.getAttendanceMode());
            values.put(KEY_FAILED_TEMPERATURE, attendenceDetails.getEmpTemp());
            values.put(KEY_NEW_LAT_LONG, attendenceDetails.getLatLong());
            values.put(KEY_NEW_ADDRESS, attendenceDetails.getAddress());
            values.put(KEY_IS_FAILED_EMERGENCY_OUT, attendenceDetails.getEmergency());
            values.put(KEY_FAILED_CUSTOMER_ID, attendenceDetails.getCustId());
            long result = db.insert(TABLE_FAILED_NEW_ATTENDANCE, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }


    public boolean deletesynchRecord(String empId) {
        String where = KEY_EMP_ID + "=" + empId;
        SQLiteDatabase db = this.getReadableDatabase();
        int value;
        try {
            value = db.delete(TABLE_ATTENDANCE_FAILED, where, null);
            return value == 1;
        } catch (Exception e) {
            Log.d("TAG", "TESTFLOW DB Exception " + e);
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    public ArrayList<EmployeeDetail> getAllEmployeeDetails() {
        ArrayList<EmployeeDetail> employeeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "Select * from " + TABLE_EMPLOYEE;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    EmployeeDetail employee = new EmployeeDetail();
                    employee.setEmpId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_EMP_ID))));
                    employee.setEmpName(cursor.getString(cursor.getColumnIndex(KEY_EMP_NAME)));
                    employee.setEmpPresistedFaceId(cursor.getString(cursor.getColumnIndex(KEY_PERSISTED_ID)));
                    employeeList.add(employee);
                } while (cursor.moveToNext());
                db.close();
                return employeeList;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public boolean insertbreakshiftdetaials(String empId, String attendanceDate, int insynch, int outSynch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long result;
        try {
            values.put(KEY_TM_EMP_ID, empId);
            values.put(KEY_ATTENDANCE_DATE, attendanceDate);
            values.put(KEY_IN_SYNC, insynch);
            values.put(KEY_OUT_SYNC, outSynch);
            result = db.insert(TABLE_ATTENDANCE, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // Closing database connection
        return false;

    }

    public boolean isBreakAttendanceExist(String empId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + TABLE_ATTENDANCE + " where " + KEY_EMP_ID + "='" + empId + "' and " + KEY_ATTENDANCE_DATE + "='" + date + "'";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                return cursor.getCount() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;

    }

    public AttendenceDetails breakShiftAttendanceDetails(String empId, String date) {
        AttendenceDetails attendeceDetails = new AttendenceDetails();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "Select * from " + TABLE_ATTENDANCE + " where " + KEY_EMP_ID + "='" + empId + "' and " + KEY_ATTENDANCE_DATE + "='" + date + "'";
        ;
        Log.i("Test", "breakShiftAttendanceDetails: " + query);
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    attendeceDetails.setRefEmpId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TM_EMP_ID))));
                    attendeceDetails.setEmpAttendanceDate(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_DATE)));
                    attendeceDetails.setInSync(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IN_SYNC))));
                    attendeceDetails.setOutSync(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_OUT_SYNC))));
                    return attendeceDetails;
                } while (cursor.moveToNext());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;

    }


    public boolean deleteAttendance(String date) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_FAILED_NEW_ATTENDANCE, KEY_ATTENDANCE_FAILED_DATE + "='" + date + "'", null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<String> getTaleColumns() {
        ArrayList<String> columnsModelArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "PRAGMA table_info('tmCustomer');";
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String cname = cursor.getString(cursor.getColumnIndex("name"));
                    columnsModelArrayList.add(cname);
                } while (cursor.moveToNext());
            }
            return columnsModelArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return null;
    }


    public boolean getTablesFromSqlite(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = "name=" + "'" + tableName + "'";
        String query = "SELECT name FROM sqlite_master where " + where;
        Cursor cursor;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                db.close();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return false;
    }


    public String getEmpCOde(String empId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select " + KEY_EMPCODE + " from " + TABLE_EMPLOYEE + " where " + KEY_EMP_ID + " = " + empId;
        Log.d("TAG", "TESTCODE query: " + query);
        Cursor cursor;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                String empCode = cursor.getString(cursor.getColumnIndex(KEY_EMPCODE));
                db.close();
                return empCode;
            }
        } catch (Exception e) {
            Toast.makeText(mcontex, "Something wrong with Databse", Toast.LENGTH_SHORT).show();
        } finally {
            if (db.isOpen())
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // Closing database connection
        return null;

    }
}
