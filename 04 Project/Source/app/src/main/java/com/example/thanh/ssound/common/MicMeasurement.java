package com.example.thanh.ssound.common;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by Thanh on 10/6/2017.
 */
public class MicMeasurement extends Measurement {

    private MediaRecorder mediaRecorder;
    protected MicMeasurement(MeasurementResult output) {
        super(output);

    }

    private void prepare(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile("/dev/null");
    }
    @Override
    public void start()  {
        runnable=new Runnable() {
            @Override
            public void run() {
                updateStatus();
                handler.postDelayed(runnable,100);
            }
        };
        try {
            prepare();
            mediaRecorder.prepare();
            mediaRecorder.start();
            runnable.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStatus() {
        measurementResult.setDecibel(20 * Math.log10(mediaRecorder.getMaxAmplitude()));
    }

    @Override
    public void stop() {
        if(mediaRecorder!=null) {
            handler.removeCallbacks(runnable);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder=null;
        }
    }


}
