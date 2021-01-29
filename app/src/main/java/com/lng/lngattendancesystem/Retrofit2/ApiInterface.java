package com.lng.lngattendancesystem.Retrofit2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lng.lngattendancesystem.Models.AttendenceDetails;
import com.lng.lngattendancesystem.Models.BlockDetailsResponseModelsDetails.BlockDetailsResponseModel;
import com.lng.lngattendancesystem.Models.BranchDetailsResponseModelDetails.BranchDetailsResponseModel;
import com.lng.lngattendancesystem.Models.ClearAttRecords.ClearAttResponce;
import com.lng.lngattendancesystem.Models.ConfigurationResponseModels.ConfigurationDetails;
import com.lng.lngattendancesystem.Models.CustomerBranchDetailsResponseModels.CustomerBranchDetailsResponse;
import com.lng.lngattendancesystem.Models.CustomerRegModule.CustomerRequest;
import com.lng.lngattendancesystem.Models.CustomerRegModule.MainCustRegResponce;
import com.lng.lngattendancesystem.Models.EmployeeDetalies.EmpMainOtpResponce;
import com.lng.lngattendancesystem.Models.FaceRegistration.MainRegresponce;
import com.lng.lngattendancesystem.Models.GetDateTimeFromSerevr.GetServerDateTimeResponce;
import com.lng.lngattendancesystem.Models.GetOutDetalesModule;
import com.lng.lngattendancesystem.Models.MarkINModels.AttendanceMarkInReponse;
import com.lng.lngattendancesystem.Models.MarkInModule.MarkedAttendanceStatusResponce;
import com.lng.lngattendancesystem.Models.MarkOutModule.MarkAttendanceOutStatus;
import com.lng.lngattendancesystem.Models.MarkOutResponseModels.AttendanceMarkOutReponse;
import com.lng.lngattendancesystem.Models.NewAttendanceModel.SynchSuccessedData;
import com.lng.lngattendancesystem.Models.NewLogicMarkAtt.MarkAttendanceResponseNew;
import com.lng.lngattendancesystem.Models.NewLogicMarkAtt.SynchResponse.SynchedMainResponse;
import com.lng.lngattendancesystem.Models.RegStatusUpdate.Regresponce;
import com.lng.lngattendancesystem.Models.RestoreEmployeeDetales.RestoreEmpMainResponce;
import com.lng.lngattendancesystem.Models.SendDetailesTomarkAttendance;
import com.lng.lngattendancesystem.Models.ShiftDetails.MainShiftdetalsRespose;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("tab/customer/setup/generateOtpForCustomer")
    Call<MainCustRegResponce> getCustRegister(@Body JsonObject customerRequest);

    @POST("tab/customer/setup/getCustomerBranchDetailsByBrCodeAndCustCode")
    Call<CustomerBranchDetailsResponse> getcustDetalies(@Body JsonObject jsonObject);


    @POST("tab/employee/updateByEmpId")
    Call<Regresponce> getRestatus(@Body JsonObject jsonObject);

    @GET("customer/employee/mark/attendance/getCurrentDate")
    Call<GetServerDateTimeResponce> getDateTimeFromSerever();

    @POST("tab/employee/attendance/markIn")
    Call<MarkedAttendanceStatusResponce> getAttendanceInstatus(@Body JsonObject jsonObject);


    @POST("tab/employee/attendance/markOut")
    Call<MarkAttendanceOutStatus> getMarkOutStatus(@Body JsonObject jsonObject);


    @POST("tab/employee/getEmployeeByMobileno")
    Call<MainRegresponce> getEmployeeDetails(@Body JsonObject jsonObject);

    @POST("tab/employee/generateOtp")
    Call<EmpMainOtpResponce> getEmpOtp(@Body JsonObject jsonObject);


    @POST("tab/employee/getShiftByEmployeeIdAndCustId")
    Call<MainShiftdetalsRespose> getShiftDetails(@Body JsonObject jsonObject);

    @POST("tab/config/getConfigDetailsByCustIdAndBrIdAndKioskNo")
    Call<ConfigurationDetails> getCustomerConfiguration(@Body JsonObject jsonObject);
/*
    @POST("tab/config/getConfigDetailsByCustIdAndBrId")
    Call<ConfigurationDetails> getCustomerConfiguration(@Body JsonObject jsonObject);
*/

    @POST("tab/customer/setup/getBranchListByCustCode")
    Call<BranchDetailsResponseModel> getBranchs(@Body JsonObject jsonObject);

    @POST("tab/customer/setup/getBlockListByBrId")
    Call<BlockDetailsResponseModel> getBlocks(@Body JsonObject jsonObject);

    @POST("tab/employee/getEmployeeDetailsByCustIdAndBrIdAndBlkId")
    Call<RestoreEmpMainResponce> getRestoreEmpDetails(@Body JsonObject jsonObject);


    @POST("tab/employee/deleteAllEmployeeAttendance")
    Call<ClearAttResponce> getClearAttendanceRecords(@Body JsonObject jsonObject);


    @POST("customer/employee/mark/attendance/breakShiftAttendanceMarkIN")
    Call<AttendanceMarkInReponse> markInWithBreakShift(@Body JsonArray jsonArray);

    @POST("customer/employee/mark/attendance/breakShiftAttendanceMarkOUT")
    Call<AttendanceMarkOutReponse> markOutWithBreakShift(@Body JsonArray jsonArray);


  /*  @POST("customer/employee/mark/attendance/breakShiftAttendanceMarkOUT")
    Call<AttendanceMarkOutReponse> markOutWithBreakShift(@Body JsonArray jsonArray);*/

    @POST("customer/employee/mark/attendance/Mark")
    Call<SynchedMainResponse> getSynchedData(@Body JsonArray jsonArray);


    @POST("customer/employee/mark/attendance/Mark")
    Call<MarkAttendanceResponseNew> getMarkAttendanceNew(@Body JsonArray jsonArray);

}
