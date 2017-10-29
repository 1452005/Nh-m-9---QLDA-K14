package com.example.thanh.ssound;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.example.thanh.ssound.common.MeasureSource;
import com.example.thanh.ssound.common.Measurement;
import com.example.thanh.ssound.common.MeasurementResult;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Thanh on 10/29/2017.
 */
public class BackgroundService extends Service implements MeasurementResult {
    Timer mTimer;
    boolean isshow=false;
    TimerTask mTimerTask=new TimerTask() {
        @Override
        public void run() {
            if(decibel>65 && decibel!=73) {
                if(isshow==false) {
                    notify1();
                    isshow = true;
                }
            }else {
                isshow =false;
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

        Intent intent=new Intent("com.example.thanh");
        intent.putExtra("yourvalue","store");
        super.onDestroy();
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
}
