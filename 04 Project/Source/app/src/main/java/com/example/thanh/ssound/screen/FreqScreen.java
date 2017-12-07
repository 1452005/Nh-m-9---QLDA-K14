package com.example.thanh.ssound.screen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thanh.ssound.R;
import com.example.thanh.ssound.common.MeasureSource;
import com.example.thanh.ssound.common.Measurement;
import com.example.thanh.ssound.common.MeasurementResult;
import com.example.thanh.ssound.common.Wheel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Thanh on 11/26/2017.
 */
public class FreqScreen extends Fragment implements MeasurementResult {
    TextView txtResult;
    TextView txtNote;
    private boolean isMeasuring=false;
    private Measurement measurement;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.freq_screen, container, false);

        //get id widget
        txtResult=(TextView)rootView.findViewById(R.id.txtFreq);
        txtNote=(TextView)rootView.findViewById(R.id.txtNote);
        measurement = Measurement.createInstance(MeasureSource.FREQ, this);
        return rootView;
    }

    public void stop() {
        try {
            isMeasuring = false;
            measurement.stop();
        }catch (Exception e){

        }
    }
    public void start(){
        try {
            isMeasuring = true;
            measurement.start();
            FreqCompute compute = new FreqCompute();
            compute.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            compute.execute();
        }catch (Exception e){

        }

    }
    int hz=1;
    @Override
    public void setValue(double hz) {
        txtResult.setText(String.valueOf((int)hz + " Hz"));
        this.hz=(int)hz;
    }


    private String prev="";
    private String notes[]={"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    private final float NOTE_C=65.5f;
    private String getNote(double hz) {
        try {
            double tmp = hz / NOTE_C;

            int interval =(int) Math.round((12 * Math.log10(tmp) / Math.log10(2)));
            int value = interval / 12;
            return notes[interval % 12] + String.valueOf(value+2);
        }catch (Exception e){
            return "";
        }
    }

    ArrayList<Integer> values=new ArrayList<Integer>();
    class FreqCompute extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            while(isMeasuring) {
                for (int i = 0; i < 500; i++) {
                    values.add(hz);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                publishProgress();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... prs) {
            try {
                int value=0;
                int max=0;
                for(int i=0;i<values.size();i++){
                    int f=Collections.frequency(values,values.get(i));
                    if(f>max){
                        max=f;
                        value=values.get(i);
                    }
                    if(f==max){
                        if(value<values.get(i))
                        {
                            value=values.get(i);
                        }
                    }
                }
                txtNote.setText(getNote(max));
                values.clear();

            }catch (Exception c){

            }
        }
    }
}
