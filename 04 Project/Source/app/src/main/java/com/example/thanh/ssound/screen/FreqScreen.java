package com.example.thanh.ssound.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thanh.ssound.R;
import com.example.thanh.ssound.common.MeasureSource;
import com.example.thanh.ssound.common.Measurement;
import com.example.thanh.ssound.common.MeasurementResult;
import com.example.thanh.ssound.common.Wheel;

/**
 * Created by Thanh on 11/26/2017.
 */
public class FreqScreen extends Fragment implements MeasurementResult {
    TextView txtResult;
    Button btnStartStop;
    private boolean isMeasuring=false;
    private Measurement measurement;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.freq_screen, container, false);

        //get id widget
        txtResult=(TextView)rootView.findViewById(R.id.txtFreq);
        btnStartStop = (Button) rootView.findViewById((R.id.btnMeasure));
        measurement = Measurement.createInstance(MeasureSource.FREQ, this);
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isMeasuring) {
                        stop();

                    } else {
                        start();
                    }
                }catch (Exception e) {

                }
            }
        });
        return rootView;
    }

    public void stop() {
        try {
            isMeasuring = false;
            measurement.stop();
            btnStartStop.setText("Start");
        }catch (Exception e){

        }
    }
    private  void start(){
        isMeasuring=true;
        measurement.start();
        btnStartStop.setText("Stop");

    }

    @Override
    public void setDecibel(double decibel) {
        txtResult.setText(String.valueOf(decibel)+" Hz");
    }
}
