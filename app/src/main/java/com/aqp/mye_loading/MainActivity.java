package com.aqp.mye_loading;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.aqp.mye_loading.adapter.AdapterMustPromoList;
import com.aqp.mye_loading.adapter.AdapterPromoList;
import com.aqp.mye_loading.model.MustPromoList;
import com.aqp.mye_loading.model.PromoList;
import com.aqp.mye_loading.other.DBHandler;
import com.aqp.mye_loading.other.DBHandlerMustPromo;
import com.aqp.mye_loading.other.DataBaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int CONTACT_PICKER_RESULT = 111;
    Button btnExit, btnNextToProcess, btnRefund, btnContacts, btnNextToProcessContact, btnClose, btnAddPromo,
            btnClosePromo, btnCloseTelecom, btnGlobe, btnSmart, btnTM, btnTNT, btnGlobeT, btnDito, btnSmartT, btnTMT, btnTNTT, btnDitoT,
            btnGithub, btnShare;
    EditText eTNumber, eTContactNumber;
    TextView tvContactName, tvBalance, tvVersion, tvNoPromo;
    String Number, ContactNumber;
    LinearLayout layoutContact, layoutMain, layoutAddPromo, layoutTelecom, layoutMustPromo;

    private ArrayList <MustPromoList> mustPromoLists;
    RecyclerView recyclerViewMustPromoList;
    DBHandlerMustPromo dbHandlerMustPromo;

    loadingDialog loadingDialog;
    Handler handler;

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
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.violet));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white_smoke));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        loadingDialog = new loadingDialog(MainActivity.this, R.style.CustomAlertDialog);
        handler = new Handler();

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
        btnDito = findViewById(R.id.btn_Dito);
        btnTNT = findViewById(R.id.btn_TNT);
        btnGlobeT = findViewById(R.id.btn_GlobeT);
        btnSmartT = findViewById(R.id.btn_SmartT);
        btnTMT = findViewById(R.id.btn_TMT);
        btnTNTT = findViewById(R.id.btn_TNTT);
        btnDitoT = findViewById(R.id.btn_DitoT);
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
        tvNoPromo = findViewById(R.id.textViewNoPromo);
        layoutMustPromo = findViewById(R.id.layoutMustPromo);
        recyclerViewMustPromoList = findViewById(R.id.recycleViewMustPromoList);
        mustPromoLists = new ArrayList<>();
        dbHandlerMustPromo = new DBHandlerMustPromo(MainActivity.this);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) & ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        } else {
            loadingDialog.startLoadingDialog();
            handler.postDelayed(() -> {
                loadingDialog.dismisDialog();
                GetBalanceSMS();
                setAdapter();
            },1000);
        }

        btnContacts.setOnClickListener(v -> {
            layoutMain.setVisibility(View.GONE);
            layoutContact.setVisibility(View.VISIBLE);
            doLaunchContactPicker(v);
        });
        tvContactName.setOnClickListener(this::doLaunchContactPicker);

        btnNextToProcess.setOnClickListener(this::validateNumber);
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
        });
        String version = "App Version: " + BuildConfig.VERSION_NAME;
        tvVersion.setText(version);

        btnGithub.setOnClickListener(view -> {
            Intent browserGithub = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Cburnett-96/My_E_Loading_TPC"));
            startActivity(browserGithub);
        });
        btnShare.setOnClickListener(view -> shareApplication());
    }

    private void CopyDatabase() {
        DataBaseHelper myDbHelper;
        myDbHelper = new DataBaseHelper(this);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        myDbHelper.openDataBase();

    }

    private void validateNumber(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
        Number = eTNumber.getText().toString().trim();

        if (TextUtils.isEmpty(Number)) {
            Toast.makeText(MainActivity.this, "Input the Numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Number.length() < 11) {
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
        btnDitoT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DitoActivity.class);
            intent.putExtra("number", Number.replace(" ", ""));
            intent.putExtra("telecom", "Dito");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
    }

    private void validateNumberContact() {
        ContactNumber = eTContactNumber.getText().toString().trim().replace("+63", "0");

        if (TextUtils.isEmpty(ContactNumber)) {
            Toast.makeText(MainActivity.this, "Input the Numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContactNumber.length() < 11) {
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
        btnDitoT.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DitoActivity.class);
            intent.putExtra("number", ContactNumber.replace(" ", ""));
            intent.putExtra("telecom", "Dito");
            startActivity(intent);
            layoutTelecom.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        });
    }

    private void AddPromo() {
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
        btnDito.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddPromoActivity.class);
            intent.putExtra("telecom", "Dito");
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

    @SuppressLint("Range")
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

                    if (phoneCur != null && phoneCur.moveToFirst()) {
                        do {
                            Name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                            phonesList.add(phone);
                        } while (phoneCur.moveToNext());
                        tvContactName.setText(Name);
                    }

                    assert phoneCur != null;
                    phoneCur.close();

                    if (phonesList.size() == 0) {
                        Toast.makeText(
                                this, "This contact does not contain any number",
                                Toast.LENGTH_LONG).show();
                    } else if (phonesList.size() == 1) {
                        eTContactNumber.setText(phonesList.get(0));
                        validateNumberContact();
                    } else {

                        final String[] phonesArr = new String[phonesList
                                .size()];
                        for (int i = 0; i < phonesList.size(); i++) {
                            phonesArr[i] = phonesList.get(i);
                        }

                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                MainActivity.this);
                        dialog.setTitle("Name : " + Name);
                        dialog.setItems(phonesArr,
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

    private void GetBalanceSMS() {
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, "address='8724'", null, null);

        if (cur.moveToFirst()) {
            Pattern pattern = Pattern.compile("P(.*?)T");
            Matcher matcher = pattern.matcher(cur.getString(12));
            if (matcher.find()) {
                tvBalance.setText(Objects.requireNonNull(matcher.group(1)).trim());
            }
        } else {
            Toast.makeText(MainActivity.this, "No SMS Found on 8724", Toast.LENGTH_SHORT).show();
        }
        if (cur.getCount() == 0)
            return;
        System.out.println(cur.getString(12));
    }

    private void setAdapter(){
        mustPromoLists = dbHandlerMustPromo.readMustPromo();
        AdapterMustPromoList adapter = new AdapterMustPromoList(mustPromoLists, MainActivity.this);
        if (mustPromoLists.size() <= 5)
        return;
        recyclerViewMustPromoList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMustPromoList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMustPromoList.setAdapter(adapter);
        tvNoPromo.setVisibility(View.GONE);
        layoutMustPromo.setVisibility(View.VISIBLE);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            loadingDialog.startLoadingDialog();

            handler.postDelayed(() -> {
                loadingDialog.dismisDialog();
                GetBalanceSMS();
                CopyDatabase();
            },2000);
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
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ", "").toLowerCase() + "_v" + BuildConfig.VERSION_NAME + ".apk");
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

    private static long back_pressed;
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            overridePendingTransition(0, 0);
            finishAffinity();
            super.onBackPressed();
        }
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}