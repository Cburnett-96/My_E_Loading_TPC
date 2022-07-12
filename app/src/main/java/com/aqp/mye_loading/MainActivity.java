package com.aqp.mye_loading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aqp.mye_loading.other.MessageReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1 ;
    private static final int CONTACT_PICKER_RESULT = 111;
    Button btnExit, btnNextToProcess, btnRefund, btnContacts, btnNextToProcessContact, btnClose, btnAddPromo,
            btnClosePromo, btnCloseTelecom, btnGlobe, btnSmart, btnTM, btnTNT, btnGlobeT, btnSmartT, btnTMT, btnTNTT,
            btnGithub, btnShare;
    EditText eTNumber, eTContactNumber;
    TextView tvContactName, tvBalance, tvVersion;
    String Number, ContactNumber;
    LinearLayout layoutContact, layoutMain, layoutAddPromo, layoutTelecom;

    private MessageReceiver messageReceiver;
    public static String SMSRECEVID = "custom.action.SMSRECEVEDINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white_smoke));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        btnExit = findViewById(R.id.btn_Exit);
        btnNextToProcess = findViewById(R.id.btn_nextToProcess);
        btnContacts = findViewById(R.id.btn_savedContact);
        btnRefund = findViewById(R.id.btn_refund);
        eTNumber = findViewById(R.id.editTextNumber);
        btnNextToProcessContact = findViewById(R.id.btn_nextToProcessContact);
        btnClose = findViewById(R.id.btn_close);
        btnAddPromo = findViewById(R.id.btn_addPromo);
        btnClosePromo = findViewById(R.id.btn_closePromo);
        btnGlobe = findViewById(R.id.btn_Globe);
        btnSmart = findViewById(R.id.btn_Smart);
        btnTM = findViewById(R.id.btn_TM);
        btnTNT = findViewById(R.id.btn_TNT);
        btnGlobeT = findViewById(R.id.btn_GlobeT);
        btnSmartT = findViewById(R.id.btn_SmartT);
        btnTMT = findViewById(R.id.btn_TMT);
        btnTNTT = findViewById(R.id.btn_TNTT);
        btnCloseTelecom = findViewById(R.id.btn_closeTelecom);
        eTContactNumber = findViewById(R.id.editTextContactNumber);
        tvContactName = findViewById(R.id.textView_ContactName);
        layoutContact = findViewById(R.id.layoutContacts);
        layoutMain = findViewById(R.id.layoutMain);
        layoutAddPromo = findViewById(R.id.layoutAddPromo);
        layoutTelecom = findViewById(R.id.layoutTelecom);
        tvBalance = findViewById(R.id.textViewBalance);
        tvVersion = findViewById(R.id.tv_Version);
        btnGithub = findViewById(R.id.btnGithub);
        btnShare = findViewById(R.id.btnShare);

        if((ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) & ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        } else {
            GetBalanceSMS();
        }

        messageReceiver = new MessageReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                if(intent.getAction().equals(MainActivity.SMSRECEVID)){
                    GetBalanceSMS();
                }
            }
        };

        btnContacts.setOnClickListener(v -> {
            layoutMain.setVisibility(View.GONE);
            layoutContact.setVisibility(View.VISIBLE);
            doLaunchContactPicker(v);
        });
        tvContactName.setOnClickListener(this::doLaunchContactPicker);

        btnNextToProcess.setOnClickListener(v -> validateNumber());
        btnNextToProcessContact.setOnClickListener(v -> validateNumberContact());

        btnAddPromo.setOnClickListener(view -> {
            layoutMain.setVisibility(View.GONE);
            layoutAddPromo.setVisibility(View.VISIBLE);
            AddPromo();
        });
        btnRefund.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RefundActivity.class);
            startActivity(intent);
        });
        //Close and Exit
        btnClosePromo.setOnClickListener(view -> {
            layoutMain.setVisibility(View.VISIBLE);
            layoutAddPromo.setVisibility(View.GONE);
        });
        btnClose.setOnClickListener(v -> {
            layoutMain.setVisibility(View.VISIBLE);
            layoutContact.setVisibility(View.GONE);
        });
        btnCloseTelecom.setOnClickListener(v -> {
            layoutMain.setVisibility(View.VISIBLE);
            layoutTelecom.setVisibility(View.GONE);
        });
        btnExit.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
        String version = "App Version: "+BuildConfig.VERSION_NAME;
        tvVersion.setText(version);

        btnGithub.setOnClickListener(view -> {
            Intent browserGithub = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Cburnett-96/My_E_Loading_TPC"));
            startActivity(browserGithub);
        });
        btnShare.setOnClickListener(view -> {
            shareApplication();
        });
    }

    private void validateNumber(){
        Number = eTNumber.getText().toString().trim();

        if (TextUtils.isEmpty(Number)) {
            Toast.makeText(MainActivity.this, "Input the Numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Number.length() < 11){
            Toast.makeText(MainActivity.this, "Numbers should be 11 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        layoutMain.setVisibility(View.GONE);
        layoutTelecom.setVisibility(View.VISIBLE);

        btnTNTT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SmartTNTActivity.class);
            intent.putExtra("number", Number.replace(" ", ""));
            intent.putExtra("telecom", "TNT");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnTMT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GlobeTMProcessActivity.class);
            intent.putExtra("number", Number.replace(" ", ""));
            intent.putExtra("telecom", "TM");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnSmartT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SmartTNTActivity.class);
            intent.putExtra("number", Number.replace(" ", ""));
            intent.putExtra("telecom", "Smart");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnGlobeT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GlobeTMProcessActivity.class);
            intent.putExtra("number", Number.replace(" ", ""));
            intent.putExtra("telecom", "Globe");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
    }
    private void validateNumberContact(){
        ContactNumber = eTContactNumber.getText().toString().trim().replace("+63","0");

        if (TextUtils.isEmpty(ContactNumber)) {
            Toast.makeText(MainActivity.this, "Input the Numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContactNumber.length() < 11){
            Toast.makeText(MainActivity.this, "Numbers should be 11 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        layoutMain.setVisibility(View.GONE);
        layoutTelecom.setVisibility(View.VISIBLE);
        layoutContact.setVisibility(View.GONE);

        btnTNTT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SmartTNTActivity.class);
            intent.putExtra("number", ContactNumber.replace(" ", ""));
            intent.putExtra("telecom", "TNT");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnTMT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GlobeTMProcessActivity.class);
            intent.putExtra("number", ContactNumber.replace(" ", ""));
            intent.putExtra("telecom", "TM");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnSmartT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SmartTNTActivity.class);
            intent.putExtra("number", ContactNumber.replace(" ", ""));
            intent.putExtra("telecom", "Smart");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnGlobeT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GlobeTMProcessActivity.class);
            intent.putExtra("number", ContactNumber.replace(" ", ""));
            intent.putExtra("telecom", "Globe");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
    }

    private void AddPromo(){
        btnTNT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddPromoActivity.class);
            intent.putExtra("telecom", "TNT");
            startActivity(intent);
            layoutAddPromo.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnTM.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddPromoActivity.class);
            intent.putExtra("telecom", "TM");
            startActivity(intent);
            layoutAddPromo.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnSmart.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddPromoActivity.class);
            intent.putExtra("telecom", "Smart");
            startActivity(intent);
            layoutAddPromo.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
        btnGlobe.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddPromoActivity.class);
            intent.putExtra("telecom", "Globe");
            startActivity(intent);
            layoutAddPromo.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });


    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CONTACT_PICKER_RESULT) && (resultCode == RESULT_OK)) {
            if (data != null) {
                Uri contactData = data.getData();

                try {

                    String id = contactData.getLastPathSegment();
                    String[] columns = {ContactsContract.CommonDataKinds.Phone.DATA, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                    Cursor phoneCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, columns,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = ?", new String[]{id},
                                    null);

                    final ArrayList<String> phonesList = new ArrayList<>();
                    String Name = null;
                    if (phoneCur.moveToFirst()) {
                        do {
                            Name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                            phonesList.add(phone);
                        } while (phoneCur.moveToNext());
                        tvContactName.setText(Name);
                    }

                    phoneCur.close();

                    if (phonesList.size() == 0) {
                        Toast.makeText(
                                this, "This contact does not contain any number",
                                Toast.LENGTH_LONG).show();
                    } else if (phonesList.size() == 1) {
                        eTContactNumber.setText(phonesList.get(0));
                    } else {

                        final String[] phonesArr = new String[phonesList
                                .size()];
                        for (int i = 0; i < phonesList.size(); i++) {
                            phonesArr[i] = phonesList.get(i);
                        }

                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                MainActivity.this);
                        dialog.setTitle("Name : " + Name);
                        ((AlertDialog.Builder) dialog).setItems(phonesArr,
                                (dialog1, which) -> {
                                    String selectedEmail = phonesArr[which];
                                    eTContactNumber.setText(selectedEmail);
                                }).create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    Log.e("FILES", "Failed to get phone data", e);
                }
            }

        }
    }

    private void GetBalanceSMS(){
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, "address='8724'", null,null);

        if (cur.moveToFirst()) {
            Pattern pattern = Pattern.compile("P(.*?)T");
            Matcher matcher = pattern.matcher(cur.getString(12));
            if (matcher.find())
            {
                tvBalance.setText(Objects.requireNonNull(matcher.group(1)).trim());
            }
        }else {
            Toast.makeText(MainActivity.this, "No SMS Found on 8724", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_CONTACTS},MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + "_v"+BuildConfig.VERSION_NAME+".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
            //Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile);
            Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", tempFile);
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}