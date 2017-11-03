package com.example.thanh.ssound;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.WindowManager;


import com.example.thanh.ssound.common.MeasureSource;
import com.example.thanh.ssound.common.Measurement;
import com.example.thanh.ssound.common.MeasurementResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Thanh on 10/29/2017.
 */
public class BackgroundService extends Service implements MeasurementResult {
    Timer mTimer;
    boolean isshow=false;
    List<Integer> decibels=new ArrayList<>();
    int maxDecibel;
    Handler handler=new Handler();
    String currentDate;

    TimerTask mTimerTask=new TimerTask() {
        @Override
        public void run() {
            if(decibel>65 && decibel!=73) {
                if(isshow==false) {

                    notify1();
                    handler.post(runable);
                    isshow = true;
                }
            }else {
                isshow =false;
            }

            String pattern = "dd-MM-yyyy";
            String date =new SimpleDateFormat(pattern).format(new Date());
            if(decibel>maxDecibel){
                maxDecibel=decibel;
            }
            if(currentDate!=date){
                decibels.add(maxDecibel);
                if(decibels.size()>30){
                    decibels.remove(0);
                }
                writeData();
            }
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        maxDecibel=0;
        //get date
        String pattern = "dd-MM-yyyy";
        currentDate =new SimpleDateFormat(pattern).format(new Date());


        //read file
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("data.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";


                while ((receiveString = bufferedReader.readLine()) != null) {
                    decibels.add(Integer.parseInt(receiveString));
                }
                inputStream.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        mTimer=new Timer();
        mTimer.schedule(mTimerTask,2000,2000);
        Measurement m= Measurement.createInstance(MeasureSource.OUTPUT,this);
        m.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        mTimerTask.cancel();
        writeData();
        super.onDestroy();
    }

    private void writeData() {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            for(int i=0; i< decibels.size();i++){
                outputStreamWriter.write(String.valueOf(decibels.get(i)));
            }
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int decibel;
    public void notify1(){

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("RSSPullService");
        Intent myinIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        PendingIntent pendingIntent=PendingIntent.getActivity(getBaseContext(),0,myinIntent,Intent.FILL_IN_ACTION);

        Context context=getApplicationContext();
        Notification.Builder builder;

        builder=new Notification.Builder(context)
                .setContentTitle("Warning")
                .setContentText("This sound level can effect on your ear")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification=builder.build();
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
       }

    @Override
    public void setDecibel(double decibel) {
        this.decibel=(int)decibel;
    }

    final Runnable runable = new Runnable() {
        public void run() {
            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("This sound level can effect on your ear");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            alertDialog.show();
        }
    };
}
