package com.example.thanh.ssound.common;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import com.example.thanh.ssound.pack.RealDoubleFFT;

/**
 * Created by Thanh on 11/24/2017.
 */
public class FreqMeasurement extends Measurement {
    private static final double[] CANCELLED = {100};
    int frequency = 8000;/*44100;*/
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord audioRecord;
    private RealDoubleFFT transformer;
    int blockSize = /*2048;// = */256;
    boolean started = false;
    boolean CANCELLED_FLAG = false;
    double[][] cancelledResult = {{100}};
    int mPeakPos;
    double mHighestFreq;
    RecordAudio recordTask;
    int width;

    protected FreqMeasurement(MeasurementResult output) {
        super(output);
    }

    @Override
    public void start(){
        transformer = new RealDoubleFFT(blockSize);
        started=true;
        new RecordAudio().execute();
    }

    @Override
    public void stop() {
        started=false;
    }

    private class RecordAudio extends AsyncTask<Void, double[], Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
            int bufferReadResult;
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
            }
            while (started) {
                if (isCancelled() || (CANCELLED_FLAG == true)) {
                    started = false;
                    publishProgress(cancelledResult);
                    break;
                } else {
                    bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                    }
                    transformer.ft(toTransform);
                    publishProgress(toTransform);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(double[]...progress) {
            double mMaxFFTSample = 150.0;
            if(progress[0].length!=1) {
                if (width > 512) {
                    for (int i = 0; i < progress[0].length; i++) {
                        int x = 2 * i;
                        int downy = (int) (150 - (progress[0][i] * 10));
                        int upy = 150;
                        if (downy < mMaxFFTSample) {
                            mMaxFFTSample = downy;
                            //mMag = mMaxFFTSample;
                            mPeakPos = i;
                        }
                    }
                } else {
                    for (int i = 0; i < progress[0].length; i++) {
                        int x = i;
                        int downy = (int) (150 - (progress[0][i] * 10));
                        int upy = 150;
                        if (downy < mMaxFFTSample) {
                            mMaxFFTSample = downy;
                            mPeakPos = i;
                        }
                    }
                }
            }
            mHighestFreq = (((1.0 * frequency) / (1.0 * blockSize)) * mPeakPos)/2;
            measurementResult.setDecibel(mHighestFreq);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());
            }


        }
    }
}
