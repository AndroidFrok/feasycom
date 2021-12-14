package com.feasycom.feasybeacon.Service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.controler.FscBeaconCallbacksImp;

public class BluetoothService extends Service {

    public static FscBeaconApiImp fscBeaconApi;

    public void initFeasyBeacon(){
        fscBeaconApi = FscBeaconApiImp.getInstance((Activity) getApplicationContext());
        fscBeaconApi.initialize();
    }

    public void setCallbacks(FscBeaconCallbacksImp fscBeaconCallbacksImp){
        fscBeaconApi.setCallbacks(fscBeaconCallbacksImp);
    }

    public void startScan(int time){
        fscBeaconApi.startScan(time);
    }

    public void stopScan(){
        fscBeaconApi.stopScan();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }



    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    class MyBluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // disconnect();
        }
    }


}
