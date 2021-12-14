package com.feasycom.feasybeacon.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.feasybeacon.Adapter.SearchAdapter;
import com.feasycom.feasybeacon.Controler.FscBeaconCallbacksImpMain;
import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Utils.ACache;
import com.feasycom.util.LogUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class MainActivity extends BaseActivity {
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
    private SearchAdapter devicesAdapter;
    private FscBeaconApi fscBeaconApi;
    private Activity activity;
    private static final int ENABLE_BT_REQUEST_ID = 1;
    Queue<BluetoothDeviceWrapper> deviceQueue = new LinkedList<BluetoothDeviceWrapper>();
    private Timer timerUI;
    private TimerTask timerTask;
    private Handler  handler = new Handler();
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        ButterKnife.bind(this);
        fscBeaconApi = FscBeaconApiImp.getInstance(activity);
        fscBeaconApi.initialize();
        if (fscBeaconApi.checkBleHardwareAvailable() == false) {
            bleMissing();
        }
        if (fscBeaconApi.isBtEnabled() == false) {
            /**
             * BT is not turned on - ask user to make it enabled
             */
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            /**
             * see onActivityResult to check what is the status of our request
             */
        }
        /**
         *  on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
         */
        initpermission();
        initView();
        devicesList.setLayoutManager(new LinearLayoutManager(this));
        devicesAdapter = new SearchAdapter(getApplicationContext());
        devicesAdapter.setMOnItemClickListener(position -> {
            return null;
        });
        devicesList.setAdapter(devicesAdapter);
        /**
         * remove the dividing line
         */
        // devicesList.setDividerHeight(0);
        // 检查是否首次打开
        /*ACache mCache = ACache.get(this);
        String isHaveOn = mCache.getAsString("haveOn");
        if (isHaveOn == null){
            mCache.put("haveOn", "haveOn");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);
            alertDialogBuilder
                    .setCancelable(false)
                    .setTitle(R.string.userAndPrivacyAgreement)
                    .setMessage(R.string.userAndPrivacyAgreementDetail)
                    .setPositiveButton(getResources().getText(R.string.privacyAgreement),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("type",2);
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, AgreementActivity.class);
                                    *//*将Bundle对象assign给Intent*//*
                                    intent.putExtras(bundle);
                                    *//*跳转Activity Second*//*
                                    startActivityForResult(intent,0);

                                }
                            })
                    .setNegativeButton(getResources().getText(R.string.userAgreement),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("type",1);
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, AgreementActivity.class);
                                    *//*将Bundle对象assign给Intent*//*
                                    intent.putExtras(bundle);
                                    *//*跳转Activity Second*//*
                                    startActivityForResult(intent,0);

                                }
                            })
                    .setNeutralButton(getResources().getText(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });


// create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

// show it
            alertDialog.show();
        }*/
    }

    void initpermission(){
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
                                , Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setDeniedMessage(getResources().getString(R.string.denied_message))
                        .setDeniedCloseBtn(getResources().getString(R.string.denied_close))
                        .setDeniedSettingBtn(getResources().getString(R.string.denied_setting))
                        .setRationalMessage(getResources().getString(R.string.rational_message))
                        .setRationalBtn(getResources().getString(R.string.rational_btn))
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Log.e(TAG, "onGranted: 同意授权" );
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        // makeText(permissions.toString() + "权限拒绝");
                        Log.e(TAG, "onDenied: 拒绝权限" );
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.setDebug(true);
        fscBeaconApi.setCallbacks(new FscBeaconCallbacksImpMain(new WeakReference<>((MainActivity) activity)));
        /*if(SCAN_FIXED_TIME) {
            fscBeaconApi.startScan(60000);
        }else {
            fscBeaconApi.startScan(0);
        }*/
        fscBeaconApi.startScan();
        timerUI = new Timer();
        timerTask = new UITimerTask(new WeakReference<MainActivity>((MainActivity) activity));
        timerUI.schedule(timerTask, 100, 100);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
               /*deviceQueue.clear();
               deviceQueue.addAll(devicesAdapter.getMDevices());
               devicesAdapter.clearList();
               devicesAdapter.myNotifyDataSetChanged();
               fscBeaconApi.stopScan();
               Log.e(TAG, "run: 开始扫描" );
               fscBeaconApi.startScan(6000);*/
               //fscBeaconApi.startScan(0);
               refreshableView.closeHeaderOrFooter();
           }
       });



       /* refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceQueue.clear();
                        devicesAdapter.clearList();
                        devicesAdapter.notifyDataSetChanged();
                        fscBeaconApi.stopScan();
                        Log.e(TAG, "run: 开始扫描" );
                        fscBeaconApi.startScan(6000);
                        //fscBeaconApi.startScan(0);
                        refreshableView.finishRefreshing();
                    }
                });
            }
        }, 0);*/
    }

    /*@OnItemClick(R.id.devicesList)
    public void deviceItemClick(int position) {
//        Log.i("click", "main");
        BluetoothDeviceWrapper deviceWrapper = (BluetoothDeviceWrapper) devicesAdapter.getItem(position);
        EddystoneBeacon eddystoneBeacon = deviceWrapper.getgBeacon();
        try {
            Log.i("click", eddystoneBeacon.getUrl());
        } catch (Exception e) {
        }
        if ((null != eddystoneBeacon) && ("URL".equals(eddystoneBeacon.getFrameTypeString()))) {
            Uri uri = Uri.parse(eddystoneBeacon.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }*/

    @Override
    public void refreshHeader() {
        //headerTitle.setText(getResources().getString(R.string.app_name));
        headerTitle.setText("Beacon");
        //headerLeft.setText("Sort");
        //headerRight.setText("Filter");
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
        fscBeaconApi.stopScan();
        FilterDeviceActivity.actionStart(activity);
        finishActivity();
    }

    @Override
    public void refreshFooter() {
        /**
         * footer image src init
         */
        SetButton.setImageResource(R.drawable.setting_off);
        AboutButton.setImageResource(R.drawable.about_off);
        SensorButton.setImageResource(R.drawable.sensor_off);
        SearchButton.setImageResource(R.drawable.search_on);
    }


    /**
     * search button binding event
     */
    @OnClick(R.id.Search_Button)
    public void searchClick() {

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

    private static final String TAG = "MainActivity";
    /**
     * set the button binding event
     */
    @OnClick(R.id.Set_Button)
    public void setClick() {
        fscBeaconApi.stopScan();
        SetActivity.actionStart(activity);
        // Log.e(TAG, "setClick: ");
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
        private WeakReference<MainActivity> activityWeakReference;

        public UITimerTask(WeakReference<MainActivity> activityWeakReference) {
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

    public SearchAdapter getDevicesAdapter() {
        return devicesAdapter;
    }
}
