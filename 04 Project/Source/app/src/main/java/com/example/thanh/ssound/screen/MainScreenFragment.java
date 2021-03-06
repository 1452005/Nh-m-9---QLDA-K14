package com.example.thanh.ssound.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thanh.ssound.R;
import com.example.thanh.ssound.common.MeasureSource;
import com.example.thanh.ssound.common.Measurement;
import com.example.thanh.ssound.common.MeasurementResult;
import com.example.thanh.ssound.common.Wheel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Thanh on 10/22/2017.
 */
public class MainScreenFragment extends Fragment implements MeasurementResult {


    Wheel wheel;

    //use for grapview
    LineGraphSeries<DataPoint> series;
    double y,x;
    GraphView graphView;

    //Measure sound
    Measurement measurement;
    boolean isMeasuring=false;

    Thread realTime;
    float decibel=0;
    TextView txtguide;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_screen, container, false);

        //get id widget
        txtguide=(TextView)rootView.findViewById(R.id.guide);
        wheel = (Wheel)rootView.findViewById((R.id.wheel));

        //prepare for measure
        measurement = Measurement.createInstance(MeasureSource.MIC, this);
        wheel.setOnClickListener(new View.OnClickListener() {
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

        //draw graph
        x = 0;
        graphView = (GraphView)rootView.findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        graphView.addSeries(series);
        Viewport viewport=graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMinY(0);
        viewport.setMaxY(120);
        viewport.setScrollable(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }


    // setup thread for realtime graph
    private void setupThread(){
        realTime =  new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMeasuring){
                    x+=0.01;
                        series.appendData(new DataPoint(x, decibel), true, 100);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    //get decibel
    @Override
    public void setValue(double decibel) {
        wheel.setDecibel((float)decibel);
        this.decibel=(float)decibel;
    }


    //start measure
    public void start(){
        isMeasuring = true;
        txtguide.setText("Tap again to stop");
        setupThread();
        measurement.start();
        realTime.start();

    }

    //stopmeasure
    public void stop(){
        isMeasuring = false;
        txtguide.setText("Tap on the wheel to measure");
        realTime = null;
        wheel.setDecibel(0);
        measurement.stop();
        decibel = 0;
    }


}
