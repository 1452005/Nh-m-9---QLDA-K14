package com.example.thanh.ssound;


import android.os.Handler;

/**
 * Created by Thanh on 10/6/2017.
 */

enum MeasureSource{
    MIC,
    OUTPUT
}
public abstract class Measurement {
    protected MeasurementResult measurementResult;
    protected Handler handler;
    protected Runnable runnable;
    public static Measurement createInstance(MeasureSource source, MeasurementResult outputResult){
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

    abstract void start();

    abstract void stop();
}
