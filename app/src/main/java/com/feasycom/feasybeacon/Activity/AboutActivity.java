package com.feasycom.feasybeacon.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Widget.AboutUsDialog;
import com.feasycom.feasybeacon.Widget.QRDialog;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */


public class AboutActivity extends BaseActivity {
    @BindView(R.id.more)
    TextView more;
    @BindView(R.id.qr)
    LinearLayout qr;
    @BindView(R.id.Search_Button)
    ImageView SearchButton;
    @BindView(R.id.Set_Button)
    ImageView SetButton;
    @BindView(R.id.About_Button)
    ImageView AboutButton;
    @BindView(R.id.aboutUsTV)
    TextView aboutUsTV;
    @BindView(R.id.ver)
    TextView ver;
    @BindView(R.id.Sensor_Button)
    ImageView SensorButton;
    @BindView(R.id.header_title)
    TextView headerTitle;

    private Activity activity;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        activity = this;
        ButterKnife.bind(activity);
        aboutUsTV.setText(Html.fromHtml("<p><b>Feasycom</b> focus on the researching and developing of IoT (internet of things) products, including Bluetooth Modules ,WiFi and LoRa Modules,Bluetooth Beacon,etc. With more than 10-year experiences in the wireless connectivity, which ensure us have the capability for providing low-risk product development, reducing system integration cost and shortening product customization cycle to thousands of diverse customer worldwide.</p>\n" +
                "\n" +
                "<p>&nbsp</p>\n" +
                "<p>Feasycom’s engineering and design services include:</p>\n" +
                "\n" +
                "<p>&nbsp SDK</p>\n" +
                "<p>&nbsp APP Support</p>\n" +
                "<p>&nbsp PCB Design</p>\n" +
                "<p>&nbsp Development Board</p>\n" +
                "<p>&nbsp Firmware Development</p>\n" +
                "<p>&nbsp Depth Customization</p>\n" +
                "<p>&nbsp Certification Request</p>\n" +
                "<p>&nbsp Turn-Key Production Testing & Manufacturing</p>\n" +
                "<p>&nbsp</p>\n" +
                "<p>Our products and services mainly apply to Automotive, Point of Sale, Home Automation, Healthcare and Engineering, Banking, Computing, Vending Business, Location, Lighting and more.</p>\n" +
                "<p>&nbsp</p>\n" +
                "<p>Aiming at <b><i>&quot Make Communication Easy and Freely &quot</b></i>, Feasycom is dedicated to design and develop high-quality products, efficient services to customers, for today, and all days to come.</p>\n"
        ));
        ver.setText(packageCode(this));
        ((TextView) findViewById(R.id.privacyAgreement)).setOnClickListener(new MyOnClickListener());
        ((TextView) findViewById(R.id.userAgreement)).setOnClickListener(new MyOnClickListener());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initView() {

    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.privacyAgreement:{
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",2);
                    Intent intent = new Intent();
                    intent.setClass(AboutActivity.this, AgreementActivity.class);
                    /*将Bundle对象assign给Intent*/
                    intent.putExtras(bundle);
                    /*跳转Activity Second*/
                    startActivityForResult(intent,0);
                    break;
                }
                case R.id.userAgreement:{
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    Intent intent = new Intent();
                    intent.setClass(AboutActivity.this, AgreementActivity.class);
                    /*将Bundle对象assign给Intent*/
                    intent.putExtras(bundle);
                    /*跳转Activity Second*/
                    startActivityForResult(intent,0);
                    break;
                }
            }
        }
    }

    public static String packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        String code = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }


    @Override
    public void refreshHeader() {
        headerTitle.setText(R.string.about);
    }

    public void refreshFooter() {
        //footer image src init
        SetButton.setImageResource(R.drawable.setting_off);
        AboutButton.setImageResource(R.drawable.about_on);
        SearchButton.setImageResource(R.drawable.search_off);
        SensorButton.setImageResource(R.drawable.sensor_off);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // SetActivity.actionStart(activity);
            finishAllActivity();
        }
        return true;
    }

    @OnClick(R.id.qr)
    public void qrClick() {
        new QRDialog(activity).show();
    }

    @OnClick(R.id.Set_Button)
    @Override
    public void setClick() {
        SetActivity.actionStart(activity);
        activity.finish();
    }

    @OnClick(R.id.About_Button)
    @Override
    public void aboutClick() {
    }

    @OnClick(R.id.Search_Button)
    @Override
    public void searchClick() {
        MainActivity.actionStart(activity);
        activity.finish();
    }


    @OnClick(R.id.Sensor_Button)
    @Override
    public void sensorClick() {
        SensorActivity.actionStart(activity);
        activity.finish();
    }

    @OnClick(R.id.more)
    public void onViewClicked() {
        new AboutUsDialog(activity).show();
    }

}
