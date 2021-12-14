package com.feasycom.feasyblue.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.controler.FscBleCentralApiImp;
import com.feasycom.controler.FscBleCentralCallbacksImp;
import com.feasycom.controler.FscSppApi;
import com.feasycom.controler.FscSppApiImp;
import com.feasycom.controler.FscSppCallbacksImp;
import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.adapter.SearchDeviceListAdapter;
import com.feasycom.feasyblue.util.ACache;
import com.feasycom.feasyblue.util.Uitls;
import com.feasycom.feasyblue.widget.RefreshableView;
import com.feasycom.util.LogUtil;
import com.feasycom.util.ToastUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.feasycom.feasyblue.activity.filterDeviceActivity.filterScene;

public class SearchDeviceActivity extends BaseActivity {
    private static final String TAG = "SearchDeviceActivity";
    private final int ENABLE_BT_REQUEST_ID = 2;
    public static final boolean AUTH_TEST = false;

    FscSppApi fscSppApi;
    FscBleCentralApi fscBleCentralApi;
    Activity activity;
    Queue<BluetoothDeviceWrapper> deviceQueue = new LinkedList<BluetoothDeviceWrapper>();
    @BindView(R.id.header_left)
    TextView headerLeft;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_title_msg)
    TextView headerTitleMsg;
    @BindView(R.id.header_right_TV)
    TextView headerRightTV;
    @BindView(R.id.header_right_LL)
    LinearLayout headerRightLL;
    @BindView(R.id.headerLinerLayout)
    LinearLayout headerLinerLayout;
    @BindView(R.id.devicesList)
    ListView devicesList;
    @BindView(R.id.refreshableView)
    RefreshableView refreshableView;
    @BindView(R.id.communication_button)
    ImageView communicationButton;
    @BindView(R.id.store_button)
    ImageView storeButton;
    @BindView(R.id.setting_button)
    ImageView settingButton;
    @BindView(R.id.about_button)
    ImageView aboutButton;
    @BindView(R.id.communication_button_text)
    TextView communicationButtonText;
    @BindView(R.id.store_button_text)
    TextView storeButtonText;
    @BindView(R.id.setting_button_text)
    TextView settingButtonText;
    @BindView(R.id.about_button_text)
    TextView aboutButtonText;
    @BindView(R.id.ble_check)
    CheckBox bleCheck;
    @BindView(R.id.spp_check)
    CheckBox sppCheck;
    @BindView(R.id.header_sort_button)
    ImageView headerSortButton;
    @BindView(R.id.header_filter_button)
    ImageView headerFilterButton;
    @BindView(R.id.header_sort_TV)
    TextView headerSortTV;
    @BindView(R.id.header_filter_TV)
    TextView headerFilterTV;
    @BindView(R.id.footer)
    LinearLayout footer;
    @BindView(R.id.header_left_image)
    ImageView headerLeftImage;



    public static String currentMode;
    public SearchDeviceListAdapter adapter;
    private Timer timerUI;
    private TimerTask taskUI;
    private boolean checkChange = false;
    private boolean testDeviceFound = false;
    private Handler handler=new Handler();

    private boolean isOta = false;

    public static void actionStart(Context context, boolean isOta) {
        Intent intent = new Intent(context, SearchDeviceActivity.class);
        intent.putExtra("IS_OTA",isOta);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_device;
    }

    @Override
    public void refreshFooter() {
        communicationButton.setImageResource(R.drawable.communication_on);
        storeButton.setImageResource(R.drawable.store_off);
        settingButton.setImageResource(R.drawable.setting_off);
        aboutButton.setImageResource(R.drawable.about_off);
        communicationButtonText.setTextColor(getResources().getColor(R.color.footer_on_text_color));
        storeButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        settingButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
        aboutButtonText.setTextColor(getResources().getColor(R.color.color_tb_text));
    }

    @Override
    public void refreshHeader() {
        if(isOta){
            headerTitle.setText(getResources().getString(R.string.OTAUpgrade));
            headerLeftImage.setImageResource(R.drawable.goback);
            headerRightLL.setVisibility(View.INVISIBLE);
            sppCheck.setChecked(true);

        }else{
            headerTitle.setText(getResources().getString(R.string.communication));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void initView() {
        Uitls.init(this);

        fscBleCentralApi = FscBleCentralApiImp.getInstance(activity);
        fscBleCentralApi.initialize();

        fscSppApi = FscSppApiImp.getInstance(activity);
        fscSppApi.initialize();
        isOta = getIntent().getBooleanExtra("IS_OTA",false);
        if(isOta){
            footer.setVisibility(View.GONE);
        }
        filterScene = 0;
        devicesList.setAdapter(adapter);
        refreshableView.setOnRefreshListener(()->{
            runOnUiThread(()->{
                deviceQueue.clear();
                adapter.clearList();
                adapter.notifyDataSetChanged();
                Log.e(TAG, "initView: 开始扫描1" );
                stopAllScan();
                startScan();
                refreshableView.finishRefreshing();
            });
        },0);
        // 检查是否首次打开
        ACache mCache = ACache.get(this);
        String isHaveOn = mCache.getAsString("haveOn");
        if (isHaveOn == null){
            mCache.put("haveOn", "haveOn");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    SearchDeviceActivity.this);
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
                                    intent.setClass(SearchDeviceActivity.this, AgreementActivity.class);
                                    // 将Bundle对象assign给Intent
                                    intent.putExtras(bundle);
                                    // 跳转Activity Second
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
                                    intent.setClass(SearchDeviceActivity.this, AgreementActivity.class);
                                    // 将Bundle对象assign给Intent
                                    intent.putExtras(bundle);
                                    // 跳转Activity Second
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
        }
    }




    @Override
    public void loadData() {
        activity = this;
        adapter = new SearchDeviceListAdapter(activity, getLayoutInflater());
    }


    @Override
    protected void onResume() {
        super.onResume();

        testDeviceFound = false;
        /**
         * enable or disable debug information
         */
        LogUtil.setDebug(true);
        /**
         * switch the context and then use the fscBleCentralApi or fscSppApi must be updated to the current context
         */
        /*fscBleCentralApi = FscBleCentralApiImp.getInstance(activity);
        fscBleCentralApi.initialize();

        fscSppApi = FscSppApiImp.getInstance(activity);
        fscSppApi.initialize();*/
        if ((fscSppApi != null) && (fscBleCentralApi != null)) {
            setCallBacks();
        }
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


                        startScan();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Log.e(TAG, "onDenied: 拒绝权限" );
                    }
                });

        /*timerUI = new Timer();
        taskUI = new UITimerTask(new WeakReference<SearchDeviceActivity>((SearchDeviceActivity) activity));
        timerUI.schedule(taskUI, 100, 250);*/
    }

    @OnClick(R.id.header_left_image)
    public void goBack() {
        activity.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            activity.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * fscSppApi.stopScan()  will  release the resources of broadcastReceiver
         */
        fscSppApi.stopScan();
    }

    @OnClick({R.id.header_sort_button,R.id.header_sort_TV})
    public void deviceSort(){
        runOnUiThread(() -> {
            adapter.sort();
            adapter.notifyDataSetChanged();
        });
    }

    public void deviceClear(){
        runOnUiThread(() -> {
            deviceQueue.clear();
            adapter.clearList();
            adapter.notifyDataSetChanged();
        });
    }

    @OnClick({R.id.header_filter_button,R.id.header_filter_TV})
    public void deviceFilterClick() {
        filterDeviceActivity.actionStart(activity);
        finishActivity();
    }

    public void startScan() {
        if (BluetoothDeviceWrapper.BLE_MODE.equals(currentMode)) {
            bleCheck.setChecked(true);
            if (fscBleCentralApi.isBtEnabled() == false) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            }
            if (!fscBleCentralApi.checkBleHardwareAvailable()) {
                ToastUtil.show(activity, "is not support ble");
            }
            fscSppApi.stopScan();
            fscBleCentralApi.startScan(30000);
        } else {
            sppCheck.setChecked(true);
            if (fscSppApi.isBtEnabled() == false) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            }
            /*if(fscBleCentralApi = null){
                Log.e(TAG, "startScan: null" );
            }*/
            fscBleCentralApi.stopScan();
            Log.e(TAG, "startScan: 开始扫描" );
            fscSppApi.startScan(30000);
        }
    }

    public void stopAllScan() {
        fscBleCentralApi.stopScan();
        fscSppApi.stopScan();
    }

    @OnCheckedChanged({R.id.spp_check, R.id.ble_check})
    public void modeCheck(CompoundButton v, boolean flag) {

        checkChange = true;
        switch (v.getId()) {
            case R.id.ble_check:
                if (flag) {
                    currentMode = BluetoothDeviceWrapper.BLE_MODE;
                }
                sppCheck.setChecked(!flag);
                break;
            case R.id.spp_check:
                if (flag) {
                    currentMode = BluetoothDeviceWrapper.SPP_MODE;
                }
                bleCheck.setChecked(!flag);
                break;
        }
        deviceClear();
        if (!flag){
            startScan();
        }
    }


    @OnItemClick(R.id.devicesList)
    public void deviceClick(int position) {
        stopAllScan();
        BluetoothDeviceWrapper bluetoothDeviceWrapper = (BluetoothDeviceWrapper) adapter.getItem(position);
        if(isOta){
            OtaActivity.actionStart(activity, bluetoothDeviceWrapper);
        }else{
            ThroughputActivity.actionStart(activity, bluetoothDeviceWrapper);
        }
    }

    public void uiHandlerStartScan() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(headerTitleMsg != null){
                    headerTitleMsg.setText(getResources().getString(R.string.searching));
                }
            }
        });
    }

    public void uiHandlerStopScan() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                adapter.sort();
