package com.lng.lngattendancesystem.Activities.CustomerActivities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.BroadCastReciever.ConnectivityReceiver;
import com.lng.lngattendancesystem.Models.BlockDetailsResponseModelsDetails.BlockDetailsResponseModel;
import com.lng.lngattendancesystem.Models.BranchDetailsResponseModelDetails.BranchDetailsResponseModel;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.Utilities.UserSession;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchBlockActivity extends AppCompatActivity {
    private Button submit;
    private LinearLayout selectBranch, selectBlock;
    private EditText branch, block;
    private UserSession userSession;
    private ProgressDialog progressDialog;
    private long TIME_OUT = 1000;
    private Context context;
    private BroadcastReceiver broadcastReceiver = new ConnectivityReceiver();
    private String customerCode;
    private BranchDetailsResponseModel branchDetails;
    private BlockDetailsResponseModel blockDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_block);
        context = BranchBlockActivity.this;
        userSession = new UserSession(BranchBlockActivity.this);
        progressDialog = new ProgressDialog(BranchBlockActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);
        Log.i("customerCode ", "onCreate: " + userSession.getCustomerCode());
        initUiComponent();
        showPrgDailog();
        getBranchDetails();
    }

    private void initUiComponent() {
        submit = findViewById(R.id.btn_verify);
        selectBranch = findViewById(R.id.select_branch);
        selectBlock = findViewById(R.id.select_block);
        branch = findViewById(R.id.branch);
        block = findViewById(R.id.block);

        branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (branchDetails != null && branchDetails.getBranchList().size() > 0) {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < branchDetails.getBranchList().size(); i++) {
                        list.add(branchDetails.getBranchList().get(i).getBrName());
                    }
                    showPopUpDailog(branch, list, "Branch");
                }
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blockDetail != null && blockDetail.getBlockList().size() > 0) {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < blockDetail.getBlockList().size(); i++) {
                        list.add(blockDetail.getBlockList().get(i).getBlkLogicalName());
                    }
                    showPopUpDailog(block, list, "Blocks");
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrgDailog();
                if (searchBranchCode() != null) {
                    String branchCode = searchBranchCode();
                    int blockId = searchBlockID();
                    String blockName = searchBlockName();

                    /**
                     *  get OTP
                     *   {
                     * "custCode":"LNG001",
                     * "brCode":"LNG00101"
                     * }
                     */
                    getCallCustomerRegApi(branchCode, blockId, blockName);
                } else {
                    Toast.makeText(context, "brachcode not found!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private void showPopUpDailog(final View v, ArrayList<String> listOfNames, String title) {
        if (listOfNames != null && listOfNames.size() > 0) {
            final String[] array = new String[listOfNames.size()];
            for (int i = 0; i < listOfNames.size(); i++) {
                array[i] = listOfNames.get(i);
            }
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle(title);

            builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((EditText) v).setText(array[i]);
                    if (v.getId() == R.id.branch) {
                        showPrgDailog();
                        getBlockDetails();
                    }
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else {
            showMsg(title + " details not found..");
        }
    }


    private String searchBranchCode() {
        if (branchDetails != null && branchDetails.getBranchList().size() > 0) {
            String selectedBrach = branch.getText().toString();
            for (int i = 0; i < branchDetails.getBranchList().size(); i++) {
                if (selectedBrach.equalsIgnoreCase(branchDetails.getBranchList().get(i).getBrName())) {
                    return branchDetails.getBranchList().get(i).getBrCode();
                }
            }
        }
        return null;
    }


    private int searchBranchID() {
        if (branchDetails != null && branchDetails.getBranchList().size() > 0) {
            String selectedBrach = branch.getText().toString();
            for (int i = 0; i < branchDetails.getBranchList().size(); i++) {
                if (selectedBrach.equalsIgnoreCase(branchDetails.getBranchList().get(i).getBrName())) {
                    return branchDetails.getBranchList().get(i).getBrId();
                }
            }
        }
        return -1;
    }


    private int searchBlockID() {
        if (blockDetail != null && blockDetail.getBlockList().size() > 0) {
            String selectedBrach = block.getText().toString();
            for (int i = 0; i < blockDetail.getBlockList().size(); i++) {
                if (selectedBrach.equalsIgnoreCase(blockDetail.getBlockList().get(i).getBlkLogicalName())) {
                    return blockDetail.getBlockList().get(i).getBlkId();
                }
            }
        }
        return -1;
    }

    private String searchBlockName() {
        if (blockDetail != null && blockDetail.getBlockList().size() > 0) {
            String selectedBrach = block.getText().toString();
            for (int i = 0; i < blockDetail.getBlockList().size(); i++) {
                if (selectedBrach.equalsIgnoreCase(blockDetail.getBlockList().get(i).getBlkLogicalName())) {
                    return blockDetail.getBlockList().get(i).getBlkLogicalName();
                }
            }
        }
        return null;
    }


    /**
     * "{
     * ""custCode"":""LDT009""
     * }"
     */
    private void getBranchDetails() {
        try {
            JSONObject customerDetail = new JSONObject();
            customerDetail.put("custCode", userSession.getCustomerCode());
            JsonObject inputData = (JsonObject) new JsonParser().parse(customerDetail.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<BranchDetailsResponseModel> branchDetailsResponseModelCall = apiClient.getBranchs(inputData);
            branchDetailsResponseModelCall.enqueue(new Callback<BranchDetailsResponseModel>() {
                @Override
                public void onResponse(Call<BranchDetailsResponseModel> call, Response<BranchDetailsResponseModel> response) {
                    removePrgDailog();
                    if (response.isSuccessful()) {
                        branchDetails = response.body();
                        if (branchDetails.getStatus().getError()) {
                            removePrgDailog();
                            //showMsg(branchDetails.getStatus().getMessage());
                            toastIconError(branchDetails.getStatus().getMessage());
                        }
                    } else {
                        removePrgDailog();
                        // showMsg(getResources().getString(R.string.exeptionMsg));
                        toastIconError(getResources().getString(R.string.exeptionMsg));
                    }
                }

                @Override
                public void onFailure(Call<BranchDetailsResponseModel> call, Throwable t) {
                    removePrgDailog();
                    //showMsg(t.getMessage());
                    toastIconError(getResources().getString(R.string.No_Response_from_server_500));
                }
            });

        } catch (Exception e) {
            removePrgDailog();
            showMsg(e.getMessage());
            e.printStackTrace();
        }

    }

    /***
     {
     "brId":1
     }
     */

    private void getBlockDetails() {
        try {
            block.setText(null);
            JSONObject customerDetail = new JSONObject();
            int branchId = searchBranchID();
            if (branchId == -1) {
                showMsg("branch details not found...!");
            }
            customerDetail.put("brId", branchId);
            JsonObject inputData = (JsonObject) new JsonParser().parse(customerDetail.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<BlockDetailsResponseModel> blockDetailsResponseModelCall = apiClient.getBlocks(inputData);
            blockDetailsResponseModelCall.enqueue(new Callback<BlockDetailsResponseModel>() {
                @Override
                public void onResponse(Call<BlockDetailsResponseModel> call, Response<BlockDetailsResponseModel> response) {
                    if (response.isSuccessful()) {
                        removePrgDailog();
                        blockDetail = response.body();
                        if (blockDetail.getStatus().getError()) {
                            showMsg(blockDetail.getStatus().getMessage());
                            blockDetail = null;
                        }
                    } else {
                        removePrgDailog();
                        showMsg("Something went wrong..!");
                    }
                }

                @Override
                public void onFailure(Call<BlockDetailsResponseModel> call, Throwable t) {
                    removePrgDailog();
                    showMsg(t.getMessage());
                }
            });

        } catch (Exception e) {
            removePrgDailog();
            showMsg(e.getMessage());
        }
    }


    public void getCallCustomerRegApi(final String branchCode, final int blockId,
                                      final String BlockName) {
    /*    CustomerRequest customerRequest = new CustomerRequest(userSession.getCustomerCode(), branchCode, userSession.getKioskNumber(), "");
        ApiInterface apiClient = ApiClient.getApiClinet().create(ApiInterface.class);
        Call<MainCustRegResponce> mainCustRegResponceCall = apiClient.getCustRegister(customerRequest);
        mainCustRegResponceCall.enqueue(new Callback<MainCustRegResponce>() {
            @Override
            public void onResponse(Call<MainCustRegResponce> call, Response<MainCustRegResponce> response) {
                if (response.isSuccessful()) {
                    try {
                        MainCustRegResponce mainCustRegResponce = response.body();
                        Status status = mainCustRegResponce.getStatus();
                        if (!status.getError()) {
                            removePrgDailog();
                            userSession.setOTP(mainCustRegResponce.getOtpDto().getOtp());
                            userSession.setBranchcode(branchCode);
                            userSession.setBlockId(blockId);
                            userSession.setBlockName(BlockName);
                            Intent intent = new Intent(BranchBlockActivity.this, CustomerOtpActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            removePrgDailog();
                            showMsg(status.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        removePrgDailog();
                        showMsg(getString(R.string.No_Response_from_server_500));
                    }

                } else {
                    removePrgDailog();
                    showMsg(getString(R.string.No_Response_from_server_500));
                }
            }

            @Override
            public void onFailure(Call<MainCustRegResponce> call, Throwable t) {
                removePrgDailog();
                t.printStackTrace();
                showMsg(getString(R.string.No_Response_from_server_500));
            }
        });

*/
    }


    private void showMsg(String msg) {
        try {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    private void toastIconError(String msg) {
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
        toast.setView(custom_view);
        toast.show();
    }


    private void removePrgDailog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showPrgDailog() {
        if (progressDialog != null)
            progressDialog.show();
    }
}
