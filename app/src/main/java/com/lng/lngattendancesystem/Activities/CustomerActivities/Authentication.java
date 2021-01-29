package com.lng.lngattendancesystem.Activities.CustomerActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.UserSession;

public class Authentication extends AppCompatActivity {

    private EditText et_passcode;
    private AppCompatButton btn_login;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initUI();
        initToolbar();
    }

    private void initUI() {
        et_passcode = findViewById(R.id.et_passcode);
        btn_login = findViewById(R.id.btn_login);
        userSession = new UserSession(Authentication.this);
        generatePassword(userSession.getBrID(), userSession.getCustId());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!et_passcode.getText().toString().isEmpty()) {
                    if (et_passcode.getText().toString().equalsIgnoreCase(userSession.getPergePassword())) {
                        et_passcode.setText(null);
                        Intent intent2 = new Intent(Authentication.this, PergeActivity.class);
                        startActivity(intent2);
                    } else {
                        Toast.makeText(Authentication.this, "Invalid Passcode!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Authentication.this, "Enter Passcode!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void generatePassword(int brId, int custId) {
        userSession.setPergePassword(brId + "" + custId);
    }

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Authentication");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Authentication.this, CustomerDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}