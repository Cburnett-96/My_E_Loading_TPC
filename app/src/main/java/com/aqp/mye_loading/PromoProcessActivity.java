package com.aqp.mye_loading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aqp.mye_loading.other.DBHandler;
import com.aqp.mye_loading.other.DBHandlerMustPromo;

import java.util.Objects;

public class PromoProcessActivity extends AppCompatActivity {
    String Number, PromoCode, telecom, Descriptions, Price;
    String ServerNumber = "8724";
    int Price2;

    TextView textViewNumber, textViewPromo, textViewPrice, textViewPromoCode;
    Button btnSend, btnClose;
    ImageView imageViewTelecom;

    private DBHandlerMustPromo dbHandler;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_promo_process);

        if (Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white_smoke));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        textViewNumber = findViewById(R.id.textView_NumberFinal);
        textViewPromo = findViewById(R.id.textView_Promo);
        textViewPrice = findViewById(R.id.textView_Price);
        textViewPromoCode = findViewById(R.id.textView_PromoCode);
        btnSend = findViewById(R.id.btn_send);
        btnClose = findViewById(R.id.btn_close);
        imageViewTelecom = findViewById(R.id.imageViewTelecom);

        dbHandler = new DBHandlerMustPromo(PromoProcessActivity.this);

        Intent intent = getIntent();
        Number = intent.getStringExtra("number");
        telecom = intent.getStringExtra("telecom");
        PromoCode = intent.getStringExtra("promoCode");
        Descriptions = intent.getStringExtra("description");
        Price = "Total Price: "+ intent.getStringExtra("price");
        Price2 = Integer.parseInt(intent.getStringExtra("price"));

        if (Objects.equals(telecom, "TNT") || Objects.equals(telecom, "Smart")){
            imageViewTelecom.setImageResource(R.raw.smart_tnt);
        }

        textViewNumber.setText(Number);
        textViewPromo.setText(Descriptions);
        textViewPrice.setText(Price);
        textViewPromoCode.setText(PromoCode);

        btnClose.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> Send());

    }

    private void Send(){
        Toast.makeText(getBaseContext(), "Processing...!",
                Toast.LENGTH_SHORT).show();
        btnSend.setVisibility(View.GONE);

        String SMS = Number+" "+PromoCode;

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
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic Failure",
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No Service!",
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio Off!",
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
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
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS Not Delivered!",
                                Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        dbHandler.addMustPromo(telecom, PromoCode, Descriptions, Price2);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(ServerNumber, null, SMS, sentPI, deliveredPI);
    }
}