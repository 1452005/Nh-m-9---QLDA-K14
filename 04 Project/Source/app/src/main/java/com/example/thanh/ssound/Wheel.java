package com.example.thanh.ssound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by Thanh on 10/13/2017.
 */
public class Wheel extends View {
    private Bitmap wheel= BitmapFactory.decodeResource(getResources(),R.drawable.wheel);
    private Bitmap arrow=BitmapFactory.decodeResource(getResources(),R.drawable.arrow);
    private Size screenSize;
    private int decibel=0;

    public Wheel(Context context, AttributeSet attrs) {
        super(context, attrs);
            screenSize=new Size(getWidth(),getHeight());

    }

    float degree=48;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(wheel, null, new RectF(0, 0, getWidth(), getHeight()), null);
        float x=getWidth()/2- arrow.getWidth()/2;
        float y= getHeight()/2-arrow.getWidth()/2;

        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate(degree,arrow.getWidth()/2,arrow.getWidth()/2);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(arrow, matrix, null);


        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);

        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(decibel)+" dB",getWidth()/2-paint.getStrokeWidth()/2,(float)(getHeight()*0.75),paint);
    }

    public void setDecibel(float decibel){
        if(!Float.isInfinite(decibel)) {
            this.decibel=(int)decibel;
            (new Task(45+decibel*(45f/20))).execute();
        }
    }

    private class Task extends AsyncTask<Void,Void,Void> {

        float destinaiton;
        public Task(float destination){
            this.destinaiton=destination;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            float distance=Math.abs(destinaiton-degree)/50;
            for(int i=0;i<50;i++){
                if(destinaiton>=degree) {
                    degree += distance;
                }else {
                    degree-=distance;
                }
                publishProgress();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            invalidate();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }



}
