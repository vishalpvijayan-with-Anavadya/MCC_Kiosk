package com.lng.lngattendancesystem.UtilActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.util.List;

import static com.lng.lngattendancesystem.SqliteDatabse.DatabaseHandler.DATABASE_NAME;

public class RestoreActivity extends AppCompatActivity {
    private Button btnNewInstall;
    private LinearLayout lytRestore;
    private UserSession userSession;


    /**
     * Hold file list.
     */

    private String[] mFileList;
    private String[] dbFileList;
    private String[] mmFileList;
    private String mChosenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        initUi();

    }


    private void initUi() {

        btnNewInstall = findViewById(R.id.btn_new_install);
        lytRestore = findViewById(R.id.lyt_restore);
        //writeSession =new ClsUserSession(RestoreActivity.this);

        userSession = new UserSession(RestoreActivity.this);


        btnNewInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // writeSession.setIsregistred(true);


                Intent intent = new Intent(RestoreActivity.this, CustomerDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();


            }
        });

        lytRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RestoreActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(RestoreActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    restore();
                } else {
                    ActivityCompat.requestPermissions(RestoreActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, ConstantValues.PERMISSIONS_REQUEST_CODE_EXTRANAL_STOARGE);
                }

            }
        });

    }

    private void restore() {
        LoadDBFile();
        LoadMMFile();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your backup file");

        builder.setItems(mmFileList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mmFileList[which].replace(".dat", "");
                boolean found = false;

                for (int i = 0; i < dbFileList.length; i++) {
                    if (dbFileList[i].replace(".bak", "").equalsIgnoreCase(mChosenFile)) {
                        found = true;
                        break;
                    }
                }


                if (found) {
                    try {
                        String filename = mmFileList[which];
                        boolean status = Util.restore(RestoreActivity.this, filename);

                        if (status) {
                            mChosenFile += ".bak";
                            File currentDB = getApplicationContext().getDatabasePath(DATABASE_NAME);
                            File sd = Environment.getExternalStorageDirectory();

                            if (ConstantValues.NO_BACKUP_FILE_MSG.equalsIgnoreCase(mChosenFile))
                                return;
                            mChosenFile = ConstantValues.DB_FILE_BACKUP_FOLDER + "//" + mChosenFile;
                            File backupDB = new File(sd, mChosenFile);

                            @SuppressWarnings("resource")
                            FileChannel src = new FileInputStream(backupDB).getChannel();
                            @SuppressWarnings("resource")
                            FileChannel dst = new FileOutputStream(currentDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            boolean result = checkColumIsExist();
                            if (result) {
                                showToast(ConstantValues.RESTORE_SUCC);
                                //writeSession.setIsregistred(true);
                                userSession.setIsregistred(true);

                       /*         Intent intent = new Intent(RestoreActivity.this, ActRoot.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
*/

                                Intent intent = new Intent(RestoreActivity.this, CustomerDashBoard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                                //setUI();
                            } else {
                                RestoreActivity.this.deleteDatabase(DATABASE_NAME);
                                showToast(ConstantValues.INVALID_BACKUP);
                            }

                        } else {
                            showToast(ConstantValues.NO_BACKUP_FILE_MSG);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("TAG", "Exception in onCreateDialog - RESTORE_DATABASE " + e.toString());
                        showToast(ConstantValues.RESTORE_ABOTED);

                    }

                } else {
                    showToast(ConstantValues.NO_BACKUP_FILE_MSG);

                }
            }
        });
        builder.show();
    }


    private void LoadDBFile() {
        try {
            File filePath = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + ConstantValues.DB_FILE_BACKUP_FOLDER);

            if (filePath.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.contains(ConstantValues.DB_TYPE);

                    }
                };

                dbFileList = filePath.list(filter);
            } else {
                dbFileList = new String[]{ConstantValues.NO_BACKUP_FILE_MSG};
            }
        } catch (Exception e) {

        }
    }


    private void LoadMMFile() {
        try {
            File filePath = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + ConstantValues.DAT_FILE_BACKUP_FOLDER);

            if (filePath.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.contains(ConstantValues.DAT_TYPE);

                    }
                };
                mmFileList = filePath.list(filter);
            } else {
                mmFileList = new String[]{ConstantValues.NO_BACKUP_FILE_MSG};
            }

        } catch (Exception e) {

        }

    }

    public boolean checkColumIsExist() {

        if (SplashActivity.databaseHandler != null) {
            boolean isFound = false;
            boolean result = SplashActivity.databaseHandler.getTablesFromSqlite(SplashActivity.databaseHandler.TABLE_CUSTOMER);
            if (result) {
                List<String> columnsModelList = SplashActivity.databaseHandler.getTaleColumns();
                for (int i = 0; i < columnsModelList.size(); i++) {

                    if (columnsModelList.get(i).equalsIgnoreCase(SplashActivity.databaseHandler.KEY_CUSTOMER_ID) ||

                            columnsModelList.get(i).equalsIgnoreCase(SplashActivity.databaseHandler.KEY_BRANCH_ID)) {

                        Log.d("Tag", "TESTCOLOM " + columnsModelList.get(i));
                        isFound = true;
                        return isFound;
                    }
                }

                if (isFound) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        }
        return false;
    }

    public void showToast(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();

    }


}