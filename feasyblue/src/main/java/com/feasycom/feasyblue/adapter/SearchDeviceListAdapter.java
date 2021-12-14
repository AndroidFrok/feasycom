package com.feasycom.feasyblue.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.feasyblue.App;
import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.util.SettingConfigUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchDeviceListAdapter extends BaseAdapter {
    private static final String TAG = "SearchDeviceListAdapter";
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<BluetoothDeviceWrapper> mDevices = new ArrayList<BluetoothDeviceWrapper>();
    private Boolean isclick = false;
    private Handler handler = new Handler(Looper.myLooper());

    public SearchDeviceListAdapter(Context context, LayoutInflater Inflator) {
        super();
        filterRssiSwitch = (boolean) SettingConfigUtil.getData(App.getContext(), "filter_rssi_switch", false);
        filterRssi = (int) SettingConfigUtil.getData(App.getContext(), "filter_value", -100);
        filterNameSwitch = (boolean) SettingConfigUtil.getData(App.getContext(), "filter_name_switch", false);
        filterName = (String) SettingConfigUtil.getData(App.getContext(), "filter_name", "");
        mContext = context;
        mInflator = Inflator;
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        BluetoothDeviceWrapper bluetoothDeviceWrapper;
        bluetoothDeviceWrapper = mDevices.get(position);
        return bluetoothDeviceWrapper;
    }

    boolean filterRssiSwitch;
    boolean filterNameSwitch;
    int filterRssi;
    String filterName = "fsc";

    public void addDevice(BluetoothDeviceWrapper deviceDetail) {
        if (null == deviceDetail) {
            return;
        }

        if (filterNameSwitch) {
            if (deviceDetail.getName() == null || !deviceDetail.getName().contains(filterName)) {
                return;
            }
        }

        if (deviceDetail.getName() == null || !deviceDetail.getName().contains("FSC")) {
            return;
        }
        if (filterRssiSwitch) {
            if (deviceDetail.getRssi() < (filterRssi - 100)) {
                return;
            }
        }
        int i = 0;
        for (; i < mDevices.size(); i++) {
            if (deviceDetail.getAddress().equals(mDevices.get(i).getAddress())) {
                mDevices.get(i).setName(deviceDetail.getName());
                mDevices.get(i).setRssi(deviceDetail.getRssi());
                mDevices.get(i).setAdvData(deviceDetail.getAdvData());
                break;
            }
        }
        if (i >= mDevices.size()) {
            if (isclick) {
                handler.postDelayed(() -> {
                    mDevices.add(deviceDetail);
                    notifyDataSetChanged();
                }, 100);
            } else {
                mDevices.add(deviceDetail);
                notifyDataSetChanged();
            }
        }
    }

    public void sort() {
        for (int i = 0; i < mDevices.size() - 1; i++) {
            for (int j = 0; j < mDevices.size() - 1 - i; j++) {
                if (mDevices.get(j).getRssi() < mDevices.get(j + 1).getRssi() && mDevices.get(j).getBondState() != BluetoothDevice.BOND_BONDED && mDevices.get(j + 1).getBondState() != BluetoothDevice.BOND_BONDED) {
                    BluetoothDeviceWrapper bd = mDevices.get(j);
                    mDevices.set(j, mDevices.get(j + 1));
                    mDevices.set(j + 1, bd);
                }
            }
        }
    }

    public void clearList() {
        mDevices.clear();
    }

    public ArrayList<BluetoothDeviceWrapper> getmDevices() {
        return mDevices;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.search_device_info, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        BluetoothDeviceWrapper deviceDetail = mDevices.get(position);
        String deviceName = deviceDetail.getName();
        String deviceAdd = deviceDetail.getAddress();
        int deviceRssi = deviceDetail.getRssi().intValue();
        if (deviceName != null && deviceName.length() > 0) {
            //设备名长度限制，最大30
            if (deviceName.length() >= 30) {
                deviceName = deviceName.substring(0, 30);
            }
            if (deviceDetail.getBondState() == BluetoothDevice.BOND_BONDED) {
                viewHolder.tvName.setText(mContext.getResources().getString(R.string.paired) + deviceName);
            } else {
                viewHolder.tvName.setText(deviceName);
            }
        } else {
            viewHolder.tvName.setText("unknow");
        }
        if (deviceAdd != null && deviceAdd.length() > 0) {
            viewHolder.tvAddr.setText(" (" + deviceAdd + ")");
        } else {
            viewHolder.tvAddr.setText(" (unknow)");
        }
        if (deviceRssi <= -100) {
            deviceRssi = -100;
        } else if (deviceRssi > 0) {
            deviceRssi = 0;
        }
        String str_rssi = "(" + deviceRssi + ")";
        if (str_rssi.equals("(-100)")) {
            str_rssi = "null";
        }
        viewHolder.deviceMode.setText(deviceDetail.getModel());
        viewHolder.pbRssi.setProgress(100 + deviceRssi);
        viewHolder.tvRssi.setText(mContext.getResources().getString(R.string.rssi) + "(" + deviceDetail.getRssi().toString() + ")");
        viewHolder.deviceView.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isclick = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    isclick = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    isclick = false;
                    break;
            }
            return false;
        });
        return view;
    }


    static class ViewHolder {
        @BindView(R.id.tv_rssi)
        TextView tvRssi;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_addr)
        TextView tvAddr;
        @BindView(R.id.device_mode)
        TextView deviceMode;
        @BindView(R.id.pb_rssi)
        ProgressBar pbRssi;
        @BindView(R.id.device_view)
        LinearLayout deviceView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
