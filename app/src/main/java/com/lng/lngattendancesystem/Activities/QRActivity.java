package com.lng.lngattendancesystem.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.balysv.materialripple.BuildConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.lng.lngattendancesystem.Models.GetDateTimeFromSerevr.GetServerDateTimeResponce;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class QRActivity extends AppCompatActivity {
    private ImageView qrCode;
    private UserSession userSession;
    private CircularImageView customerLogo;
    private ProgressDialog progressDialog;
    private TextView branchName, customername;
    private String latestVersion, currentVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        init();
    }

    private void init() {
        qrCode = findViewById(R.id.qr_code_image);
        customerLogo = findViewById(R.id.logo);
        userSession = new UserSession(QRActivity.this);
        customername = findViewById(R.id.customer_name);
        branchName = findViewById(R.id.branch_name);
        QRPeriodHandler();
        changeCustomerDetailes();
        new GetLatestVersion().execute();
    }


    public void changeCustomerDetailes() {
        if (SplashActivity.databaseHandler == null)
            return;
        List<getCustomerAllDitailes> cutsomerDetalesList = SplashActivity.databaseHandler.getCustomerAllrecords();
        byte[] custImg = null;
        if (cutsomerDetalesList != null) {

            if (cutsomerDetalesList.get(0).getCustomerName().equalsIgnoreCase(cutsomerDetalesList.get(0).getBranchName())) {
                customername.setText(cutsomerDetalesList.get(0).getCustomerName());
                branchName.setText(null);
            } else {
                customername.setText(cutsomerDetalesList.get(0).getCustomerName());
                branchName.setText(cutsomerDetalesList.get(0).getBranchName());
            }
            custImg = cutsomerDetalesList.get(0).getCustomerLogo();
            if (custImg != null) {
                Bitmap customerImag = BitmapFactory.decodeByteArray(custImg, 0, custImg.length);
                customerLogo.setImageBitmap(customerImag);
            }
        } else {
            toastIconError("Sorry Customer Detailes not found!");
            return;

        }
        //customerLogo
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void QRPeriodHandler() {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    getTimefromSereverApi();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100, ConstantValues.QR_CODE_GENERATION_INTERVAL);

    }

    private void toastIconError(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(QRActivity.this);
                toast.setDuration(Toast.LENGTH_LONG);
                //inflate view
                View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
                ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
                //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
                ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
                toast.setView(custom_view);
                toast.show();

            }
        });


    }


    private void getTimefromSereverApi() {
        try {
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<GetServerDateTimeResponce> shiftdeteles = apiClient.getDateTimeFromSerever();
            shiftdeteles.enqueue(new Callback<GetServerDateTimeResponce>() {
                @Override
                public void onResponse(Call<GetServerDateTimeResponce> call, retrofit2.Response<GetServerDateTimeResponce> response) {
                    if (response.isSuccessful()) {

                        try {

                            GetServerDateTimeResponce getServerDateTimeResponce = response.body();
                            Log.i("TAG", "called" + getServerDateTimeResponce.getCurrentDate());
                            userSession.setServerDateTime(getServerDateTimeResponce.getCurrentDate());
                            String hyperText = userSession.getBranchCode() + "|" + userSession.getServerDateAndTime() + "|" + new Random().nextInt(99);
                            byte[] data1 = hyperText.getBytes(StandardCharsets.UTF_8);
                            String base64 = Base64.encodeToString(data1, Base64.DEFAULT);
                            generateQRCode(base64);
                            _removeProgressDialog();
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
                    toastIconError(getString(R.string.Internet));
                }
            });
        } catch (Exception e) {
            _removeProgressDialog();
            toastIconError(getString(R.string.No_Response_from_server_500));
            e.printStackTrace();
        }


    }


    private void toastIconSucc(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(QRActivity.this);
                toast.setDuration(Toast.LENGTH_LONG);
                //inflate view
                View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
                ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
                //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
                ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.usetv));
                toast.setView(custom_view);
                toast.show();

            }
        });


    }

    public void _removeProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void generateQRCode(final String texto) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                QRCodeWriter writer = new QRCodeWriter();
                ImageView qrCode = findViewById(R.id.qr_code_image);
                try {
                    BitMatrix bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 512, 512);
                    int width = 512;
                    int height = 512;
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            if (bitMatrix.get(x, y))
                                bmp.setPixel(x, y, Color.BLACK);
                            else
                                bmp.setPixel(x, y, Color.WHITE);
                        }
                    }
                    qrCode.setImageBitmap(bmp);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new GetLatestVersion().execute();
    }

    private void showUpdateDialogBox(String msg) {
        try {
            final Dialog dialog = new Dialog(QRActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_upadte, null);
            TextView leavemsg = dialogView.findViewById(R.id.latestvesrion);
            leavemsg.setText("Latest Version " + msg);
            dialog.setContentView(dialogView);
            dialog.setCancelable(false);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.bt_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lng.lngattendancev1")));

                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class GetLatestVersion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return latestVersion;
        }


        @Override
        protected void onPostExecute(String s) {
            currentVersion = BuildConfig.VERSION_NAME;
            Log.d("TAG", "latestVersion" + latestVersion);
            if (latestVersion != null) {
                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    showUpdateDialogBox(latestVersion);
                }
            }
        }
    }
}