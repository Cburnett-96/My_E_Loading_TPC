package com.aqp.mye_loading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RefundActivity extends AppCompatActivity {
    EditText edtTraceNumber;
    Button btnRefund, btnClose;
    RadioGroup radioGroupSentTo;
    String gateway;
    EditText textViewSMS;
    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_refund);

        if (Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white_smoke));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        edtTraceNumber = findViewById(R.id.idEdtTraceNumber);
        textViewSMS = findViewById(R.id.textView_Messages);
        btnRefund = findViewById(R.id.btn_Refund);
        btnClose = findViewById(R.id.btn_Close);
        radioGroupSentTo = findViewById(R.id.radioGroupSendTo);
        textInputLayout = findViewById(R.id.text_input_end_icon);

        GetSMS();

        radioGroupSentTo.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButtonSelect = findViewById(checkedId);
            if(radioButtonSelect != null){
                gateway = radioButtonSelect.getText().toString();
            }
        });

        btnRefund.setOnClickListener(view -> {
            if (!edtTraceNumber.getText().toString().isEmpty()){
                if (gateway != null){
                    Send();
                } else {
                    Toast.makeText(RefundActivity.this, "Please select gateway!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RefundActivity.this, "Please input trace number!", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(view -> finish());

        textInputLayout.setEndIconOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String filter = clipboard.getPrimaryClip().toString().trim().replaceAll("[^0-9]", "");
            edtTraceNumber.setText(filter);
        });

    }
    private void Send(){
        Toast.makeText(getBaseContext(), "Processing...!",
                Toast.LENGTH_SHORT).show();
        btnRefund.setVisibility(View.GONE);

        String SMS = "RF "+edtTraceNumber.getText().toString().trim();

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message Sent!",
                                Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> finish(), 1000);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic Failure",
                                Toast.LENGTH_SHORT).show();
                        btnRefund.setVisibility(View.VISIBLE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No Service!",
                                Toast.LENGTH_SHORT).show();
                        btnRefund.setVisibility(View.VISIBLE);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        btnRefund.setVisibility(View.VISIBLE);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio Off!",
                                Toast.LENGTH_SHORT).show();
                        btnRefund.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }, new IntentFilter(SENT));

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Delivered!",
                                Toast.LENGTH_SHORT).show();
                        btnRefund.setVisibility(View.VISIBLE);
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS Not Delivered!",
                                Toast.LENGTH_SHORT).show();
                        btnRefund.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(gateway, null, SMS, sentPI, deliveredPI);
    }

    private void GetSMS() {
        StringBuilder smsBuilder = new StringBuilder();

        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        @SuppressLint("Recycle") Cursor cur = getContentResolver().query(uriSMSURI, null, "address='8724'", null,"date asc");

        if (cur.moveToFirst()) {
            while (cur.moveToNext()){
                String Body= cur.getString(cur.getColumnIndexOrThrow("body"));
                smsBuilder.append("\n");
                smsBuilder.append(Body).append(" ");
                smsBuilder.append("\n");
            }
            textViewSMS.setText(smsBuilder);
            textViewSMS.setSelection(textViewSMS.getText().length());
        }else {
            Toast.makeText(this, "No SMS Found on 8724", Toast.LENGTH_SHORT).show();
        }
    }
}