package com.aqp.mye_loading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

class loadingDialog {
     private final Activity activity;
     private AlertDialog alertDialog;

     loadingDialog(Activity myactivity, int customAlertDialog)
     {
         activity= myactivity;
     }

     @SuppressLint("InflateParams")
     void startLoadingDialog()
     {
         AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
         LayoutInflater inflater = activity.getLayoutInflater();
         builder.setView(inflater.inflate(R.layout.custom_dialog_loading,null));
         builder.setCancelable(false);
         alertDialog = builder.create();
         alertDialog.show();
     }


     void dismisDialog()
     {
         alertDialog.dismiss();
     }
}
