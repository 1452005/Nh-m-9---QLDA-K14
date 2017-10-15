package com.example.thanh.ssound;

import android.media.audiofx.Visualizer;
import android.os.Handler;

/**
 * Created by Thanh on 10/6/2017.
 */
public class OutputMeasurement extends Measurement {
    private Visualizer visualizer;

    protected OutputMeasurement(MeasurementResult result) {
        super(result);
        visualizer=new Visualizer(0);
        visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);

    }

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

    private void updateStatus() {
        Visualizer.MeasurementPeakRms measurementPeakRms = new Visualizer.MeasurementPeakRms();
        visualizer.getMeasurementPeakRms(measurementPeakRms);
        measurementResult.setDecibel(20*Math.log10(Math.abs(5000+ measurementPeakRms.mRms)));

    }

    @Override
    public void stop() {
        handler.removeCallbacks(runnable);
        visualizer.setEnabled(false);
    }
}
