package com.lng.lngattendancesystem.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.lng.lngattendancesystem.Activities.FaceRegisterActivities.FaceRegister;
import com.lng.lngattendancesystem.R;


public class EmployeeConfirmation extends DialogFragment {
    String employeeName, mobileNumber;
    private View root_view;
    private Context context;
    private Button cancel, proceed;
    private TextView name, mobile;
    private UserSession userSession;

    public EmployeeConfirmation(String employeeName, String mobileNumber) {
        this.employeeName = employeeName;
        this.mobileNumber = mobileNumber;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.employee_confirm_layout, container, false);
        context = getContext();

        cancel = root_view.findViewById(R.id.cancel);
        proceed = root_view.findViewById(R.id.proceed);
        userSession = new UserSession(getContext());
        name = root_view.findViewById(R.id.name);
        mobile = root_view.findViewById(R.id.mobile_no);
        name.setText(employeeName);
        mobile.setText(mobileNumber);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(), FaceRegister.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();

            }
        });


        return root_view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}