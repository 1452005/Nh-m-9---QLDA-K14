package com.example.thanh.ssound.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thanh.ssound.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.List;

/**
 * Created by Thanh on 10/22/2017.
 */
public class StatisticFragment extends Fragment {
    GraphView graphView;
    BarGraphSeries<DataPoint> series;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistic_screen, container, false);
        graphView=(GraphView)rootView.findViewById(R.id.graph);
        series=new BarGraphSeries<>();
        getData();
        graphView.addSeries(series);
        Viewport viewport=graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(120);
        viewport.setMaxX(30);
        viewport.setScrollable(false);
        return rootView;
    }

    //get data for create graph
    private void getData(){
        DataPoint []points=new DataPoint[30];
        float a=0;
        for(int i=0;i<30;i++){

            points[i]= new DataPoint(i,Math.random()*120);
        }

        series.resetData(points);
    }
}
