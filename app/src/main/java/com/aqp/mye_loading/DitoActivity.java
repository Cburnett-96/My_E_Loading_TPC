package com.aqp.mye_loading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aqp.mye_loading.adapter.AdapterPromoList;
import com.aqp.mye_loading.model.PromoList;
import com.aqp.mye_loading.other.DBHandler;
import com.aqp.mye_loading.other.DBHandlerMustPromo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class DitoActivity extends AppCompatActivity {
    private ArrayList<PromoList> promoLists;
    private DBHandler dbHandler;
    private DBHandlerMustPromo dbHandlerMustPromo;
    RecyclerView recyclerViewPromoList;

    LinearLayout layoutProcess, layoutFinalizing;
    TextView textViewNumber, textViewNumberFinal, textViewPromo;
    Button btnBack, btnNextToSend, btnSend, btnClose;
    Button btn5,btn10,btn20,btn25,btn30,btn50,btn100,btn200;
    String Number, Promo, telecom;
    String ServerNumber = "8724";
    EditText edtAmount;
    com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButton;
    BottomSheetDialog bottomSheetDialog;

    CoordinatorLayout coordinatorLayoutRegular;
    TextView selectTask1, itemRegular, itemPromo;
    ColorStateList def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_dito);

        if (Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white_smoke));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        recyclerViewPromoList = findViewById(R.id.recycleViewPromoList);
        layoutProcess = findViewById(R.id.layoutProcess);
        layoutFinalizing = findViewById(R.id.layoutFinalizing);
        textViewNumber = findViewById(R.id.textView_Number);
        textViewNumberFinal = findViewById(R.id.textView_NumberFinal);
        textViewPromo = findViewById(R.id.textView_Promo);
        btnBack = findViewById(R.id.backButton);
        btnNextToSend = findViewById(R.id.btn_nextToSend);
        btnSend = findViewById(R.id.btn_send);
        btnClose = findViewById(R.id.btn_close);
        floatingActionButton = findViewById(R.id.fabFilter);

        btn5 = findViewById(R.id.btn_5);
        btn10 = findViewById(R.id.btn_10);
        btn20 = findViewById(R.id.btn_20);
        btn25 = findViewById(R.id.btn_25);
        btn30 = findViewById(R.id.btn_30);
        btn50 = findViewById(R.id.btn_50);
        btn100 = findViewById(R.id.btn_100);
        btn200 = findViewById(R.id.btn_200);
        edtAmount = findViewById(R.id.editTextAmount);

        coordinatorLayoutRegular = findViewById(R.id.coordinatorRegular);
        selectTask1 = findViewById(R.id.selectTask1);
        itemRegular = findViewById(R.id.itemRegular);
        itemPromo = findViewById(R.id.itemPromo);

        def = itemPromo.getTextColors();
        dbHandler = new DBHandler(DitoActivity.this);
        dbHandlerMustPromo = new DBHandlerMustPromo(DitoActivity.this);

        floatingActionButton.setOnClickListener(this::FilterPromo);

        itemRegular.setOnClickListener(v -> {
            selectTask1.animate().x(0).setDuration(100);
            itemRegular.setTextColor(getResources().getColor(R.color.black));
            itemPromo.setTextColor(def);
            clearData();
            coordinatorLayoutRegular.setVisibility(View.VISIBLE);
            recyclerViewPromoList.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
        });
        itemPromo.setOnClickListener(v -> {
            recyclerViewPromoList.setVisibility(View.VISIBLE);
            setAdapter();
            itemPromo.setTextColor(getResources().getColor(R.color.black));
            itemRegular.setTextColor(def);
            int size = itemPromo.getWidth();
            selectTask1.animate().x(size).setDuration(100);
            coordinatorLayoutRegular.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
        });

        promoLists = new ArrayList<>();

        Intent intent = getIntent();
        Number = intent.getStringExtra("number");
        telecom = intent.getStringExtra("telecom");

        textViewNumber.setText(Number);

        btn5.setOnClickListener(v -> edtAmount.setText(btn5.getText().toString().trim()));
        btn10.setOnClickListener(v -> edtAmount.setText(btn10.getText().toString().trim()));
        btn20.setOnClickListener(v -> edtAmount.setText(btn20.getText().toString().trim()));
        btn25.setOnClickListener(v -> edtAmount.setText(btn25.getText().toString().trim()));
        btn30.setOnClickListener(v -> edtAmount.setText(btn30.getText().toString().trim()));
        btn50.setOnClickListener(v -> edtAmount.setText(btn50.getText().toString().trim()));
        btn100.setOnClickListener(v -> edtAmount.setText(btn100.getText().toString().trim()));
        btn200.setOnClickListener(v -> edtAmount.setText(btn200.getText().toString().trim()));
        btnBack.setOnClickListener(v -> finish());

        btnNextToSend.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
            ProcessLoad();
        });

        btnClose.setOnClickListener(v -> {
            layoutProcess.setVisibility(View.VISIBLE);
            layoutFinalizing.setVisibility(View.GONE);
        });

        btnSend.setOnClickListener(v -> Send());
    }

    private void FilterPromo(View view){
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View showFilter = inflater.inflate(R.layout.bottom_sheet_filter, null);
        bottomSheetDialog = new BottomSheetDialog(DitoActivity.this);
        bottomSheetDialog.setContentView(showFilter);
        bottomSheetDialog.show();

        RadioButton radioButtonAZ = showFilter.findViewById(R.id.radioButtonAZ);
        RadioButton radioButtonZA = showFilter.findViewById(R.id.radioButtonZA);
        RadioButton radioButtonLowHigh = showFilter.findViewById(R.id.radioButtonLowHigh);
        RadioButton radioButtonHighLow = showFilter.findViewById(R.id.radioButtonHighLow);

        AdapterPromoList adapter = new AdapterPromoList(promoLists);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPromoList.setLayoutManager(layoutManager);
        recyclerViewPromoList.setItemAnimator(new DefaultItemAnimator());

        radioButtonAZ.setOnClickListener(view1 -> {
            Collections.sort(promoLists, new Comparator<PromoList>() {
                @Override
                public int compare(PromoList a, PromoList b) {
                    return a.promocode.compareTo(b.promocode);
                }
            });
            recyclerViewPromoList.setAdapter(adapter);
            bottomSheetDialog.dismiss();
        });
        radioButtonZA.setOnClickListener(view1 -> {
            Collections.sort(promoLists, new Comparator<PromoList>() {
                @Override
                public int compare(PromoList a, PromoList b) {
                    return b.promocode.compareTo(a.promocode);
                }
            });
            recyclerViewPromoList.setAdapter(adapter);
            bottomSheetDialog.dismiss();
        });
        radioButtonLowHigh.setOnClickListener(view1 -> {
            Collections.sort(promoLists, new Comparator<PromoList>() {
                @Override
                public int compare(PromoList a, PromoList b) {
                    return a.price - b.price;
                }
            });
            recyclerViewPromoList.setAdapter(adapter);
            bottomSheetDialog.dismiss();
        });
        radioButtonHighLow.setOnClickListener(view1 -> {
            Collections.sort(promoLists, new Comparator<PromoList>() {
                @Override
                public int compare(PromoList a, PromoList b) {
                    return b.price - a.price;
                }
            });
            recyclerViewPromoList.setAdapter(adapter);
            bottomSheetDialog.dismiss();
        });
    }

    private void ProcessLoad(){
        Promo = "Regular Load "+edtAmount.getText().toString().trim();

        if (TextUtils.isEmpty(edtAmount.getText())){
            Toast.makeText(DitoActivity.this, "Input or Choose Amount", Toast.LENGTH_SHORT).show();
        } else {
            textViewNumberFinal.setText(Number);
            textViewPromo.setText(Promo);

            layoutProcess.setVisibility(View.GONE);
            layoutFinalizing.setVisibility(View.VISIBLE);
        }


    }

    private void Send(){
        String SMS = Number+" "+edtAmount.getText().toString().trim();

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
                        new Handler().postDelayed(() -> finish(), 1000);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic Failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No Service!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio Off!",
                                Toast.LENGTH_SHORT).show();
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
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS Not Delivered!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        dbHandlerMustPromo.addMustPromo(telecom, edtAmount.getText().toString().trim(), Promo, Integer.parseInt(edtAmount.getText().toString().trim()));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(ServerNumber, null, SMS, sentPI, deliveredPI);

    }

    private void setAdapter(){
        promoLists = dbHandler.readPromo(telecom);

        AdapterPromoList adapter = new AdapterPromoList(promoLists);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerViewPromoList.setLayoutManager(staggeredGridLayoutManager);
        recyclerViewPromoList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPromoList.setAdapter(adapter);
    }

    public void clearData() {
        AdapterPromoList adapter = new AdapterPromoList(promoLists);
        promoLists.clear(); // clear list
        adapter.notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}