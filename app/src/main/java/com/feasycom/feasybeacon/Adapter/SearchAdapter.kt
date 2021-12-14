package com.feasycom.feasybeacon.Adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.feasybeacon.R
import com.feasycom.feasybeacon.Utils.SettingConfigUtil
import com.feasycom.feasybeacon.Widget.TipsDialog
import kotlinx.android.synthetic.main.search_beacon_info.view.*
import java.util.*

class SearchAdapter(val context: Context) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    val mDevices = ArrayList<BluetoothDeviceWrapper>()

    private var tipsDialog: TipsDialog? = null

    var mOnItemClickListener: ((position: Int)-> Unit)? = null

    private var isClick: Boolean = false;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.search_beacon_info, parent,false))
    }

    override fun getItemCount(): Int {
        return mDevices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceDetail = mDevices[position]
        val deviceName = deviceDetail.name
        val completeName = deviceDetail.completeLocalName
        val deviceAdd = deviceDetail.address
        val deviceModel = deviceDetail.model
        var deviceRssi = deviceDetail.rssi.toInt()
        if (completeName != null && completeName.isNotEmpty()) {
            //设备名长度限制，最大10
            /*if (completeName.length() > 10) {
                completeName = completeName.substring(0, 10);
            }*/
            holder.itemView.tv_name.text = completeName
        } else if (deviceName != null && deviceName.isNotEmpty()) {
            //设备名长度限制，最大10
            /*if (deviceName.length() > 10) {
                deviceName = deviceName.substring(0, 10);
            }*/
            holder.itemView.tv_name.text = deviceName
        } else {
            holder.itemView.tv_name.text = "unknow"
        }
        if (deviceAdd != null && deviceAdd.isNotEmpty()) {
            holder.itemView.tv_addr.text = " ($deviceAdd)"
        } else {
            holder.itemView.tv_addr.text = " (unknow)"
        }

        //iBeacon

        //iBeacon
        if (null != deviceDetail.getiBeacon()) {
            holder.itemView.iBeacon_item_view.setIBeaconValue(deviceDetail.getiBeacon())
            holder.itemView.iBeacon_item_view.visibility = View.VISIBLE
            holder.itemView.beacon_pic.setImageResource(R.drawable.ibeacon)
        } else {
            holder.itemView.iBeacon_item_view.setIBeaconValue(null)
            holder.itemView.iBeacon_item_view.visibility = View.GONE
        }

        //gBeacon

        //gBeacon
        if (null != deviceDetail.getgBeacon()) {
            holder.itemView.gBeacon_item_view.setGBeaconValue(deviceDetail.getgBeacon())
            holder.itemView.gBeacon_item_view.visibility = View.VISIBLE
            if ("URL" == deviceDetail.getgBeacon().frameTypeString) {
                holder.itemView.beacon_pic.setImageResource(R.drawable.url)
            } else if ("UID" == deviceDetail.getgBeacon().frameTypeString) {
                holder.itemView.beacon_pic.setImageResource(R.drawable.uid)
            }
        } else {
            holder.itemView.gBeacon_item_view.setGBeaconValue(null)
            holder.itemView.gBeacon_item_view.visibility = View.GONE
        }

        //AltBeacon

        //AltBeacon
        if (null != deviceDetail.altBeacon) {
            holder.itemView.altBeacon_item_view.setAltBeaconValue(deviceDetail.altBeacon)
            holder.itemView.altBeacon_item_view.visibility = View.VISIBLE
            holder.itemView.beacon_pic.setImageResource(R.drawable.altbeacon)
        } else {
            holder.itemView.altBeacon_item_view.setAltBeaconValue(null)
            holder.itemView.altBeacon_item_view.visibility = View.GONE
        }
        if (deviceRssi <= -100) {
            deviceRssi = -100
        } else if (deviceRssi > 0) {
            deviceRssi = 0
        }
        holder.itemView.pb_rssi.progress = 100 + deviceRssi
        holder.itemView.tv_rssi.text = "RSSI:" + deviceDetail.rssi.toString()

        holder.itemView.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->{
                    isClick = true;
                    Log.i(TAG, "Item: ACTION_DOWN");
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isClick = false;
                    Log.i(TAG, "Item: ACTION_UP");
                }
            }
            false}


        holder.itemView.setOnClickListener {
            mOnItemClickListener?.invoke(position)
        }
    }


    fun addDevice(deviceDetail: BluetoothDeviceWrapper?) {
        if (deviceDetail == null) {
            return;
        }
        var i = 0
        while (i < mDevices.size) {
            if (deviceDetail.address == mDevices[i].address) {
                mDevices[i].completeLocalName = deviceDetail.completeLocalName
                mDevices[i].name = deviceDetail.name
                mDevices[i].rssi = deviceDetail.rssi
                if (null != deviceDetail.getiBeacon()) {
                    if (deviceDetail.advData == mDevices[i].advData) {
                        Log.i("iBeacon", Integer.toString(i))
                        return;
                    }
                }
                if (null != deviceDetail.getgBeacon()) {
                    if (deviceDetail.advData == mDevices[i].advData) {
                        return;
                    }
                }
                if (null != deviceDetail.altBeacon) {
                    if (deviceDetail.advData == mDevices[i].advData) {
                        return;
                    }
                }
            }
            i++
        }
        Log.i("count", Integer.toString(i))
        if (i >= mDevices.size) {
            mDevices.add(deviceDetail)
        }

        //process the filter event.

        //process the filter event.
        if (SettingConfigUtil.getData(context, "filter_switch", false) as Boolean) {
            if (mDevices[i].rssi < SettingConfigUtil.getData(context, "filter_value", -100) as Int - 100) {
                mDevices.removeAt(i)
            }
        }

        if (mDevices.size == 300) {
            tipsDialog = TipsDialog(context)
            tipsDialog!!.setInfo("many device were found,please pull down!")
            tipsDialog!!.show()
        }
        return;
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
        const val TAG = "SerchAdapter"
    }
}