package com.example.thanh.ssound;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLScCK0cYhR5B22--scxiX6nBLUZ3Jr0_ABQtr4rXHKQQzCvUlA/viewform");

    }
}
