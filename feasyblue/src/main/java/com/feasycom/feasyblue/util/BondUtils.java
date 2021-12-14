package com.feasycom.feasyblue.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BondUtils {
    public static String TAG = "yxz";
    private String defaultPin = "1234";

    private void registerDiscoveryReceiver(Context mContext) {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mContext.registerReceiver(discoveryReceiver, filter);
        // XLog.d(mContext, TAG, "yxz at MainActivity.java registerDiscoveryReceiver() 注册广播接收者，接收发现设备消息的广播");
    }

    //与设备配对
    static public boolean createBond(Context mcontext, Class btClass, BluetoothDevice btDevice) throws Exception {
        // Log.d(mcontext, TAG, "-----------yxz at ClsUtils.java createBond() begin" + btDevice.getAddress() + "----------");
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        // Log.d(mcontext, TAG, "-----------yxz at ClsUtils.java createBond() end" + btDevice.getAddress() + "----------");
        return returnValue.booleanValue();
    }

    //与设备解除配对
    static public boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    static public boolean setPin(Class btClass, BluetoothDevice btDevice, String str) throws Exception {
        // XLog.d(TAG, "yxz at ClsUtils.java setPin() begin");
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
            // XLog.e(TAG, "返回值:" + returnValue);
        } catch (SecurityException e) {
            e.printStackTrace();
            // XLog.e(TAG, "SecurityException:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // XLog.e(TAG, "IllegalArgumentException:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // XLog.e(TAG, "Exception:" + e.getMessage());
        }
        // XLog.d(TAG, "yxz at ClsUtils.java setPin() end");
        return true;

    }

    // 取消用户输入
    static public boolean cancelPairingUserInput(Class btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    // 取消配对
    static public boolean cancelBondProcess(Class btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    //确认配对
    static public void setPairingConfirmation(Context mcontext, Class<?> btClass, BluetoothDevice device, boolean isConfirm) throws Exception {
        // XLog.d(mcontext, TAG, "yxz at ClsUtils.java setPairingConfirmation() begin");
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
        // XLog.d(mcontext, TAG, "yxz at ClsUtils.java setPairingConfirmation() end");
    }


    /**
     * @param clsShow
     */
    static public void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                Log.e("method name", hideMethod[i].getName() + ";and the i is:" + i);
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                Log.e("Field name", allFields[i].getName());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //自动配对的广播
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 2.终止有序广播
                abortBroadcast();// 如果没有将广播终止，则会出现一个一闪而过的配对框。
                // 3.调用setPin方法进行配对...
                try {
                    //该返回值不能准确的表示是否配对成功，测试发现该值返回true时，设备的绑定状态为正在绑定中，故将添加设备的操作放至监听绑定状态的广播中。
                    boolean ret = setPin(device.getClass(), device, defaultPin);
                    Log.d(TAG, "提示信息:" + "setPin()结果：" + ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        // XLog.d(TAG, "----------bounding......,mac" + device.getAddress() + "----------");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        // XLog.d(TAG, "----------bound success, mac:" + device.getAddress() + "----------");
                        break;

                    case BluetoothDevice.BOND_NONE:
                        // XLog.d(TAG, "----------bound cancel, mac:" + device.getAddress() + "----------");
                        break;
                    default:
                        break;

                }
            }
        }
    };
}
