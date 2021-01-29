package com.lng.lngattendancesystem.Utilities.CrashReport;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class ReportCrashResultActivity extends AppCompatActivity {
    public String reportDetails;
    Button send;
    ProgressDialog progressDialog;
    UserSession userSession;
    String employeeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_crash_result);
        userSession = new UserSession(ReportCrashResultActivity.this);
        employeeName = userSession.getEmpName();
        reportDetails = getIntent().getStringExtra("data");
        progressDialog = Util.getProgressDialog(ReportCrashResultActivity.this, "Please wait..");
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportCrashResultActivity.this.finishAffinity();
                /* try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Send error report..", Toast.LENGTH_SHORT).show();
                        }
                    });

                    String filePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath()
                            + "/facetek/report/"
                            + "errorTrace.txt";
                    sendErrorMail(ReportCrashResultActivity.this, filePath);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });


        new SendEmail().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReportCrashResultActivity.this.finishAffinity();
    }

    // manual Mail to send

/*
    private void sendErrorMail(Context _context, String filePath) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        String subject = "Error Description";
        String body = "Sorry for your inconvenience .\nWe assure you that we will solve this problem as soon as possible."
                + "\n\nYou may give details about your current activity"
                + "\n\nThanks for using app.";
        sendIntent.setType("plain/text");
        sendIntent.setData(Uri.parse("mailto:"));
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                ConstantValues.SUPPORT_MAIL});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" + filePath));
        _context.startActivity(Intent.createChooser(sendIntent, "Email via..."));

    }
*/


    class SendEmail extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            if (employeeName == null || employeeName.isEmpty())
                employeeName = "";

            String subject = " Facetek KIOSK Bug report";
            boolean status = sendEmail("smtp.gmail.com", //SMTP Server Address
                    "587", //SMTP Port Number

                    "true", //Enable Authorization
                    "true", //Enable TLS
                    "facetekbugs@gmail.com", //Your SMTP Username
                    "faceteck@123", //Your SMTP Password
                    "Facetek", //Sender Address
                    "lngtechpl@gmail.com", //Recipient Address
                    subject, //Message Subject
                    reportDetails); //Message Body

            return status;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            reportDetails = "";
            if (status) {
                Log.i("TAG", "TESTMIAL sent:");
                Toast.makeText(ReportCrashResultActivity.this, "Unfortunately, App has stopped. Please try again!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                ReportCrashResultActivity.this.finishAffinity();
            } else {
                Log.i("TAG", "TESTMIAL Fail:");
                Toast.makeText(ReportCrashResultActivity.this, "Unfortunately, App has stopped. Please try again!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                ReportCrashResultActivity.this.finishAffinity();
            }


        }

        boolean sendEmail(
                String smtpAddress,
                String smtpPort,
                String enableTLS,
                String enableAuth,
                final String username,
                final String password,
                String fromAddress,
                String toAddress,
                String mySubject,
                String myMessage
        ) {

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", enableTLS);
            props.put("mail.smtp.auth", enableAuth);
            props.put("mail.smtp.host", smtpAddress);
            props.put("mail.smtp.port", smtpPort);

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            try {
                Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress(fromAddress));

                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(toAddress));

                message.setSubject(mySubject);

                //message.setText("hello");


                BodyPart messageBodyPart = new MimeBodyPart();

                // Now set the actual message
                messageBodyPart.setText(myMessage);

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is attachment

                //multipart.addBodyPart(messageBodyPart);

                // Send the complete message parts
                message.setContent(multipart);

                Transport.send(message);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }
}