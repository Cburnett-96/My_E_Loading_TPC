package com.aqp.mye_loading.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqp.mye_loading.GlobeTMProcessActivity;
import com.aqp.mye_loading.other.DBHandler;
import com.aqp.mye_loading.model.PromoList;
import com.aqp.mye_loading.PromoProcessActivity;
import com.aqp.mye_loading.R;

import java.util.ArrayList;

public class AdapterPromoList extends RecyclerView.Adapter<AdapterPromoList.MyViewHolder> {
    ArrayList<PromoList> promoLists;
    DBHandler dbHandler;
    String Number, telecom;
    Intent intent;

    public AdapterPromoList(ArrayList<PromoList> promoLists) {
        this.promoLists = promoLists;
    }

    @NonNull
    @Override
    public AdapterPromoList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_promo, parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AdapterPromoList.MyViewHolder holder, int position) {

        String promocode = promoLists.get(position).getPromocode();
        int price = promoLists.get(position).getPrice();
        String sms = promoLists.get(position).getSms();
        String call = promoLists.get(position).getCall();
        String data = promoLists.get(position).getData();
        String validity = promoLists.get(position).getValidity();

        holder.tvPromoCode.setText(promocode);
        holder.tvPrice.setText("₱"+price+".00");
        holder.tvSms.setText(sms);
        holder.tvCall.setText(call);
        holder.tvData.setText(data);
        holder.tvValidity.setText(validity);

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertExit = new AlertDialog.Builder(holder.btnDelete.getContext());
            alertExit.setTitle("Delete Promo")
                    .setMessage("Are you sure, want to delete this promo? " + promoLists.get(position).getPromocode())
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHandler.deleteHandler(promoLists.get(position).getId());
                        removeItem(position);
                        Toast.makeText(holder.btnDelete.getContext(), "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {

                    });
            AlertDialog dialog = alertExit.create();
            dialog.show();
        });

        holder.layoutPromos.setOnClickListener(view -> {
            //((Activity)holder.layoutPromos.getContext()).finish();
            String SMS = "SMS: " + promoLists.get(position).getSms() +
                    "\nCall: " + promoLists.get(position).getCall() +
                    "\nInternet Data: " + promoLists.get(position).getData() +
                    "\nValidity: "+ promoLists.get(position).getValidity();

            intent = new Intent(holder.layoutPromos.getContext(), PromoProcessActivity.class);
            intent.putExtra("number", Number);
            intent.putExtra("telecom", telecom);
            intent.putExtra("promoCode", promoLists.get(position).getPromocode());
            intent.putExtra("price", String.valueOf(promoLists.get(position).getPrice()));
            intent.putExtra("description", SMS);
            holder.layoutPromos.getContext().startActivity(intent);
        });
    }
    public void removeItem(int position) {
        promoLists.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public int getItemCount() {
        return promoLists.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromoCode, tvPrice, tvSms, tvCall, tvData, tvValidity;
        LinearLayout layoutPromos;
        Button btnDelete;
        public MyViewHolder(final View itemView) {
            super(itemView);
            layoutPromos = itemView.findViewById(R.id.layoutPromos);
            tvPromoCode = itemView.findViewById(R.id.textViewPromoCode);
            tvPrice = itemView.findViewById(R.id.textViewPrice);
            tvSms = itemView.findViewById(R.id.textViewText);
            tvCall = itemView.findViewById(R.id.textViewCall);
            tvData = itemView.findViewById(R.id.textViewData);
            tvValidity = itemView.findViewById(R.id.textViewValidity);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            dbHandler = new DBHandler(itemView.getContext());
            Intent intent = ((Activity) itemView.getContext()).getIntent();
            Number = intent.getStringExtra("number");
            telecom = intent.getStringExtra("telecom");
        }
    }

}
