package com.feasycom.feasybeacon.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.feasycom.bean.BeaconBean;
import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.bean.FeasyBeacon;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.controler.FscBeaconCallbacksImp;
import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.feasybeacon.Adapter.SettingBeaconParameterListAdapter;
import com.feasycom.feasybeacon.BeaconView.IntervalSpinnerView;
import com.feasycom.feasybeacon.BeaconView.GsensorSpinnerView;
import com.feasycom.feasybeacon.BeaconView.KeycfgSpinnerView;
import com.feasycom.feasybeacon.BeaconView.LableButtonView;
import com.feasycom.feasybeacon.BeaconView.LableEditView;
import com.feasycom.feasybeacon.BeaconView.LableSpinnerView;
import com.feasycom.feasybeacon.Bean.BaseEvent;
import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Controler.FscBeaconCallbacksImpParameter;
import com.feasycom.feasybeacon.Utils.ViewUtil;
import com.feasycom.feasybeacon.Widget.InfoDialog;
import com.feasycom.feasybeacon.Widget.OTADetermineDialog;
import com.feasycom.feasybeacon.Widget.ToggleButton;
import com.feasycom.util.FeasyBeaconUtil;
import com.feasycom.util.FeasycomUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.feasycom.feasybeacon.Activity.SetActivity.OPEN_TEST_MODE;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class ParameterSettingActivity extends BaseActivity{
    // do not  injected here otherwise listViewHeader can not use annotation injection
    public ListView parameterlistview;
    View listViewHeader;
    View listViewFooter;
    @BindView(R.id.TxPower)
    LableSpinnerView TxPower;
    @BindView(R.id.TxPowerDivider)
    View TxPowerDivider;
    @BindView(R.id.adv_interval)
    IntervalSpinnerView adv_interval;
    @BindView(R.id.adv_gsensor)
    GsensorSpinnerView adv_gsensor;
    @BindView(R.id.adv_keycfg)
    KeycfgSpinnerView adv_keycfg;

    // @BindView(R.id.AdvIntervalDivider)
    //View AdvIntervalDivider;
    @BindView(R.id.ConnectableDivider)
    View ConnectableDivider;
    @BindView(R.id.PIN)
    LableEditView PIN;
    @BindView(R.id.PIN_divider)
    View PINDivider;
    @BindView(R.id.add_LL)
    LinearLayout addLL;
    @BindView(R.id.header_left)
    TextView headerLeft;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right)
    TextView headerRight;

    /*
    @BindView(R.id.Search_Button)
    ImageView SearchButton;
    @BindView(R.id.Set_Button)
    ImageView SetButton;
    @BindView(R.id.About_Button)
    ImageView AboutButton;
    */
    @BindView(R.id.Module)
    LableEditView Module;
    @BindView(R.id.Version)
    LableEditView Version;
    @BindView(R.id.Name)
    LableEditView Name;
    @BindView(R.id.Interval)
    LableEditView Interval;
    @BindView(R.id.Gsensor)
    LableEditView Gsensor;
    @BindView(R.id.Keycfg)
    LableEditView Keycfg;
    @BindView(R.id.Connectable)
    LableButtonView Connectable;
    @BindView(R.id.ExtEnd)
    LableEditView ExtEnd;

    private int CHECK_CONNECT_TIME = 50000;
    private List<String> advIntervalList;
    private List<String> advin;
    private List<String> duration;
    private List<String> txPowerlist;
    private ArrayAdapter<String> intervalSpinnerAdapter;
    private ArrayAdapter<String> gsensorAdvinSpinnerAdapter;
    private ArrayAdapter<String> gsensorDurationSpinnerAdapter;
    private ArrayAdapter<String> keycfgAdvinSpinnerAdapter;
    private ArrayAdapter<String> keycfgDurationSpinnerAdapter;
    public static final int REQUEST_BEACON_ADD = 1;
    private FscBeaconApi fscBeaconApi;
    private BluetoothDeviceWrapper device;
    private Handler handler = new Handler();
    private SettingBeaconParameterListAdapter adapter;
    private Activity activity;
    private String versionString;
    private String moduleString;
    private String encryptWay;
    InfoDialog connectDialog;
    OTADetermineDialog otaDetermineDialog;
    //for BLE password
    String pin2Connect;
    public static boolean firstEnter = true;
    public static int TOTAL_COUNT = 0;
    public static int SUCESSFUL_COUNT = 0;
    public static boolean isModule_BP109 = false;
    public static boolean isModule_BP101 = false;
    public static boolean isModule_BP671 = false;
    public static boolean isModule_BP108 = false;
    public static boolean isModule_BP120 = false;
    public static boolean isKeycfg = false;
    public static boolean interval = true;
    public static boolean gsensor = false;
    public static boolean keycfg = false;
    public static boolean buzzer = false;
    public static boolean led = false;
    private FscBleCentralApi fscBleCentralApi = null;
    public static boolean back = false;


    public static void actionStart(Context context, BluetoothDeviceWrapper device, String pin) {
        Intent intent = new Intent(context, ParameterSettingActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("device", (Serializable) device);
        mBundle.putSerializable("pin", pin);
        intent.putExtras(mBundle);
        context.startActivity(intent);
        firstEnter = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back = false;
        setContentView(R.layout.activity_parameter_configuration);
        activity = this;
        EventBus.getDefault().register(this);
        device = (BluetoothDeviceWrapper) getIntent().getSerializableExtra("device");
        pin2Connect = (String) getIntent().getSerializableExtra("pin");

        connectDialog = new InfoDialog(activity, "connecting...");
        otaDetermineDialog = new OTADetermineDialog(activity);
        parameterlistview = (ListView) findViewById(R.id.parameter_listview);
        adapter = new SettingBeaconParameterListAdapter(activity, getLayoutInflater(), device.getFeasyBeacon());
        adapterInit(adapter);
        listViewHeader = (View) getLayoutInflater().inflate(R.layout.setting_parameter_header, null, false);
        listViewFooter = (View) getLayoutInflater().inflate(R.layout.setting_parameter_footer, null, false);
        parameterlistview.addHeaderView(listViewHeader);
        parameterlistview.addFooterView(listViewFooter, null, false);

        /**
         * inject here or listViewHeader can not use annotation injection
         */
        ButterKnife.bind(this);

        if (null != device.getFeasyBeacon()) {
            encryptWay = device.getFeasyBeacon().getEncryptionWay();

            moduleString = device.getFeasyBeacon().getModule();

            checkLength(encryptWay, device.getFeasyBeacon());

            advin = Arrays.asList(getResources().getStringArray(R.array.advin));
            duration = Arrays.asList(getResources().getStringArray(R.array.duration));


            if (device.getFeasyBeacon().getGsensor() || device.getFeasyBeacon().getKeycfg()) {
                advIntervalList = Arrays.asList(getResources().getStringArray(R.array.interval_table_1));
            } else {
                advIntervalList = Arrays.asList(getResources().getStringArray(R.array.interval_table));
            }

            intervalSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, advIntervalList);
            adv_interval.spinnerInit(intervalSpinnerAdapter, advIntervalList, device.getFeasyBeacon());
            if (device.getFeasyBeacon().getKeycfg()) {
                adv_keycfg.setVisibility(View.VISIBLE);
                keycfgAdvinSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, advin);
                keycfgDurationSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, duration);
                adv_keycfg.spinnerAdvin(keycfgAdvinSpinnerAdapter, advin);
                adv_keycfg.spinnerDuration(keycfgDurationSpinnerAdapter, duration);
            }
            if (device.getFeasyBeacon().getGsensor()) {
                adv_gsensor.setVisibility(View.VISIBLE);
                gsensorAdvinSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, advin);
                gsensorDurationSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, duration);
                adv_gsensor.spinnerAdvin(gsensorAdvinSpinnerAdapter, advin);
                adv_gsensor.spinnerDuration(gsensorDurationSpinnerAdapter, duration);
            }

            /**
             * We will be compatible with many modules by moduleString and versionString
             * firmware version
             */
            versionString = device.getFeasyBeacon().getVersion();
            /**
             * TxPower bind with BP103 BP104 BP106 BP101 BP671
             */
            if ("26".equals(moduleString) || "27".equals(moduleString) || "28".equals(moduleString) || "29".equals(moduleString)) {
                isModule_BP109 = false;
                isModule_BP101 = false;
                isModule_BP671 = false;
                isModule_BP108 = false;
                isModule_BP120 = false;
                TxPower.setVisibility(View.VISIBLE);
                TxPowerDivider.setVisibility(View.VISIBLE);
                txPowerlist = Arrays.asList(getResources().getStringArray(R.array.txpower_table));
                intervalSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
            } else if ("25".equals(moduleString)) {//BP109
                isModule_BP109 = true;
                isModule_BP101 = false;
                isModule_BP671 = false;
                isModule_BP108 = false;
                isModule_BP120 = false;
                TxPower.setVisibility(View.GONE);
                TxPowerDivider.setVisibility(View.GONE);
            } else if ("30".equals(moduleString)) {//BP101
                isModule_BP109 = false;
                isModule_BP101 = true;
                isModule_BP671 = false;
                isModule_BP108 = false;
                isModule_BP120 = false;
                TxPower.setVisibility(View.VISIBLE);
                TxPowerDivider.setVisibility(View.VISIBLE);
                txPowerlist = Arrays.asList(getResources().getStringArray(R.array.BP101_txpower_table));
                intervalSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
            } else if ("31".equals(moduleString)) {//BP671
                isModule_BP109 = false;
                isModule_BP101 = false;
                isModule_BP671 = true;
                isModule_BP108 = false;
                isModule_BP120 = false;
                TxPower.setVisibility(View.VISIBLE);
                TxPowerDivider.setVisibility(View.VISIBLE);
                txPowerlist = Arrays.asList(getResources().getStringArray(R.array.BP671_txpower_table));
                intervalSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
            } else if ("39".equals(moduleString)) {//BP108
                isModule_BP109 = false;
                isModule_BP101 = false;
                isModule_BP671 = false;
                isModule_BP108 = true;
                isModule_BP120 = false;
                TxPower.setVisibility(View.VISIBLE);
                TxPowerDivider.setVisibility(View.VISIBLE);
                txPowerlist = Arrays.asList(getResources().getStringArray(R.array.BP108_txpower_table));
                intervalSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
            } else if("36".equals(moduleString)) {      // BP120
                isModule_BP109 = false;
                isModule_BP101 = false;
                isModule_BP671 = false;
                isModule_BP108 = false;
                isModule_BP120 = true;
                TxPower.setVisibility(View.VISIBLE);
                TxPowerDivider.setVisibility(View.VISIBLE);
                txPowerlist = Arrays.asList(getResources().getStringArray(R.array.BP108_txpower_table));
                intervalSpinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
            }
            if (("unknow").equals(moduleString) || versionString.length() != 3) {
                Connectable.setCheck(true);
            } else {
                /**
                 * connectable button does not appear if BLE password is used
                 */
                if (FeasyBeacon.BLE_KEY_WAY.equals(encryptWay.substring(1))) {
                    Connectable.setVisibility(View.GONE);
                    ConnectableDivider.setVisibility(View.GONE);
                } else {
                    Connectable.setVisibility(View.VISIBLE);
                    ConnectableDivider.setVisibility(View.VISIBLE);
                }
                Connectable.setCheck(device.getFeasyBeacon().isConnectable());
            }

            if (FeasyBeacon.BLE_KEY_WAY.equals(encryptWay.substring(1))) {
                PIN.setVisibility(View.VISIBLE);
                PINDivider.setVisibility(View.VISIBLE);
            } else {
                PIN.setVisibility(View.GONE);
                PINDivider.setVisibility(View.GONE);
            }
        } else {
            // Log.e("ParameterSetting", "为null");
        }
    }

    private static final String TAG = "ParameterSettingActivit";

    public static void checkLength(String str, FeasyBeacon fb) {
        String s = FeasycomUtil.checkLength(Integer.toBinaryString(Integer.valueOf(str.substring(0, 1), 16)), 4, true);
        char[] c = s.toCharArray();
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    if (c[i] == '0') {
                        fb.setKeycfg(false);
                        isKeycfg = false;
                    } else {
                        isKeycfg = true;
                        fb.setKeycfg(true);
                        // fb.setKeycfg(false);
                    }
                    break;
                case 1:
                    if (c[i] == '0') {
                        fb.setGsensor(false);
                    } else {
                        fb.setGsensor(true);
                    }
                    break;
                case 2:
                    if (c[i] == '0') {
                        fb.setBuzzer(false);
                    } else {
                        fb.setBuzzer(true);
                    }
                    break;
                case 3:
                    if (c[i] == '0') {
                        fb.setLed(false);
                    } else {
                        fb.setLed(true);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fscBeaconApi = FscBeaconApiImp.getInstance(activity);
        fscBeaconApi.initialize();

        Name.setTextWacher(new ViewUtil.NameTextWatcher(Name, fscBeaconApi));
        Interval.setTextWacher(new ViewUtil.IntervalTextWatcher(Interval, fscBeaconApi));
        PIN.setTextWacher(new ViewUtil.PinTextWatcher(PIN, fscBeaconApi));
        ExtEnd.setTextWacher(new ViewUtil.ExtendTextWatcher(ExtEnd, fscBeaconApi));
        Gsensor.setTextWacher(new ViewUtil.GsensorTextWatcher(Gsensor, fscBeaconApi));
        Keycfg.setTextWacher(new ViewUtil.KeyTextWatcher(Keycfg, fscBeaconApi));
        initView();
        if (OPEN_TEST_MODE) {
            connectAndGetInfomation();
        } else {
            if (!fscBeaconApi.isConnected()) {
                if (null == device.getFeasyBeacon()
                        || FeasyBeaconUtil.updateDetermine(device.getFeasyBeacon().getVersion(), device.getFeasyBeacon().getModule())) {
                    otaDetermineDialog.show();
                } else {
                    connectAndGetInfomation();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        fscBeaconApi.disconnect();
        handler.removeCallbacks(checkConnect);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void initView() {
        parameterlistview.setAdapter(adapter);
        Connectable.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                fscBeaconApi.setConnectable(on);
            }
        });
        fscBeaconApi.setCallbacks(new FscBeaconCallbacksImpParameter(new WeakReference<ParameterSettingActivity>((ParameterSettingActivity) activity), fscBeaconApi, moduleString, device.getFeasyBeacon()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            fscBeaconApi.disconnect();
            // SetActivity.actionStart(activity);
            finishActivity();
        }
        return true;
    }

    @Override
    public void refreshHeader() {
        headerTitle.setText(getResources().getString(R.string.parameter_setting_title));
        headerLeft.setText(getResources().getString(R.string.back));
        headerRight.setText(getResources().getString(R.string.save));
    }

    @Override
    public void refreshFooter() {
        /**
         * footer image src init
         */
        /*
        SetButton.setImageResource(R.drawable.setting_on);
        AboutButton.setImageResource(R.drawable.about_off);
        SearchButton.setImageResource(R.drawable.search_off);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddBeaconActivity.REQUEST_BEACON_ADD_OK) {
            BeaconBean beaconBean = (BeaconBean) data.getSerializableExtra("beaconBean");
            fscBeaconApi.addBeaconInfo(beaconBean);
            if (fscBeaconApi.isBeaconInfoFull()) {
                addBeaconEnable(false);
            } else {
                addBeaconEnable(true);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Subscribe
    public void onEventMainThread(BaseEvent event) {
        switch (event.getEventId()) {
            case BaseEvent.DELE_BEACON_EVENT:
                fscBeaconApi.deleteBeaconInfo(event.getIndex());
                adapter.notifyDataSetChanged();
                addBeaconEnable(true);
                break;
            case BaseEvent.OTA_EVENT_YES:
                UpgradeActivity.actionStart(activity, device, pin2Connect);
                finishActivity();
                break;
            case BaseEvent.OTA_EVENT_NO:
                connectAndGetInfomation();
                break;
        }
    }

    @OnClick(R.id.add_IV)
    public void addBeacon() {
        Intent intent = new Intent(activity, AddBeaconActivity.class);
        startActivityForResult(intent, REQUEST_BEACON_ADD);
    }

    //@OnClick(R.id.Set_Button)
    public void setClick() {
    }

    //@OnClick(R.id.About_Button)
    public void aboutClick() {
        fscBeaconApi.disconnect();
        AboutActivity.actionStart(activity);
        finishActivity();
    }

    //@OnClick(R.id.Search_Button)
    public void searchClick() {
        fscBeaconApi.disconnect();
        MainActivity.actionStart(activity);
        finishActivity();
    }

    @Override
    public void sensorClick() {
        fscBeaconApi.disconnect();
        SensorActivity.actionStart(activity);
        finishActivity();
    }

    @OnClick(R.id.header_left)
    public void goBack() {
        back = true;
        fscBeaconApi.disconnect();
       /* Log.e(TAG, "goBack: " );
        SetActivity.actionStart(activity);
        finishActivity();*/
    }

    @OnClick(R.id.header_right)
    public void save() {
        if (!IntervalSpinnerView.verify) {
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Interval,Gsonser and Key cannot be \"Zero\" at the same time")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton(" cancel", new DialogInterface.OnClickListener() {//添加取消
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            alertDialog2.show();
        } else if (!(KeycfgSpinnerView.keycfgSend || GsensorSpinnerView.gsensorSend) && IntervalSpinnerView.position > 11) {
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Interval ,G-Sensor and Key , at least one value :0<X≤2\n")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton(" cancel", new DialogInterface.OnClickListener() {//添加取消
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            alertDialog2.show();
        } else {
            if (fscBeaconApi.isConnected()) {
                connectDialog.setInfo("save...");
                connectDialog.show();
                fscBeaconApi.saveBeaconInfo();
            } else {
                return;
            }
        }

    }

    FscBeaconCallbacksImp fscBeaconCallbacks = new FscBeaconCallbacksImp();

    Runnable checkConnect = new Runnable() {
        @Override
        public void run() {
            if (!fscBeaconApi.isConnected() && (("connecting...".equals(connectDialog.getInfo()))
                    || ("check password...".equals(connectDialog.getInfo())))) {
                fscBeaconApi.setCallbacks(null);
                connectDialog.setInfo("timeout");
                connectFailedHandler();
            }
        }
    };


    public void connectFailedHandler() {
        if(back){
            SetActivity.actionStart(activity);
            finishActivity();
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectDialog.dismiss();
                    fscBeaconApi.disconnect();
                    SetActivity.actionStart(activity);
                    finishActivity();
                }
            }, InfoDialog.INFO_DIAOLOG_SHOW_TIME);
        }
    }

    private void connectAndGetInfomation() {
        connectDialog.show();
        handler.postDelayed(checkConnect, CHECK_CONNECT_TIME);
        fscBeaconApi.connect(device, pin2Connect);
        TOTAL_COUNT++;
    }

    public void setConnectDialog() {
        connectDialog.show();
    }

    // enable to add beacon button
    public void addBeaconEnable(boolean enable) {
        if (enable) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addLL.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addLL.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * initialize 10 broadcast messages
     *
     * @param adapter
     */
    private void adapterInit(Adapter adapter) {
        SettingBeaconParameterListAdapter mAdapter = (SettingBeaconParameterListAdapter) adapter;
        for (int i = 0; i < fscBeaconApi.BEACON_AMOUNT; i++) {
            mAdapter.addBeacon(new BeaconBean(Integer.valueOf(i + 1).toString(), FeasyBeacon.BEACON_TYPE_NULL));
        }
    }

    public InfoDialog getConnectDialog() {
        return connectDialog;
    }

    public LableEditView getInterval() {
        return Interval;
    }

    public LableButtonView getConnectable() {
        return Connectable;
    }

    public LableEditView getExtEnd() {
        return ExtEnd;
    }

    public String getEncryptWay() {
        return encryptWay;
    }

    public LableEditView getPIN() {
        return PIN;
    }

    public IntervalSpinnerView getAdvInterval() {
        return adv_interval;
    }


    public LableSpinnerView getTxPower() {
        return TxPower;
    }

    public LableEditView getName() {
        return Name;
    }

    public LableEditView getVersion() {
        return Version;
    }

    public GsensorSpinnerView getGsensor() {
        return adv_gsensor;
    }

    public KeycfgSpinnerView getKeycfg() {
        return adv_keycfg;
    }

    public LableEditView getModule() {
        return Module;
    }

    public SettingBeaconParameterListAdapter getAdapter() {
        return adapter;
    }

    public Handler getHandler() {
        return handler;
    }

    public Runnable getCheckConnect() {
        return checkConnect;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getPin2Connect() {
        return pin2Connect;
    }

    public ListView getParameterlistview() {
        return parameterlistview;
    }

    public FscBeaconApi getFscBeaconApi() {
        return fscBeaconApi;
    }



}
