package com.feasycom.feasybeacon.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.bean.FeasyBeacon;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.feasybeacon.Adapter.SettingAdapter;
import com.feasycom.feasybeacon.Bean.BaseEvent;
import com.feasycom.feasybeacon.Controler.FscBeaconCallbacksImpSet;
import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Widget.PinDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.feasycom.feasybeacon.Activity.ParameterSettingActivity.SUCESSFUL_COUNT;
import static com.feasycom.feasybeacon.Activity.ParameterSettingActivity.TOTAL_COUNT;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class SetActivity extends BaseActivity{
    @BindView(R.id.header_left)
    TextView headerLeft;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right)
    TextView headerRight;
    @BindView(R.id.devicesList)
    RecyclerView devicesList;
    @BindView(R.id.refreshableView)
    SmartRefreshLayout refreshableView;
    @BindView(R.id.Search_Button)
    ImageView SearchButton;
    @BindView(R.id.Set_Button)
    ImageView SetButton;
    @BindView(R.id.About_Button)
    ImageView AboutButton;
    @BindView(R.id.Sensor_Button)
    ImageView SensorButton;
    private SettingAdapter devicesAdapter;
    private FscBeaconApi fscBeaconApi;
    private Activity activity;
    private static final int ENABLE_BT_REQUEST_ID = 1;
    private PinDialog pinDialog;
    private Handler handler = new Handler();

    private Timer timerUI;
    private TimerTask timerTask;
    public static final boolean OPEN_TEST_MODE = false;
    public static final boolean SCAN_FIXED_TIME = false;
    Queue<BluetoothDeviceWrapper> deviceQueue = new LinkedList<BluetoothDeviceWrapper>();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        activity = this;
        ButterKnife.bind(this);
        fscBeaconApi = FscBeaconApiImp.getInstance(activity);
        fscBeaconApi.initialize();
        initView();
        devicesAdapter = new SettingAdapter();
        devicesAdapter.setMOnItemClickListener(position -> {
            BluetoothDeviceWrapper deviceDetail = (BluetoothDeviceWrapper) devicesAdapter.getItem(position);
            if (null != deviceDetail.getFeasyBeacon() && null != deviceDetail.getFeasyBeacon() && FeasyBeacon.BLE_KEY_WAY.equals(deviceDetail.getFeasyBeacon().getEncryptionWay().substring(1))) {
                pinDialog.show();
                pinDialog.setPosition(position);
            } else {
                fscBeaconApi.stopScan();
                ParameterSettingActivity.actionStart(activity, (BluetoothDeviceWrapper) devicesAdapter.getItem(position), null);
                finishActivity();
            }
            return null;
        });
        devicesList.setLayoutManager(new LinearLayoutManager(this));
        devicesList.setAdapter(devicesAdapter);
        pinDialog = new PinDialog(activity);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * registered EventBus
         */
        EventBus.getDefault().register(this);
        fscBeaconApi.setCallbacks(new FscBeaconCallbacksImpSet(new WeakReference<SetActivity>((SetActivity) activity)));
        /*if (OPEN_TEST_MODE) {
            fscBeaconApi.startScan(25000);
        } else {
            fscBeaconApi.startScan(0);
        }*/
        Log.e(TAG, "onResume: 开始扫描" + fscBeaconApi.startScan() );
        timerUI = new Timer();
        timerTask = new UITimerTask(new WeakReference<SetActivity>((SetActivity) activity));
        timerUI.schedule(timerTask, 100, 100);
    }

    private static final String TAG = "SetActivity";
   @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // MainActivity.actionStart(this);
            finishAllActivity();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timerUI != null) {
            timerUI.cancel();
            timerUI = null;
        }
        fscBeaconApi.stopScan();
        EventBus.getDefault().unregister(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * user didn't want to turn on BT
         */
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                btDisabled();
                return;
            }
        }
    }

    @Override
    public void initView() {
        refreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                fscBeaconApi.stopScan();
                if(!fscBeaconApi.startScan()){
                    Log.e(TAG, "onRefresh: 扫描失败" );
                    deviceQueue.clear();
                    deviceQueue.addAll(devicesAdapter.getMDevices());
                    devicesAdapter.clearList();
                    devicesAdapter.myNotifyDataSetChanged();
                }else{
                    Log.e(TAG, "onRefresh: 扫描成功" );
                    // deviceQueue.clear();
                    devicesAdapter.clearList();
                    devicesAdapter.myNotifyDataSetChanged();
                }
                /*deviceQueue.clear();
                deviceQueue.addAll(devicesAdapter.getMDevices());
                devicesAdapter.clearList();
                devicesAdapter.myNotifyDataSetChanged();
                fscBeaconApi.stopScan();
                fscBeaconApi.startScan(6000);*/
                refreshableView.closeHeaderOrFooter();
            }
        });
    }


    @Subscribe
    public void onEventMainThread(BaseEvent event) {
        switch (event.getEventId()) {
            case BaseEvent.PIN_EVENT:
                int position = (int) event.getObject("position");
                String pin = (String) event.getObject("pin");
                fscBeaconApi.stopScan();
                ParameterSettingActivity.actionStart(activity, (BluetoothDeviceWrapper) devicesAdapter.getItem(position), pin);
                // finishActivity();
                break;
        }
    }

    @Override
    public void refreshHeader() {
        if (OPEN_TEST_MODE) {
            headerTitle.setText(" total " + TOTAL_COUNT + " successful " + SUCESSFUL_COUNT);
        } else {
            //headerTitle.setText(getResources().getString(R.string.app_name));
            headerTitle.setText(R.string.setting);
            //headerLeft.setText("   Sort");
            //headerRight.setText("Filter   ");
            headerLeft.setVisibility(View.GONE);
            headerRight.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.header_left)
    public void deviceSort(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                devicesAdapter.sort();
                devicesAdapter.myNotifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.header_right)
    public void deviceFilterClick() {

    }
    @Override
    public void refreshFooter() {
        /**
         * footer image src init
         */
        SetButton.setImageResource(R.drawable.setting_on);
        AboutButton.setImageResource(R.drawable.about_off);
        SearchButton.setImageResource(R.drawable.search_off);
        SensorButton.setImageResource(R.drawable.sensor_off);
    }

   /* @OnItemClick(R.id.devicesList)
    public void deviceItemClick(int position) {
        BluetoothDeviceWrapper deviceDetail = (BluetoothDeviceWrapper) devicesAdapter.getItem(position);
        if (null != deviceDetail.getFeasyBeacon() && null != deviceDetail.getFeasyBeacon() && FeasyBeacon.BLE_KEY_WAY.equals(deviceDetail.getFeasyBeacon().getEncryptionWay().substring(1))) {
            pinDialog.show();
            pinDialog.setPosition(position);
        } else {
            fscBeaconApi.stopScan();
            ParameterSettingActivity.actionStart(activity, (BluetoothDeviceWrapper) devicesAdapter.getItem(position), null);
            finishActivity();
        }
    }*/

    /**
     * search button binding event
     */
    @OnClick(R.id.Search_Button)
    public void searchClick() {
        fscBeaconApi.stopScan();
        MainActivity.actionStart(activity);
        // finishActivity();
    }

    @OnClick(R.id.Sensor_Button)
    public void sensorClick() {
        fscBeaconApi.stopScan();
        SensorActivity.actionStart(activity);
        // finishActivity();
    }

    /**
     * about button binding events
     */
    @OnClick(R.id.About_Button)
    public void aboutClick() {
        fscBeaconApi.stopScan();
        AboutActivity.actionStart(activity);
        // finishActivity();
    }

    /**
     * set the button binding event
     */
    @OnClick(R.id.Set_Button)
    public void setClick() {
//        fscBeaconApi.startScan(15000);
        //fscBeaconApi.startScan(0);
    }


    /**
     * bluetooth is not turned on
     */
    private void btDisabled() {
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finishActivity();
    }

    /**
     * does not support BLE
     */
    private void bleMissing() {
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finishActivity();
    }


    class UITimerTask extends TimerTask {
        private WeakReference<SetActivity> activityWeakReference;

        public UITimerTask(WeakReference<SetActivity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void run() {
            activityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityWeakReference.get().getDevicesAdapter().addDevice(activityWeakReference.get().getDeviceQueue().poll());
                    activityWeakReference.get().getDevicesAdapter().myNotifyDataSetChanged();
                }
            });
        }
    }

    public Queue<BluetoothDeviceWrapper> getDeviceQueue() {
        return deviceQueue;
    }

    public SettingAdapter getDevicesAdapter() {
        return devicesAdapter;
    }

    public FscBeaconApi getFscBeaconApi() {
        return fscBeaconApi;
    }

    public Handler getHandler() {
        return handler;
    }
}
