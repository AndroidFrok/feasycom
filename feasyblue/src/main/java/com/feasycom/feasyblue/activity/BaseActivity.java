package com.feasycom.feasyblue.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.controler.ActivityCollector;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

//import com.skyjing.toolsuitls.tools.Uitls;


public abstract class BaseActivity extends AppCompatActivity {
    private final String TAG="BaseActivity";
    public static boolean inputPassWordShow=true;
    private int layoutId;
    private Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate "+getClass().getSimpleName());
        /**
         * close the effect
         */
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        layoutId = getLayoutId();
        if (layoutId > 0)
            setContentView(getLayoutId());

        loadData();
        /**
         * bind annotation
         */
        unbinder=ButterKnife.bind(this);
        initView();

        refreshFooter();

        refreshHeader();
        Log.e(TAG, "onCreate: " +getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"onStart "+getClass().getSimpleName());
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG,"onStop "+getClass().getSimpleName());
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.i(TAG,"onResume "+getClass().getSimpleName());
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"onPause "+getClass().getSimpleName());
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy "+getClass().getSimpleName());
        unbinder.unbind();
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if ((layoutId == R.layout.activity_search_device)
                    || (layoutId == R.layout.activity_setting)
                    || (layoutId == R.layout.activity_about)) {
                ActivityCollector.finishAllActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected abstract int getLayoutId();

    public abstract void refreshFooter();

    public abstract void refreshHeader();

    public abstract void initView();

    public abstract void loadData();

    @Optional
    @OnClick({R.id.setting_button_LL, R.id.about_button_LL, R.id.communication_button_LL, R.id.store_button_LL})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_button_LL:
                AboutActivity.actionStart(this);
                finishActivity();
                break;
            case R.id.setting_button_LL:
                SettingActivity.actionStart(this);
                finishActivity();
                break;
            case R.id.communication_button_LL:
                SearchDeviceActivity.actionStart(this,false);
                finishActivity();
                break;
            case R.id.store_button_LL:
                StoreActivity.actionStart(this);
                finishActivity();
        }
    }
    public void finishActivity() {
        Log.i(TAG,"finishActivity "+getClass().getSimpleName());
        this.finish();
    }
    
}
