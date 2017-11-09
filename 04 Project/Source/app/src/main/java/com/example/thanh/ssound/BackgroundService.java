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
import android.util.Log;
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

    private static final int REPEAT_TIME = 300;
    //decibel get from output device
    int decibel;

    //check for schedule check
    Timer mTimer;

    //popup is showing
    boolean isshow=false;

    //Use for save data
    List<Integer> decibels=new ArrayList<>();
    int maxDecibel;

    Handler handler=new Handler();
    String currentDate;

    //popup will repeat showing in 5 minute
    int repeatTime=0;

    TimerTask mTimerTask=new TimerTask() {
        @Override
        public void run() {

            //check condition show popup
            if(decibel>65 && decibel!=73 && repeatTime==0) {
                if(isshow==false) {

                    //notify and show popup
                    showNotify();
                    isshow = true;
                }
            }else {
                isshow =false;
                //update repeat time
                if(repeatTime>0) {
                    repeatTime -= 2;
                }
            }

            //check to save data to file
            String pattern = "dd-MM-yyyy";
            String date =new SimpleDateFormat(pattern).format(new Date());
            if(decibel>maxDecibel){
                maxDecibel=decibel;
            }
            if(currentDate.equals(date)==false){
                decibels.add(maxDecibel);
                if(decibels.size()>30){
                    decibels.remove(0);
                }
                writeData();
                maxDecibel=0;
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

        //start listening
        mTimer=new Timer();
        mTimer.schedule(mTimerTask,2000,2000);
        Measurement m= Measurement.createInstance(MeasureSource.OUTPUT,this);
        m.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //stop listening
        mTimer.cancel();
        mTimerTask.cancel();
        writeData();
        super.onDestroy();
    }

    //write data to file
    private void writeData() {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("data.txt", Context.MODE_PRIVATE));
            for(int i=0; i< decibels.size();i++){
                outputStreamWriter.write(String.valueOf(decibels.get(i))+"\n");
            }
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //push notify function

    public void showNotify(){

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

        //show popup
        handler.post(runable);
       }

    @Override
    public void setDecibel(double decibel) {
        this.decibel=(int)decibel;
    }


    //show popup warning
    final Runnable runable = new Runnable() {
        public void run() {
            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("This sound level can effect on your ear\n" +
                    " If you click cancel, this popup will never show again");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        //if choose cancel => stop service
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getApplicationContext().stopService(new Intent(getApplicationContext(),BackgroundService.class));
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            //if choose od => set repeat time to 5 minute = 300 second
                            dialog.dismiss();
                            repeatTime=REPEAT_TIME;
                        }
                    });
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            alertDialog.show();
        }
    };
}
