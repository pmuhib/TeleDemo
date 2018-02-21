package com.androidtutorialpoint.teledemo;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

public class CallState extends AppCompatActivity {
    TelephonyManager tm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view=findViewById(android.R.id.content);
        setContentView(R.layout.activity_call_state);
        tm= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener stateListener=new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
            if(state==TelephonyManager.CALL_STATE_RINGING)
            {
                Snackbar.make(view,"Phone Is Ringing",Snackbar.LENGTH_LONG).show();
            }
            if(state==TelephonyManager.CALL_STATE_IDLE)
            {
                Snackbar.make(view,"Phone Is in Ideal Start",Snackbar.LENGTH_LONG).show();
            }
                if(state==TelephonyManager.CALL_STATE_OFFHOOK)
                {
                    Snackbar.make(view,"Phone is Currently in A call",Snackbar.LENGTH_LONG).show();
                }
            }
        };
        tm.listen(stateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }
}
