package com.example.thanh.ssound.common;


import android.os.Handler;

public abstract class Measurement {
    protected MeasurementResult measurementResult;
    protected Handler handler;
    protected Runnable runnable;
    public static Measurement createInstance(MeasureSource source, MeasurementResult outputResult){

        //check source
        if( source == MeasureSource.MIC){
            return new MicMeasurement(outputResult);
        }
        if( source==MeasureSource.OUTPUT){
            return new OutputMeasurement(outputResult);
        }
        return null;
    }


    protected Measurement(MeasurementResult output){
        measurementResult=output;
        handler=new Handler();
    }

    public abstract void start();

    public abstract void stop();
}
