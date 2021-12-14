package com.feasycom.feasybeacon.Adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.feasybeacon.R
import kotlinx.android.synthetic.main.setting_device_info.view.*
import java.util.*

class SettingAdapter : RecyclerView.Adapter<SettingAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    val mDevices = ArrayList<BluetoothDeviceWrapper>()

    var mOnItemClickListener: ((position: Int)-> Unit)? = null

    private var isClick: Boolean = false;

    override fun getItemCount(): Int {
        return mDevices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceDetail = mDevices[position]
        val deviceName = deviceDetail.name
        val completeName = deviceDetail.completeLocalName
        val deviceAdd = deviceDetail.address
        var deviceRssi = deviceDetail.rssi.toInt()
        if (completeName != null && completeName.length > 0) {
            holder.itemView.tv_name.text = completeName + "-" + deviceAdd.substring(9, 11) + deviceAdd.substring(12, 14) + deviceAdd.substring(15, 17)
        } else if (deviceName != null && deviceName.length > 0) {
            holder.itemView.tv_name.text = deviceName
        } else {
            holder.itemView.tv_name.text = "unknow name"
        }
        if (deviceAdd != null && deviceAdd.length > 0) {
            holder.itemView.tv_addr.text = deviceAdd
        } else {
            holder.itemView.tv_addr.text = "unknow address"
        }

        if (deviceRssi <= -100) {
            deviceRssi = -100
        } else if (deviceRssi > 0) {
            deviceRssi = 0
        } else {
        }
        var str_rssi = "($deviceRssi)"
        if (str_rssi == "(-100)") {
            str_rssi = "null"
        }
        holder.itemView.pb_rssi.progress = 100 + deviceRssi
        holder.itemView.tv_rssi.text = "RSSI:" + deviceDetail.rssi.toString()
        if (null != deviceDetail.feasyBeacon) {
            if (null == deviceDetail.feasyBeacon.battery) {
                holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity_charging)
                holder.itemView.charge_value.text = "100%"
            } else {
                val battry = Integer.valueOf(deviceDetail.feasyBeacon.battery).toInt()
                if (battry > 100) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity_charging)
                    holder.itemView.charge_value.text = "100%"
                } else if (battry == 0) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity0)
                    holder.itemView.charge_value.text = "0%"
                } else if (battry > 0 && battry < 10) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity10)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 10 && battry < 20) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity20)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 20 && battry < 30) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity30)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 30 && battry < 40) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity40)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 40 && battry < 50) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity50)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 50 && battry < 60) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity60)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 60 && battry < 70) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity70)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 70 && battry < 80) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity80)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 80 && battry < 90) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity90)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                } else if (battry >= 90 && battry <= 100) {
                    holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity100)
                    holder.itemView.charge_value.text = Integer.toString(battry) + "%"
                }
            }
        } else {
            holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity_charging)
            holder.itemView.charge_value.text = "100%"
        }

        holder.itemView.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->{
                    isClick = true;
                    Log.i(TAG, "Item: ACTION_DOWN");
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isClick = false;
                }
            }
            false}

        holder.itemView.setOnClickListener {
            mOnItemClickListener?.invoke(position)
        }
    }

    fun myNotifyDataSetChanged(){
        if(!isClick){
            notifyDataSetChanged()
        }
    }

    fun addDevice(deviceDetail: BluetoothDeviceWrapper?) {
        if (deviceDetail == null) {
            return
        } else {
            if (null != deviceDetail.feasyBeacon) {
                if (("21" == deviceDetail.feasyBeacon.module
                                || "25" == deviceDetail.feasyBeacon.module
                                || "26" == deviceDetail.feasyBeacon.module
                                || "27" == deviceDetail.feasyBeacon.module
                                || "28" == deviceDetail.feasyBeacon.module
                                || "29" == deviceDetail.feasyBeacon.module
                                || "30" == deviceDetail.feasyBeacon.module
                                || "34" == deviceDetail.feasyBeacon.module
                                || "35" == deviceDetail.feasyBeacon.module
                                || "31" == deviceDetail.feasyBeacon.module
                                || "36" == deviceDetail.feasyBeacon.module
                                || "39" == deviceDetail.feasyBeacon.module)
                        && deviceDetail.rssi >= -80) {
                    var i = 0
                    while (i < mDevices.size) {

                        if (deviceDetail.address == mDevices.get(i).getAddress()) {
                            mDevices.get(i).setCompleteLocalName(deviceDetail.completeLocalName)
                            mDevices.get(i).setName(deviceDetail.name)
                            mDevices.get(i).setRssi(deviceDetail.rssi)
                            if (null != deviceDetail.getiBeacon()) {
                                mDevices.get(i).setiBeacon(deviceDetail.getiBeacon())
                            } else {
                                mDevices.get(i).setiBeacon(null)
                            }
                            if (null != deviceDetail.getgBeacon()) {
                                mDevices.get(i).setgBeacon(deviceDetail.getgBeacon())
                            } else {
                                mDevices.get(i).setgBeacon(null)
                            }
                            if (null != deviceDetail.feasyBeacon) {
                                mDevices.get(i).setFeasyBeacon(deviceDetail.feasyBeacon)
                            } else {
                                mDevices.get(i).setFeasyBeacon(null)
                            }
                            break
                        }
                        i++
                    }
                    if (i >= mDevices.size) {
                        mDevices.add(deviceDetail)
                    }
                }
            }
        }
    }

    fun clearList(){
        mDevices.clear()
    }

    fun sort() {
        for (i in 0 until mDevices.size - 1) {
            for (j in 0 until mDevices.size - 1 - i) {
                if (mDevices[j].rssi < mDevices[j + 1].rssi) {
                    val bd = mDevices[j]
                    mDevices[j] = mDevices[j + 1]
                    mDevices[j + 1] = bd
                }
            }
        }
    }

    fun getItem(position: Int): BluetoothDeviceWrapper {
        return mDevices[position]
    }

    companion object{
        const val TAG = "SettingAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.setting_device_info, parent, false))
    }
}