package com.feasycom.feasyblue.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.controler.FscBleCentralApiImp;
import com.feasycom.controler.FscSppApi;
import com.feasycom.controler.FscSppApiImp;
import com.feasycom.feasyblue.R;

import butterknife.BindView;
import butterknife.OnClick;

import static com.feasycom.feasyblue.activity.filterDeviceActivity.filterScene;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.header_left_image)
    ImageView headerLeftImage;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.parameterDefining)
    Button parameterDefining;
    @BindView(R.id.otaButton)
    Button otaButton;
    @BindView(R.id.shareButton)
    Button share;
    @BindView(R.id.communication_button)
    ImageView communicationButton;
    @BindView(R.id.communication_button_text)
    TextView communicationButtonText;
    @BindView(R.id.store_button)
    ImageView storeButton;
    @BindView(R.id.store_button_text)
    TextView storeButtonText;
    @BindView(R.id.setting_button)
    ImageView settingButton;
    @BindView(R.id.setting_button_text)
    TextView settingButtonText;
    @BindView(R.id.about_button)
    ImageView aboutButton;
    @BindView(R.id.about_button_text)
    TextView aboutButtonText;
    @BindView(R.id.header_right_text)
    TextView headerRightText;


    private Activity activity;
    private FscBleCentralApi fscBleCentralApi;
    private FscSppApi fscSppApi;
    private static final int REQUEST_CHOOSEFILE = 0;
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void refreshFooter() {
        communicationButton.setImageResource(R.drawable.communication_off);
        settingButton.setImageResource(R.drawable.setting_on);
        storeButton.setImageResource(R.drawable.store_off);
        aboutButton.setImageResource(R.drawable.about_off);

        communicationButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        settingButtonText.setTextColor(getResources().getColor(R.color.footer_on_text_color));
        storeButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        aboutButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
    }

    @Override
    public void refreshHeader() {
        headerLeftImage.setVisibility(View.GONE);
        headerRightText.setVisibility(View.GONE);
        headerTitle.setText(getString(R.string.settingTitle));
    }

    @Override
    public void initView() {

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
    }

    @OnClick(R.id.parameterDefining)
    public void parameterDefiningClick() {
        filterScene = 1;
        ParameterModificationActivity.actionStart(activity);
    }

    @OnClick(R.id.otaButton)
    public void otaButtonClick() {
        filterScene = 2;
        // OtaDeviceListActivity.actionStart(activity);
        SearchDeviceActivity.actionStart(activity,true);
    }

    @OnClick(R.id.quickConnection)
    public void quickConnection() {
        QuickConnectionActivity.actionStart(activity);
    }

    @OnClick(R.id.shareButton)
    public void share(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult: " + data.getData() );
        }
    }

}
