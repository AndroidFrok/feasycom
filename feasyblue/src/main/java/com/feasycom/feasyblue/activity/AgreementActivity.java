package com.feasycom.feasyblue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.util.AgreementUtil;

import java.util.Map;

public class AgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        WebView webView = findViewById(R.id.webView);
        webView.loadData("Loading...", "8", "utf-8");
        request(type);
        findViewById(R.id.goback).setOnClickListener(new MyOnClickListener());
        ((TextView) findViewById(R.id.title)).setText(type == 1 ? R.string.userAgreement : R.string.privacyAgreement);
    }

    private void request(int type) {
        AgreementUtil.requestAgreement(type, re -> {
            Map data = (Map) re.get("data");
            String url = data.get("url").toString();
            Log.e("TAG", "request: " + url);
            runOnUiThread(() -> {
                WebView webView = findViewById(R.id.webView);
                webView.loadUrl(url);
            });
        });
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.goback: {
                    finish();
                    break;
                }
            }
        }
    }
}