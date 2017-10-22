package com.example.thanh.ssound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Thanh on 10/22/2017.
 */
public class MainScreenFragment extends Fragment implements MeasurementResult {

    Wheel wheel;
    LineGraphSeries<DataPoint> series;

    double y,x;
    GraphView graphView;
    Measurement measurement;
    boolean isMeasuring=false;
    Thread realTime;
    float decibel=0;
    TextView txtguide;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_screen, container, false);
        txtguide=(TextView)rootView.findViewById(R.id.guide);
        wheel = (Wheel)rootView.findViewById((R.id.wheel));
        measurement = Measurement.createInstance(MeasureSource.MIC, this);
        wheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMeasuring){
                    isMeasuring=false;
                    measurement.stop();
                    decibel=0;
                    realTime=null;
                    wheel.setDecibel(decibel);
                    txtguide.setText("Tap on the wheel to measure");

                }else {
                    isMeasuring=true;
                    setupThread();
                    measurement.start();
                    realTime.start();
                    txtguide.setText("Tap again to stop");

                }
            }
        });

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
        measurement.stop();
        realTime=null;
    }

    private void setupThread(){
        realTime =  new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMeasuring){
                    x+=0.01;
                    series.appendData(new DataPoint(x, decibel),true,100);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void setDecibel(double decibel) {
        wheel.setDecibel((float)decibel);
        this.decibel=(float)decibel;
    }
}
