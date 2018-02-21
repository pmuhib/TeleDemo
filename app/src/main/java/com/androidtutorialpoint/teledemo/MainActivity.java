package com.androidtutorialpoint.teledemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    int PERMISSION_PHONE=20;
    TextToSpeech tt;
    TextView textView;
    TelephonyManager tm;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text1);
        next = (Button) findViewById(R.id.next);
        tt = new TextToSpeech(this, this);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CallState.class));
            }
        });

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED)
        {
            phoneInfo();
        }
        else
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE))
            {
                Snackbar.make(findViewById(android.R.id.content),"",Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_PHONE);
                    }
                }).show();
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_PHONE);
            }
        }
    }

    private void phoneInfo() {
        String IMEINumber=tm.getDeviceId();
        String SerialNumber=tm.getSimSerialNumber();
        String VoiceMailNumber=tm.getVoiceMailNumber();
        String DeviceSoftwareVerion=tm.getDeviceSoftwareVersion();
        String NetworkCountryIso=tm.getNetworkCountryIso();
        String SimOperator=tm.getSimOperator();
        String SubscriberId=tm.getSubscriberId();
        String SimOperatorName=tm.getSimOperatorName();
        String Phonetype="";
        String Networkoperator=tm.getNetworkOperator();
        int mcc= Integer.parseInt(Networkoperator.substring(0,3));
        int mnc= Integer.parseInt(Networkoperator.substring(3));

       /* What is MCC?
        MCC stands for mobile country code, it's an unique three digit code assigned and managed by ITU to identify the country which a mobile subscriber belongs. For example, MCC 310 is used by US.
        What is MNC?
        MNC stands for mobile network code, this unique 2 or 3 digit code is used to identify a carrier together with MCC. For example, you see 310-090, you know it's AT&T in USA.
        What is LAC?
        LAC stands for location area code, it's used by carriers to identify a location area where the cell towers are located. However it's not unique, different countries and carriers could have the same LAC. In general, you know MCC, MNC and LAC, you could know the approximate location of a cell phone.
        What is Cell ID?
        Cell ID is used to identify a base transceiver station within a LAC. A cell tower could have more than one (1) cell id. You know a Cell ID, you know the location of a cell phone. You know more than 3 cell towers, you can pinpoint a cell phone.*/

        CellLocation location=tm.getCellLocation();
        GsmCellLocation cellLocation= (GsmCellLocation)location;

        int cid=cellLocation.getCid();
        int lac=cellLocation.getLac();
        int phntype=tm.getPhoneType();

        switch (phntype)
        {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                Phonetype="CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                Phonetype="GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                Phonetype="NONE";
                break;
        }
        boolean isRoaming=tm.isNetworkRoaming();
        String res=getCellInfo(false);
        textView.setText("IMEINumber="+IMEINumber+"\nSerialNumber="+SerialNumber+"\nVoiceMailNumber="+VoiceMailNumber+
                "\nDeviceSoftwareVerion="
                +DeviceSoftwareVerion+"\nNetworkCountryIso="+NetworkCountryIso+"\nSimOperator="+SimOperator+
                "\nSubscriberId="+SubscriberId+"\nSimOperatorName="+SimOperatorName+"\n Network Operator="+Networkoperator
                +"\nPhonetype="+Phonetype+"\nIs in Roaming="+isRoaming+"\n Location="+res+"\n Mcc="+mcc+"\n Mnc="+mnc+
                "\n Lac="+lac+"\n Cid="+cid);
        PhoneStateListener stateListener=new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if(state==TelephonyManager.CALL_STATE_RINGING)
                {
                    tt.speak(incomingNumber+"calling",TextToSpeech.QUEUE_FLUSH,null);
                    Toast.makeText(getApplicationContext(),"Phone Is Ringing",Toast.LENGTH_LONG).show();
                }
            }
        };
        tm.listen(stateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    public String getCellInfo(boolean cidOnly) {
        List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();
        StringBuffer buf = new StringBuffer();
        String tempResult = "";
        if (infos.size() > 0) {
            for (NeighboringCellInfo info : infos) {
                tempResult = cidOnly ? info.getCid() + ";" : info.getLac() + ","
                        + info.getCid() + "," + info.getRssi() + info.getCid()+ ";";

                buf.append(tempResult);
             //   Toast.makeText(getBaseContext(),info.getCid()+"\n"+info.getLac()+"\n"+info.getRssi(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(),tempResult,Toast.LENGTH_SHORT).show();
            }
            // Removes the trailing semicolon
            buf.deleteCharAt(buf.length() - 1);
            return buf.toString();
        } else {
            return null;
        }
    }

    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS)
        {
            int result=tt.setLanguage(Locale.US);
            if(result==TextToSpeech.LANG_MISSING_DATA ||result==TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(getBaseContext(),"Working",Toast.LENGTH_SHORT).show();

            } else {
            }

        } else {
            Toast.makeText(getBaseContext(),"Intialization Failed",Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    protected void onDestroy() {
        if(tt!=null)
        {
            tt.stop();
            tt.shutdown();
        }
        super.onDestroy();
    }
    /* String[] permission=new String[]{Manifest.permission_group.PHONE};

        //ActivityCompat.checkSelfPermission(Manifest.permission_group.LOCATION);
        ActivityCompat.requestPermissions(MainActivity.this,permission,1);
        String[] per=new String[]{Manifest.permission_group.LOCATION};
        ActivityCompat.requestPermissions(MainActivity.this,per,2);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1:
            {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    phoneInfo();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();


                    return;
                }
                }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    phoneInfo();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
*/
}
