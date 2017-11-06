package com.example.thanh.ssound;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.thanh.ssound.common.MeasureSource;
import com.example.thanh.ssound.common.Measurement;
import com.example.thanh.ssound.common.MeasurementResult;
import com.example.thanh.ssound.screen.FileScreen;
import com.example.thanh.ssound.screen.MainScreenFragment;
import com.example.thanh.ssound.screen.StatisticFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MeasurementResult{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    Switch healthSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add navigation bar to app
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //checkpermission
        checkAllPermission();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //make application display in tab
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager, true);

        //set switch button to item menu
        navigationView.getMenu().findItem(R.id.nav_warning).setActionView(new Switch(this));

        //catch event
        healthSwitch = (Switch) navigationView.getMenu().findItem(R.id.nav_warning).getActionView();
        healthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    //start service
                    startService(new Intent(MainActivity.this, BackgroundService.class));
                } else {
                    //stop service
                    stopService(new Intent(MainActivity.this, BackgroundService.class));
                }
            }
        });

        //catch on navigation bar item click
        navigationView.setNavigationItemSelectedListener(this);

        //add advertise
        MobileAds.initialize(this, "ca-app-pub-5869339706395652~9980150895");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.Banner));

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6430AEB378BEF21DC2B07D2E224846B9")
                .build();

        adView.loadAd(adRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Load config file
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("config.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                //set value to switch button
                healthSwitch.setChecked(Boolean.parseBoolean(stringBuilder.toString()));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        //write data to config file
        if(healthSwitch!=null){
            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(String.valueOf(healthSwitch.isChecked()));
                outputStreamWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //check all permistion that app use from device (use from Android 6.0)
    private void checkAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this,
                            "Video app required access to microphone", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.SYSTEM_ALERT_WINDOW}, 0);
                return;
            }

        }
    }

    //processing with navigation bar
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

        if (id == R.id.nav_warning) {   // on/off health warming
            Switch healthSwitch =(Switch) item.getActionView();
            if(healthSwitch.isChecked()){
                healthSwitch.setChecked(false);
            }else{
                healthSwitch.setChecked(true);
            }
        } else if (id == R.id.nav_feedback) {   // open feedback screen
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        }

        if(id!=R.id.nav_warning) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void setDecibel(double decibel) {
        Log.d("Audio----------",String.valueOf(decibel));
    }


    //Processing with paging in tab view
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new MainScreenFragment();
                case 1: return new StatisticFragment();
                case 2: return new FileScreen();

            }
            return null;

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
