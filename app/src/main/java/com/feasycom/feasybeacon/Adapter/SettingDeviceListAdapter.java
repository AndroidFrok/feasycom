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

public class SettingDeviceListAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<BluetoothDeviceWrapper> mDevices = new ArrayList<BluetoothDeviceWrapper>();

    public SettingDeviceListAdapter(Context context, LayoutInflater Inflator) {
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

    private static final String TAG = "SettingDeviceListAdapte";
    synchronized public void addDevice(BluetoothDeviceWrapper deviceDetail) {
        if(deviceDetail == null){
            return;
        }
        else {
            if(null != deviceDetail.getFeasyBeacon()) {
                if(("21".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "25".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "26".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "27".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "28".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "29".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "30".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "34".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "35".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "31".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "36".equals(deviceDetail.getFeasyBeacon().getModule())
                        || "39".equals(deviceDetail.getFeasyBeacon().getModule()))
                        && (deviceDetail.getRssi() >= -80)){
                    int i = 0;
                    for (; i < mDevices.size(); i++) {
                        if (deviceDetail.getAddress().equals(mDevices.get(i).getAddress())) {
                            mDevices.get(i).setCompleteLocalName(deviceDetail.getCompleteLocalName());
                            mDevices.get(i).setName(deviceDetail.getName());
                            mDevices.get(i).setRssi(deviceDetail.getRssi());

                            if (null != deviceDetail.getiBeacon()) {
                                mDevices.get(i).setiBeacon(deviceDetail.getiBeacon());
                            } else {
                                mDevices.get(i).setiBeacon(null);
                            }
                            if (null != deviceDetail.getgBeacon()) {
                                mDevices.get(i).setgBeacon(deviceDetail.getgBeacon());
                            } else {
                                mDevices.get(i).setgBeacon(null);
                            }
                            if (null != deviceDetail.getFeasyBeacon()) {
                                mDevices.get(i).setFeasyBeacon(deviceDetail.getFeasyBeacon());
                            } else {
                                mDevices.get(i).setFeasyBeacon(null);
                            }

                            break;
                        }
                    }
                    if (i >= mDevices.size()) {
                        mDevices.add(deviceDetail);
                    }
                }
            }
        }
/*
        int i = 0;
        for (; i < mDevices.size(); i++) {
            if (deviceDetail.getAddress().equals(mDevices.get(i).getAddress())) {
                mDevices.get(i).setCompleteLocalName(deviceDetail.getCompleteLocalName());
                mDevices.get(i).setName(deviceDetail.getName());
                mDevices.get(i).setRssi(deviceDetail.getRssi());

                if (null != deviceDetail.getiBeacon()) {
                    mDevices.get(i).setiBeacon(deviceDetail.getiBeacon());
                } else {
                    mDevices.get(i).setiBeacon(null);
                }
                if (null != deviceDetail.getgBeacon()) {
                    mDevices.get(i).setgBeacon(deviceDetail.getgBeacon());
                } else {
                    mDevices.get(i).setgBeacon(null);
                }
                if (null != deviceDetail.getFeasyBeacon()) {
                    mDevices.get(i).setFeasyBeacon(deviceDetail.getFeasyBeacon());
                } else {
                    mDevices.get(i).setFeasyBeacon(null);
                }

                break;
            }
        }
        if (i >= mDevices.size()) {
            mDevices.add(deviceDetail);
        }
*/
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
            view = mInflator.inflate(R.layout.setting_device_info,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDeviceWrapper deviceDetail = mDevices.get(position);
        String deviceName = deviceDetail.getName();
        String completeName = deviceDetail.getCompleteLocalName();
        String deviceAdd = deviceDetail.getAddress();
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
        viewHolder.tvRssi.setText("RSSI:" + deviceDetail.getRssi().toString());
        /*
        if (null != deviceDetail.getFeasyBeacon()) {
            if (null == deviceDetail.getFeasyBeacon().getBattery()) {
                viewHolder.chargePic.setImageResource(R.drawable.charging);
            } else {
                int battry = Integer.valueOf(deviceDetail.getFeasyBeacon().getBattery()).intValue();
                if (battry > 100) {
                    viewHolder.chargePic.setImageResource(R.drawable.charging);
                } else if (battry >= 0 && battry < 20) {
                    viewHolder.chargePic.setImageResource(R.drawable.battery1);
                } else if (battry >= 20 && battry < 40) {
                    viewHolder.chargePic.setImageResource(R.drawable.battery2);
                } else if (battry >= 40 && battry < 60) {
                    viewHolder.chargePic.setImageResource(R.drawable.battery3);
                } else if (battry >= 60 && battry < 80) {
                    viewHolder.chargePic.setImageResource(R.drawable.battery4);
                } else if (battry >= 80 && battry <= 100) {
                    viewHolder.chargePic.setImageResource(R.drawable.battery5);
                }
            }

        } else {
            viewHolder.chargePic.setImageResource(R.drawable.charging);
        }
        */
        if (null != deviceDetail.getFeasyBeacon()) {
            if (null == deviceDetail.getFeasyBeacon().getBattery()) {
                viewHolder.chargePic.setImageResource(R.drawable.electric_quantity_charging);
                viewHolder.chargeValue.setText("100%");
            } else {
                int battry = Integer.valueOf(deviceDetail.getFeasyBeacon().getBattery()).intValue();
                if (battry > 100) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity_charging);
                    viewHolder.chargeValue.setText("100%");
                } else if (battry == 0){
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity0);
                    viewHolder.chargeValue.setText("0%");
                } else if (battry > 0 && battry < 10){
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity10);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 10 && battry < 20){
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity20);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 20 && battry < 30){
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity30);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 30 && battry < 40) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity40);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 40 && battry < 50) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity50);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 50 && battry < 60) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity60);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 60 && battry < 70) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity70);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 70 && battry < 80) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity80);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 80 && battry < 90) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity90);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                } else if (battry >= 90 && battry <= 100) {
                    viewHolder.chargePic.setImageResource(R.drawable.electric_quantity100);
                    viewHolder.chargeValue.setText(Integer.toString(battry)+"%");
                }
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
