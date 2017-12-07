package com.example.thanh.ssound.common;

import android.media.audiofx.Visualizer;

/**
 * Created by Thanh on 10/6/2017.
 */
public class OutputMeasurement extends Measurement {
    private Visualizer visualizer;

    protected OutputMeasurement(MeasurementResult result) {
        super(result);

        //configure for recorduing
        visualizer=new Visualizer(0);
        visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);

    }

    //start measure
    @Override
    public void start(){
        stop();
        runnable=new Runnable() {
            @Override
            public void run() {
                updateStatus();
                handler.postDelayed(runnable,10);
            }
        };

        visualizer.setEnabled(true);
        runnable.run();
    }

    //update result to interface
    private void updateStatus() {
        Visualizer.MeasurementPeakRms measurementPeakRms = new Visualizer.MeasurementPeakRms();
        visualizer.getMeasurementPeakRms(measurementPeakRms);
        int decibel=(int)(20*Math.log10(Math.abs(5000+ measurementPeakRms.mRms)));
        if(decibel<73) {
            measurementResult.setValue(decibel);
        }else {
            measurementResult.setValue(0);
        }

    }

    //stop measure
    @Override
    public void stop() {
        handler.removeCallbacks(runnable);
        visualizer.setEnabled(false);
    }
}
