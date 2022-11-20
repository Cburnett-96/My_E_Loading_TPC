package com.aqp.mye_loading.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.aqp.mye_loading.MainActivity;

public class MessageReceiver extends BroadcastReceiver {

    public static MessageListener mListener;
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String Number = prefs.getString("number", null);
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");

        SmsMessage[] smsMessage = new SmsMessage[pdus.length];
        String no = "8724";

        String SMS = "android.provider.Telephony.SMS_RECEIVED";

        if (intent.getAction().equals(SMS)){
            for (int i = 0; i < pdus.length; i++) {
                smsMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                /*smsMessage =
                if (smsMessage.getOriginatingAddress().equals(no))
                    return;
                message = "Sender: " + smsMessage.getMessageBody();
                mListener.messageReceived(message);*/
            }
            if(!smsMessage[0].getOriginatingAddress().equals(no))
                return;
            //Toast.makeText(context, smsMessage[0].getMessageBody(), Toast.LENGTH_SHORT).show();
            //System.out.println(Number);

            Intent intents = new Intent(context, MainActivity.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intents.setAction(MainActivity.SMSRECEVID);
            intents.putExtra("no", Number);
            context.sendBroadcast(intents);
            context.startActivity(intents);
        }
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}
