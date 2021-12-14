package com.feasycom.feasybeacon.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.feasybeacon.Adapter.SensorAdapter;
import com.feasycom.feasybeacon.Bean.BaseEvent;
import com.feasycom.feasybeacon.Controler.FscBeaconCallbacksImpSensor;
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


public class SensorActivity extends BaseActivity{
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
    private SensorAdapter devicesAdapter;
    private FscBeaconApi fscBeaconApi;
    private Activity activity;
    private static final int ENABLE_BT_REQUEST_ID = 1;
    private PinDialog pinDialog;
    private Handler handler = new Handler();

    private Timer timerUI;
    private TimerTask timerTask;
    Queue<BluetoothDeviceWrapper> deviceQueue = new LinkedList<BluetoothDeviceWrapper>();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SensorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        activity = this;
        ButterKnife.bind(this);
        fscBeaconApi = FscBeaconApiImp.getInstance(activity);
        fscBeaconApi.initialize();

        initView();
        devicesAdapter = new SensorAdapter();
        devicesAdapter.setMOnItemClickListener(position -> {
            return null;
        });
        devicesList.setLayoutManager(new LinearLayoutManager(this));
        devicesList.setAdapter(devicesAdapter);
        /**
         * remove the dividing line
         */
        // devicesList.setDividerHeight(0);
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

        fscBeaconApi.setCallbacks(new FscBeaconCallbacksImpSensor(new WeakReference<SensorActivity>((SensorActivity) activity)));

        fscBeaconApi.startScan();
        timerUI = new Timer();
        timerTask = new SensorActivity.UITimerTask(new WeakReference<SensorActivity>((SensorActivity) activity));
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
                    deviceQueue.clear();
                    deviceQueue.addAll(devicesAdapter.getMDevices());
                    devicesAdapter.clearList();
                    devicesAdapter.myNotifyDataSetChanged();
                }else{
                    deviceQueue.clear();
                    devicesAdapter.clearList();
                    devicesAdapter.myNotifyDataSetChanged();
                }
                //fscBeaconApi.startScan(0);
                refreshableView.closeHeaderOrFooter();
            }
        });
    }


    @Subscribe
    public void onEventMainThread(BaseEvent event) {
        switch (event.getEventId()) {
            /*case BaseEvent.PIN_EVENT:
                int position = (int) event.getObject("position");
                String pin = (String) event.getObject("pin");
                fscBeaconApi.stopScan();
                ParameterSettingActivity.actionStart(activity, (BluetoothDeviceWrapper) devicesAdapter.getItem(position), pin);
                finishActivity();
                break;*/
        }
    }

    @Override
    public void refreshHeader() {
            //headerTitle.setText(getResources().getString(R.string.app_name));
            headerTitle.setText("Sensor");
            //headerLeft.setText("   Sort");
            //headerRight.setText("Filter   ");
            headerLeft.setVisibility(View.GONE);
            headerRight.setVisibility(View.GONE);
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
        SetButton.setImageResource(R.drawable.setting_off);
        AboutButton.setImageResource(R.drawable.about_off);
        SearchButton.setImageResource(R.drawable.search_off);
        SensorButton.setImageResource(R.drawable.sensor_on);
    }


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
        fscBeaconApi.stopScan();
        SetActivity.actionStart(activity);
        // finishActivity();
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
        private WeakReference<SensorActivity> activityWeakReference;

        public UITimerTask(WeakReference<SensorActivity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void run() {
            activityWeakReference.get().runOnUiThread(()->{
                activityWeakReference.get().getDevicesAdapter().addDevice(activityWeakReference.get().getDeviceQueue().poll());
                activityWeakReference.get().getDevicesAdapter().myNotifyDataSetChanged();
            });
        }
    }

    public Queue<BluetoothDeviceWrapper> getDeviceQueue() {
        return deviceQueue;
    }

    public SensorAdapter getDevicesAdapter() {
        return devicesAdapter;
    }

    public FscBeaconApi getFscBeaconApi() {
        return fscBeaconApi;
    }

    public Handler getHandler() {
        return handler;
    }
}
