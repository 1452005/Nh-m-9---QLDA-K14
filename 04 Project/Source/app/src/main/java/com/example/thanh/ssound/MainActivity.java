package com.example.thanh.ssound;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MeasurementResult {

    Wheel wheel;
    BarGraphSeries<DataPoint> series;

    double y,x;
    GraphView graphView;
    Measurement measurement;
    boolean isMeasuring=false;
    Thread realTime;
    float decibel=0;
    TextView txtguide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //measure

        //checkpermission
        checkAllPermission();

        txtguide=(TextView)findViewById(R.id.guide);
        wheel = (Wheel) findViewById((R.id.wheel));
        measurement = Measurement.createInstance(MeasureSource.MIC, this);
        wheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMeasuring){
                    isMeasuring=false;
                    measurement.stop();
                    realTime=null;

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
        graphView = (GraphView) findViewById(R.id.graph);
        series = new BarGraphSeries<>();
        graphView.addSeries(series);
        Viewport viewport=graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMinY(0);
        viewport.setMaxY(120);
        viewport.setScrollable(true);


    }


    @Override
    protected void onResume() {
        super.onResume();
        setupThread();
    }

    private void setupThread(){
        realTime =  new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMeasuring){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            x+=0.01;
                            series.appendData(new DataPoint(x, decibel),true,100);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this,
                            "Video app required access to microphone", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setDecibel(double decibel) {
        wheel.setDecibel((float)decibel);
        this.decibel=(float)decibel;
    }
}