//                adapter.myNotifyDataSetChanged();
                if(headerTitleMsg != null){
                    headerTitleMsg.setText(getResources().getString(R.string.searched));
                }
            }
        });
    }



    private void setCallBacks() {
        fscBleCentralApi.setCallbacks(new FscBleCentralCallbacksImp() {
            @Override
            public void startScan() {
                if (BluetoothDeviceWrapper.BLE_MODE.equals(currentMode)) {

                    uiHandlerStartScan();
                }
            }

            @Override
            public void stopScan() {
                if (BluetoothDeviceWrapper.BLE_MODE.equals(currentMode)) {
                    uiHandlerStopScan();
                }
            }

            @Override
            public void blePeripheralFound(BluetoothDeviceWrapper device, int rssi, byte[] record) {
                if (AUTH_TEST) {
                    if ("test".equals(device.getName())) {
                        stopAllScan();
                        if(!testDeviceFound){
                            testDeviceFound=true;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ThroughputActivity.actionStart(activity, device);
                                }
                            },2000);
                        }
                    }
                } else {
                    /**
                     * queue to add a device return true / false
                     * note that the life cycle of the device object, here with a queue cache
                     */
                    if(rssi == 127){
                        return;
                    }
                    if (BluetoothDeviceWrapper.BLE_MODE.equals(currentMode)) {
                        adapter.addDevice(device);
                        /*if (deviceQueue.size() < 10) {
                            deviceQueue.offer(device);
                        }*/
                    }
                }
            }
        });

        fscSppApi.setCallbacks(new FscSppCallbacksImp() {
            @Override
            public void sppDeviceFound(BluetoothDeviceWrapper device, int rssi) {
                if (AUTH_TEST) {
                    if ("laser".equals(device.getName())) {
                        stopAllScan();
                        ThroughputActivity.actionStart(activity, device);
                    }
                } else {
                    /**
                     * queue to add a device return true / false
                     * note that the life cycle of the device object, here with a queue cache
                     * fix bug :there will be a delay broadcast, the probability of BLE_MODE list will cause the case of SPP equipment
                     */
                    if (BluetoothDeviceWrapper.SPP_MODE.equals(currentMode)) {
                        adapter.addDevice(device);
                        /*if (deviceQueue.size() < 10) {
                            deviceQueue.offer(device);
                        }*/
                    }
                }
            }

            public void startScan() {
                if (BluetoothDeviceWrapper.SPP_MODE.equals(currentMode)) {
                    uiHandlerStartScan();
                }
            }


            public void stopScan() {
                if (BluetoothDeviceWrapper.SPP_MODE.equals(currentMode)) {
                    uiHandlerStopScan();
                }
            }
        });
    }

    class UITimerTask extends TimerTask {
        private WeakReference<SearchDeviceActivity> activityWeakReference;

        public UITimerTask(WeakReference<SearchDeviceActivity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void run() {
            activityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityWeakReference.get().getAdapter().addDevice(activityWeakReference.get().getDeviceQueue().poll());
                }
            });
        }
    }


    public SearchDeviceListAdapter getAdapter() {
        return adapter;
    }

    public Queue<BluetoothDeviceWrapper> getDeviceQueue() {
        return deviceQueue;
    }
}
