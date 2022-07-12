package com.aqp.mye_loading.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.aqp.mye_loading.MainActivity;
import com.aqp.mye_loading.other.MessageListener;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (Object o : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
            String message = "Sender: " + smsMessage.getMessageBody();
            mListener.messageReceived(message);
        }
        Intent intents = new Intent();
        intents.setAction(MainActivity.SMSRECEVID);
        context.sendBroadcast(intents);
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}
