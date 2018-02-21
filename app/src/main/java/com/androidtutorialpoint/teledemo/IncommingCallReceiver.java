package com.androidtutorialpoint.teledemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class IncommingCallReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                Toast.makeText(context,"Phone Is Ringing",Toast.LENGTH_LONG).show();
            }
            if(state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                Toast.makeText(context, "Phone Is Idle", Toast.LENGTH_LONG).show();
            }
            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                Toast.makeText(context,"Phone is Currently in A call",Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e)
        {
            {e.printStackTrace();}
        }
    }
}
