package com.feasycom.feasybeacon.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Utils.AgreementUtil;


import java.util.Map;

public class AgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadData("Loading...", "8", "utf-8");
        request(type);
        ((TextView) findViewById(R.id.goback)).setOnClickListener(new MyOnClickListener());
        ((TextView) findViewById(R.id.title)).setText(type == 1 ? R.string.userAgreement : R.string.privacyAgreement);
    }

    private void request(int type){
        AgreementUtil.requestAgreement(type, new AgreementUtil.AgreementComplete() {
            @Override
            public void Complete(Map re) {
                Map data = (Map) re.get("data");
                String url = data.get("url").toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WebView webView = (WebView) findViewById(R.id.webView);
                        webView.loadUrl(url);
                    }
                });
            }
        });
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.goback:{
                    finish();
                    break;
                }
            }
        }
    }
}