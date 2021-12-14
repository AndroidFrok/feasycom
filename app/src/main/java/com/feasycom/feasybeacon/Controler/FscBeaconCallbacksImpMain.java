package com.feasycom.feasybeacon.Controler;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBeaconCallbacksImp;
import com.feasycom.feasybeacon.Activity.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class FscBeaconCallbacksImpMain extends FscBeaconCallbacksImp  {
    private WeakReference<MainActivity> weakReference;
    private static final String TAG = "Main";

    public FscBeaconCallbacksImpMain(WeakReference<MainActivity> weakReference) {
        this.weakReference = weakReference;
    }

    @Override
    public void blePeripheralFound(BluetoothDeviceWrapper device, int rssi, byte[] record) {
        /**
         * BLE search speed is fast,please pay attention to the life cycle of the device object ,directly use the final type here
         */
        if ((null != device.getgBeacon()) || (null != device.getiBeacon()) || (null != device.getAltBeacon())) {
            if ((weakReference.get()!=null)&&(weakReference.get().getDeviceQueue().size() < 350)) {
                weakReference.get().getDeviceQueue().offer(device);
            }
        }
    }
}
