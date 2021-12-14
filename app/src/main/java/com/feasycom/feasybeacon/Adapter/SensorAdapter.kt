package com.feasycom.feasybeacon.Adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.feasybeacon.R
import kotlinx.android.synthetic.main.sensor_device_info.view.*
import java.util.*

class SensorAdapter : RecyclerView.Adapter<SensorAdapter.ViewHolder>(){

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
        val temperature = deviceDetail.monitor.temperature
        holder.itemView.temperature.text = "$temperature℃"
        val humidity = deviceDetail.monitor.humidity
        holder.itemView.humidity.text = "$humidity%"
        var deviceRssi = deviceDetail.rssi.toInt()
        if (completeName != null && completeName.isNotEmpty()) {
            holder.itemView.tv_name.text = completeName + "-" + deviceAdd.substring(9, 11) + deviceAdd.substring(12, 14) + deviceAdd.substring(15, 17)
        } else if (deviceName != null && deviceName.isNotEmpty()) {
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
        holder.itemView.tv_rssi.text = "RSSI:" + deviceDetail.rssi.toString() + "dB"
        if (null != deviceDetail.feasyBeacon) {
            if (null == deviceDetail.feasyBeacon.battery) {
                holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity_charging)
                holder.itemView.charge_value.text = "100%"
            } else {
                val battry = Integer.valueOf(deviceDetail.feasyBeacon.battery).toInt()
                holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity)
                holder.itemView.charge_value.text = Integer.toString(battry) + "%"
            }
        } else {
            holder.itemView.charge_pic.setImageResource(R.drawable.electric_quantity_charging)
            holder.itemView.charge_value.text = "100%"
        }

        holder.itemView.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->{
                    isClick = true;
                    Log.i(SearchAdapter.TAG, "Item: ACTION_DOWN");
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isClick = false;
                    Log.e(SearchAdapter.TAG, "Item: ACTION_UP");
                }
            }
            false}


        holder.itemView.setOnClickListener {
            mOnItemClickListener?.invoke(position)
        }
    }

    fun addDevice(deviceDetail: BluetoothDeviceWrapper?) {
        if (deviceDetail == null) {
            return
        } else {
            var i = 0
            while (i < mDevices.size) {
                if (deviceDetail.address == mDevices[i].address) {
                    mDevices[i].completeLocalName = deviceDetail.completeLocalName
                    mDevices[i].name = deviceDetail.name
                    mDevices[i].rssi = deviceDetail.rssi
                    if (null != deviceDetail.monitor) {
                        mDevices[i].monitor = deviceDetail.monitor
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

    fun myNotifyDataSetChanged(){
        if(!isClick){
            notifyDataSetChanged()
        }
    }

    companion object{
        const val TAG = "SensorAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.sensor_device_info, parent,false))
    }

}