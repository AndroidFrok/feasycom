package com.feasycom.feasyblue.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.controler.FscBleCentralApiImp;
import com.feasycom.controler.FscSppApi;
import com.feasycom.controler.FscSppApiImp;
import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.util.Uitls;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.header_left_image)
    ImageView headerLeftImage;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.communication_button)
    ImageView communicationButton;
    @BindView(R.id.communication_button_text)
    TextView communicationButtonText;
    @BindView(R.id.setting_button)
    ImageView settingButton;
    @BindView(R.id.setting_button_text)
    TextView settingButtonText;
    @BindView(R.id.store_button)
    ImageView storeButton;
    @BindView(R.id.store_button_text)
    TextView storeButtonText;
    @BindView(R.id.about_button)
    ImageView aboutButton;
    @BindView(R.id.about_button_text)
    TextView aboutButtonText;
    @BindView(R.id.privacy)
    TextView privacy;
    @BindView(R.id.user)
    TextView user;


    private Activity activity;
    private FscBleCentralApi fscBleCentralApi;
    private FscSppApi fscSppApi;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void refreshFooter() {
        communicationButton.setImageResource(R.drawable.communication_off);
        settingButton.setImageResource(R.drawable.setting_off);
        storeButton.setImageResource(R.drawable.store_off);
        aboutButton.setImageResource(R.drawable.about_on);

        communicationButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        settingButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        storeButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        aboutButtonText.setTextColor(getResources().getColor(R.color.footer_on_text_color));
    }

    @Override
    public void refreshHeader() {
        headerLeftImage.setVisibility(View.INVISIBLE);
        headerTitle.setText(getResources().getString(R.string.about));
    }

    @Override
    public void initView() {
//        version.setText(getResources().getString(R.string.appName) + " " + getResources().getString(R.string.appVersion));
        version.setText(getResources().getString(R.string.appName) + " " + Uitls.getVersionName());

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get user input and set it to result
                Bundle bundle = new Bundle();
                bundle.putInt("type",2);
                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, AgreementActivity.class);
                // 将Bundle对象assign给Intent
                intent.putExtras(bundle);
                // 跳转Activity Second
                startActivityForResult(intent,0);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("type",1);
                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, AgreementActivity.class);
                // 将Bundle对象assign给Intent
                intent.putExtras(bundle);
                // 跳转Activity Second
                startActivityForResult(intent,0);

            }
        });
    }

    @Override
    public void loadData() {
        activity = this;
        /**
         * anonymous inner class will hold the outer class object of activity
         * it is recommended to clear the object here
         */
        fscBleCentralApi= FscBleCentralApiImp.getInstance(activity);
        fscBleCentralApi.setCallbacks(null);

        fscSppApi= FscSppApiImp.getInstance(activity);
        fscSppApi.setCallbacks(null);
        ((TextView) findViewById(R.id.privacyAgreement)).setOnClickListener(new MyOnClickListener());
        ((TextView) findViewById(R.id.userAgreement)).setOnClickListener(new MyOnClickListener());
    }

    @OnClick(R.id.qr)
    public void qrCode() {
        QRCodeActivity.actionStart(activity);
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

}
