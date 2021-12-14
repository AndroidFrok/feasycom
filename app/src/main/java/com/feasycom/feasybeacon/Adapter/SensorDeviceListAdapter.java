package com.feasycom.feasybeacon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.feasybeacon.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class SensorDeviceListAdapter extends BaseAdapter {
    private static final String TAG = "SensorDeviceListAdapter";
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<BluetoothDeviceWrapper> mDevices = new ArrayList<BluetoothDeviceWrapper>();

    public SensorDeviceListAdapter(Context context, LayoutInflater Inflator) {
        super();
        mContext = context;
        mInflator = Inflator;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDevices.get(position);
    }

    synchronized public void addDevice(BluetoothDeviceWrapper deviceDetail) {
        if(deviceDetail == null){
            return;
        }
        else {
            int i = 0;
            for (; i < mDevices.size(); i++) {
                if (deviceDetail.getAddress().equals(mDevices.get(i).getAddress())) {
                    mDevices.get(i).setCompleteLocalName(deviceDetail.getCompleteLocalName());
                    mDevices.get(i).setName(deviceDetail.getName());
                    mDevices.get(i).setRssi(deviceDetail.getRssi());
                    if (null != deviceDetail.getMonitor()) {
                        mDevices.get(i).setMonitor(deviceDetail.getMonitor());
                    }
                    break;
                }
            }
            if (i >= mDevices.size()) {
                mDevices.add(deviceDetail);
            }

        }
    }

    public void sort() {
        for (int i=0; i < mDevices.size() - 1; i++) {
            for (int j = 0; j < mDevices.size() - 1 - i; j++) {
                if(mDevices.get(j).getRssi() < mDevices.get(j + 1).getRssi()) {
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

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.sensor_device_info,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDeviceWrapper deviceDetail = mDevices.get(position);
        String deviceName = deviceDetail.getName();
        String completeName = deviceDetail.getCompleteLocalName();
        String deviceAdd = deviceDetail.getAddress();
        String temperature = deviceDetail.getMonitor().getTemperature();
        viewHolder.temperature.setText(temperature + "℃");
        String humidity = deviceDetail.getMonitor().getHumidity();
        viewHolder.humidity.setText(humidity + "%");
        int deviceRssi = deviceDetail.getRssi().intValue();
        if((completeName != null) && completeName.length()>0){
            viewHolder.tvName.setText(completeName+"-"+deviceAdd.substring(9,11)+deviceAdd.substring(12,14)+deviceAdd.substring(15,17));
        }else if ((deviceName != null) && deviceName.length() > 0) {
            viewHolder.tvName.setText(deviceName);
        } else {
            viewHolder.tvName.setText("unknow name");
        }
        if (deviceAdd != null && deviceAdd.length() > 0) {
            viewHolder.tvAddr.setText(deviceAdd);
        } else {
            viewHolder.tvAddr.setText("unknow address");
        }

        if (deviceRssi <= -100) {
            deviceRssi = -100;
        } else if (deviceRssi > 0) {
            deviceRssi = 0;
        } else {
        }
        String str_rssi = "(" + deviceRssi + ")";
        if (str_rssi.equals("(-100)")) {
            str_rssi = "null";
        }
        viewHolder.pbRssi.setProgress(100 + deviceRssi);
        viewHolder.tvRssi.setText("RSSI:" + deviceDetail.getRssi().toString() + "dB");
        if (null != deviceDetail.getFeasyBeacon()) {
            if (null == deviceDetail.getFeasyBeacon().getBattery()) {
                viewHolder.chargePic.setImageResource(R.drawable.electric_quantity_charging);
                viewHolder.chargeValue.setText("100%");
            } else {
                int battry = Integer.valueOf(deviceDetail.getFeasyBeacon().getBattery()).intValue();
                viewHolder.chargePic.setImageResource(R.drawable.electric_quantity);
                viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
            }

        } else {
            viewHolder.chargePic.setImageResource(R.drawable.electric_quantity_charging);
            viewHolder.chargeValue.setText("100%");
        }
        return view;
    }



   static class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.temperature)
        TextView temperature;
        @BindView(R.id.humidity)
        TextView humidity;
        @BindView(R.id.charge_pic)
        ImageView chargePic;
        @BindView(R.id.charge_value)
        TextView chargeValue;
        @BindView(R.id.tv_addr)
        TextView tvAddr;
        @BindView(R.id.tv_rssi)
        TextView tvRssi;
        @BindView(R.id.pb_rssi)
        ProgressBar pbRssi;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
