package com.aqp.mye_loading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aqp.mye_loading.other.DBHandler;

import java.util.Objects;

public class AddPromoActivity extends AppCompatActivity {

    EditText edtPromoCode, edtSMS, edtCall, edtData, edtValidity, edtPrice;
    Button btnAddPromo, btnClose;
    TextView tvTelecomName;
    private DBHandler dbHandler;
    String telecom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_add_promo);

        if (Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white_smoke));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        edtCall = findViewById(R.id.idEdtCall);
        edtData = findViewById(R.id.idEdtData);
        edtPrice = findViewById(R.id.idEdtPrice);
        edtPromoCode = findViewById(R.id.idEdtPromoCode);
        edtSMS = findViewById(R.id.idEdtSMS);
        edtValidity = findViewById(R.id.idEdtValidity);
        btnAddPromo = findViewById(R.id.btn_Add);
        btnClose = findViewById(R.id.btn_Close);
        tvTelecomName = findViewById(R.id.textView_telecomName);

        dbHandler = new DBHandler(AddPromoActivity.this);

        Intent intent = getIntent();
        telecom = intent.getStringExtra("telecom");
        tvTelecomName.setText("Add Promo For: "+telecom);

        btnAddPromo.setOnClickListener(view -> {
            String promoCode = edtPromoCode.getText().toString().trim();
            String SMS = edtSMS.getText().toString().trim();
            String Call = edtCall.getText().toString().trim();
            String Data = edtData.getText().toString().trim();
            String Validity = edtValidity.getText().toString().trim();
            String Price = edtPrice.getText().toString().trim();

            if (promoCode.isEmpty() && SMS.isEmpty() && Call.isEmpty() &&
                    Data.isEmpty() && Validity.isEmpty() && Price.isEmpty()) {
                Toast.makeText(AddPromoActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHandler.addNewPromo(telecom, promoCode, SMS, Call, Data, Validity, Price);

            Toast.makeText(AddPromoActivity.this, "Promo has been added!", Toast.LENGTH_SHORT).show();

            edtCall.setText("");
            edtData.setText("");
            edtPrice.setText("");
            edtPromoCode.setText("");
            edtSMS.setText("");
            edtValidity.setText("");
        });

        btnClose.setOnClickListener(view -> {
            Intent intent1 = new Intent(AddPromoActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });
    }
}