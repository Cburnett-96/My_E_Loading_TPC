package com.aqp.mye_loading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aqp.mye_loading.MainActivity;
import com.aqp.mye_loading.PromoProcessActivity;
import com.aqp.mye_loading.R;
import com.aqp.mye_loading.model.MustPromoList;
import com.aqp.mye_loading.other.DBHandler;
import com.aqp.mye_loading.other.DBHandlerMustPromo;

import java.util.ArrayList;

public class AdapterMustPromoList extends RecyclerView.Adapter<AdapterMustPromoList.MyViewHolder> {
    ArrayList<MustPromoList> promoLists;
    Context context;
    DBHandlerMustPromo dbHandler;
    Intent intent;

    public AdapterMustPromoList(ArrayList<MustPromoList> promoLists, Context context) {
        this.promoLists = promoLists;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterMustPromoList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.must_promo_list, parent, false);
        return new AdapterMustPromoList.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMustPromoList.MyViewHolder holder, int position) {
        String promocode = promoLists.get(position).getPromocode();
        int price = promoLists.get(position).getPrice();
        String descriptions = promoLists.get(position).getDescriptions();

        holder.tvPromoCode.setText(promocode);
        holder.tvPrice.setText("₱" + price + ".00");
        String filterText = descriptions.substring(0,11);
        holder.tvDescriptions.setText(Html.fromHtml(filterText+"...\n\n"+"<br/><font color='gray'>Click for more details!</font>"));

        holder.layoutPromos.setOnClickListener(view -> {
            String SMS = promoLists.get(position).getDescriptions();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext(), R.style.CustomAlertDialog);
            alertDialog.setTitle(promocode+" ₱" + price + ".00");
            alertDialog.setMessage(descriptions);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_input_number, null);
            alertDialog.setView(dialogView);
            EditText input = dialogView.findViewById(R.id.editTextNumberMustPromo);
            alertDialog.setIcon(R.drawable.ic_code);

            new Handler().postDelayed(() -> {
                input.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                input.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
            }, 200);

            alertDialog.setPositiveButton("Next",
                    (dialog, which) -> {
                        String Number = input.getText().toString().trim();

                        if (TextUtils.isEmpty(Number)) {
                            Toast.makeText(view.getContext(), "Input the Numbers", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Number.length() < 11) {
                            Toast.makeText(view.getContext(), "Numbers should be 11 digits", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        intent = new Intent(holder.layoutPromos.getContext(), PromoProcessActivity.class);
                        intent.putExtra("number", input.getText().toString().trim());
                        intent.putExtra("telecom", "Must");
                        intent.putExtra("promoCode", promoLists.get(position).getPromocode());
                        intent.putExtra("price", String.valueOf(promoLists.get(position).getPrice()));
                        intent.putExtra("description", SMS);
                        holder.layoutPromos.getContext().startActivity(intent);
                        Toast.makeText(input.getContext(),
                                "Double Check the NUMBER", Toast.LENGTH_SHORT).show();
                    });

            alertDialog.setNegativeButton("Close",
                    (dialog, which) -> dialog.cancel());

            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return promoLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromoCode, tvPrice, tvDescriptions;
        LinearLayout layoutPromos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPromos = itemView.findViewById(R.id.layoutPromos);
            tvPromoCode = itemView.findViewById(R.id.textViewPromoCode);
            tvPrice = itemView.findViewById(R.id.textViewPrice);
            tvDescriptions = itemView.findViewById(R.id.textViewDescriptions);

            dbHandler = new DBHandlerMustPromo(itemView.getContext());

        }
    }
}
