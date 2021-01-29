package com.lng.lngattendancesystem.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.Activities.FaceRegisterActivities.MobileVerification;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.MarkAttendanceActivity;
import com.lng.lngattendancesystem.MainActivity;
import com.lng.lngattendancesystem.Models.GetDateTimeFromSerevr.GetServerDateTimeResponce;
import com.lng.lngattendancesystem.Models.RestoreEmployeeDetales.RestoreEmpMainResponce;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;
import com.lng.lngattendancesystem.Utilities.DateTimeUtil;
import com.lng.lngattendancesystem.Utilities.UserSession;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.MarkAttendanceActivity.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Customer_Home_Fragment extends Fragment {
    View view;
    UserSession userSession;
    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 00;
    CustomerDashBoard ref;
    private MaterialRippleLayout ripplefacereg, ripplemark;
    private ImageView customerLogo;
    private long TIME_OUT = 1000;
    private boolean rippleInClicked = false;
    private TextView customerName, branchName;
    private ProgressDialog progressDialog;

    public Customer_Home_Fragment(CustomerDashBoard customerDashBoard) {
        ref = customerDashBoard;
        // Required empty public constructor
    }
    public Customer_Home_Fragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_customer__home_, container, false);
        userSession = new UserSession(getContext());
        initUiComponent();
        return view;
    }

    private void initUiComponent() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking for Employee data update");
        progressDialog.setCancelable(false);
        ripplefacereg = view.findViewById(R.id.ripplefacereg);
        ripplemark = view.findViewById(R.id.ripplemark);
        customerLogo = view.findViewById(R.id.image);
        customerName = view.findViewById(R.id.customer_name);
        branchName = view.findViewById(R.id.branch_name);
        changeCustomerDetailes();
        // getTimefromSereverApi();

        ripplefacereg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (!rippleInClicked) {
                        rippleInClicked = true;
                        Intent intent = new Intent(getContext(), MobileVerification.class);
                        startActivity(intent);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rippleInClicked = false;
                            }
                        }, 2000);
                    }

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }

            }
        });


        ripplemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rippleInClicked) {
                    rippleInClicked = true;
                    //Intent intent = new Intent(getContext(), FaceDetectActivity.class);
                    Intent intent = new Intent(getContext(), MarkAttendanceActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rippleInClicked = false;
                        }
                    }, 2000);
                }


            }
        });


    }


    private void getTimefromSereverApi() {
        progressDialog.show();
        try {
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<GetServerDateTimeResponce> shiftdeteles = apiClient.getDateTimeFromSerever();
            shiftdeteles.enqueue(new Callback<GetServerDateTimeResponce>() {
                @Override
                public void onResponse(Call<GetServerDateTimeResponce> call, retrofit2.Response<GetServerDateTimeResponce> response) {
                    if (response.isSuccessful()) {
                        try {
                            GetServerDateTimeResponce getServerDateTimeResponce = response.body();
                            userSession.setServerDateTime(getServerDateTimeResponce.getCurrentDate());
                            if (userSession.getApiTime() != null) {
                                String serverTime = DateTimeUtil.getonlydate(userSession.getServerDateAndTime());
                                String sessionTime = DateTimeUtil.getonlydate(userSession.getApiTime());
                                if (!serverTime.equalsIgnoreCase(sessionTime)) {
                                    restoreEmpDetales();
                                } else {
                                    restoreEmpDetales();
                                    _removeProgressDialog();
                                }
                            } else {
                                Log.d("TAG", "NAGU im the defult");
                                restoreEmpDetales();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            _removeProgressDialog();
                            toastIconError(getString(R.string.No_Response_from_server_500));
                        }
                    } else {
                        _removeProgressDialog();
                        toastIconError(getString(R.string.No_Response_from_server_500));
                    }
                }

                @Override
                public void onFailure(Call<GetServerDateTimeResponce> call, Throwable t) {
                    _removeProgressDialog();
                    t.printStackTrace();
                    Log.i(TAG, "getTimefromSereverApi onFailure: No responce");
                    toastIconError(getString(R.string.Internet));
                }
            });
        } catch (Exception e) {
            _removeProgressDialog();
            toastIconError(getString(R.string.No_Response_from_server_500));
            e.printStackTrace();
        }


    }


    private void restoreEmpDetales() {
        try {
            JSONObject customerDetail = new JSONObject();
            customerDetail.put("custId", userSession.getCustId());
            customerDetail.put("brId", userSession.getBrID());
            customerDetail.put("blkId", userSession.getBlockId());
            JsonObject inputData = (JsonObject) new JsonParser().parse(customerDetail.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<RestoreEmpMainResponce> getRestoreEmp = apiClient.getRestoreEmpDetails(inputData);
            getRestoreEmp.enqueue(new Callback<RestoreEmpMainResponce>() {
                @Override
                public void onResponse(Call<RestoreEmpMainResponce> call, Response<RestoreEmpMainResponce> response) {
                    if (response.isSuccessful()) {
                        try {
                            RestoreEmpMainResponce restoreEmpMainResponce = response.body();
                            String empId = null;
                            if (restoreEmpMainResponce.getEmployeeDetails().size() > 0 ||
                                    restoreEmpMainResponce.getEmployeeDetails() != null) {
                                for (int i = 0; i < restoreEmpMainResponce.getEmployeeDetails().size(); i++) {
                                    if (!SplashActivity.databaseHandler.isEmployeeExist(
                                            String.valueOf(restoreEmpMainResponce.getEmployeeDetails().get(i).getEmpId()))) {
                                        String shiftType = restoreEmpMainResponce.getEmployeeDetails().get(i).getShiftType();
                                        empId = String.valueOf(restoreEmpMainResponce.getEmployeeDetails().get(i).getEmpId());
                                        String persistedFaceId = restoreEmpMainResponce.getEmployeeDetails().get(i).getEmpPresistedFaceId();
                                        String empName = restoreEmpMainResponce.getEmployeeDetails().get(i).getEmpName();
                                        boolean updatedStatus = SplashActivity.databaseHandler.insertEmprecords(empId, empName,
                                                persistedFaceId, shiftType,"");
                                        if (updatedStatus) {
                                            //toastIconError("Registerd successfully!");
                                        } else {
                                            //toastIconError("Employee Data synching failed !");
                                        }
                                    } else {
                                        empId = String.valueOf(restoreEmpMainResponce.getEmployeeDetails().get(i).getEmpId());
                                        String FromSqliteempPersistedFaceId = SplashActivity.databaseHandler.getPersistedFaceIDByEmpId(empId);
                                        String FromApipersistedFaceId = restoreEmpMainResponce.getEmployeeDetails().get(i).getEmpPresistedFaceId();
                                        if (FromSqliteempPersistedFaceId == null) {
                                            // insert dire
                                            int result = SplashActivity.databaseHandler.updatePersitedFaecByEmpID(empId, FromApipersistedFaceId);
                                        } else if (!FromApipersistedFaceId.equalsIgnoreCase(FromSqliteempPersistedFaceId)) {
                                            int result = SplashActivity.databaseHandler.updatePersitedFaecByEmpID(empId, FromApipersistedFaceId);
                                        }
                                    }
                                }
                                ref.changeRecordsStatus(true);
                                _removeProgressDialog();
                                userSession.setApiTime(userSession.getServerDateAndTime());

                            } else {
                                _removeProgressDialog();
                                toastIconError(restoreEmpMainResponce.getStatus().getMessage());
                            }
                        } catch (Exception e) {
                            _removeProgressDialog();
                            e.printStackTrace();
                            toastIconError(getString(R.string.exeptionMsg));
                        }
                    } else {
                        _removeProgressDialog();
                        toastIconError(getString(R.string.No_Response_from_server_500));
                    }
                }

                @Override
                public void onFailure(Call<RestoreEmpMainResponce> call, Throwable t) {
                    toastIconError(getString(R.string.No_Response_from_server_500));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            toastIconError(getString(R.string.No_Response_from_server_500));
        }


    }


    public void changeCustomerDetailes() {
        if (SplashActivity.databaseHandler == null)
            return;
        List<getCustomerAllDitailes> cutsomerDetalesList = SplashActivity.databaseHandler.getCustomerAllrecords();
        byte[] custImg = null;
        if (cutsomerDetalesList != null) {
            custImg = cutsomerDetalesList.get(0).getCustomerLogo();
            if (custImg != null) {
                Bitmap customerImag = BitmapFactory.decodeByteArray(custImg, 0, custImg.length);
                customerLogo.setImageBitmap(customerImag);
                if (cutsomerDetalesList.get(0).getCustomerName().equalsIgnoreCase(cutsomerDetalesList.get(0).getBranchName())) {
                    customerName.setText(cutsomerDetalesList.get(0).getCustomerName());
                    branchName.setText(null);
                } else {
                    customerName.setText(cutsomerDetalesList.get(0).getCustomerName());
                    branchName.setText(cutsomerDetalesList.get(0).getBranchName());
                }

            }
        } else {
            toastIconError("Sorry Customer Detailes not found!");
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            userSession.setRegiCompleeted(false);
            startActivity(intent);
            getActivity().finish();
            return;

        }

    }

    private void toastIconError(String msg) {
        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_LONG);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
        toast.setView(custom_view);
        toast.show();

    }

    public void onClickDesable(final View v) {
        v.setEnabled(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, TIME_OUT);
    }

    public void _removeProgressDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

    }


}
