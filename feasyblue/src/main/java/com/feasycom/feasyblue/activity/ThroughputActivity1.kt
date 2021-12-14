package com.feasycom.feasyblue.activity

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.controler.*
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.dfileselector.activity.DefaultSelectorActivity
import com.feasycom.feasyblue.util.SettingConfigUtil
import kotlinx.android.synthetic.main.activity_throughput1.*
import kotlinx.android.synthetic.main.header_img_title_text_img.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.zip.CRC32

class ThroughputActivity1: AppCompatActivity(){

    private var sendCRC = CRC32() // 发送crc
    private var receiveCRC = CRC32() // 接收crc
    private var deviceDetail: BluetoothDeviceWrapper? = null
    private lateinit var fscBleCentralApi: FscBleCentralApi
    private lateinit var fscSppApi: FscSppApiImp

    var receiveBuffer = StringBuffer() // 接收数据缓存buffer string类型
    var receiveBufferHex = StringBuffer() // 接收数据缓存buffer hex string 类型
    lateinit var currentMode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throughput1)

        deviceDetail = (intent.getSerializableExtra("device") as BluetoothDeviceWrapper).apply {
            currentMode = model?:"BLE"
            Log.e(TAG, "onCreate: ${model}")
            if (BluetoothDeviceWrapper.SPP_MODE == currentMode){
                switchServiceButton.visibility = View.GONE
            }
            header_title.text = name
        }
        setCallBacks()

        header_title_msg.text = resources.getString(R.string.connecting)
        header_left_image.apply {
            setImageResource(R.drawable.goback)
            setOnClickListener {
                disconnectDevice()
                finish()
            }
        }

        setOnClickListener()
    }


    fun setOnClickListener(){
        sendEdit.addTextChangedListener {
            SettingConfigUtil.saveData(applicationContext, "editInfo", it.toString())
            editByteCount.text = "${it.toString().toByteArray(charset("UTF-8")).size}"
        }
        sendButton.setOnClickListener {send()}
        sendFileButton.setOnClickListener{
            // fscBleCentralApi.sendFile(204800)
            val intent = Intent(this@ThroughputActivity1, SelectFileActivity::class.java)
            startActivityForResult(intent, 1)
        }
        intervalSendCheck.setOnClickListener {
            if(intervalSendCheck.isOpened){
                Log.e(TAG, "onCreate: 打开")
                lifecycleScope.launch(Dispatchers.IO){
                    while(intervalSendCheck.isOpened){
                        send()
                        /*delay(try {
                            intervalSendTime.text.toString().toLong()
                        } catch (e: NumberFormatException) {
                            100
                        })*/
                    }
                }
            }else {
                Log.e(TAG, "onCreate: 关闭")
            }
        }
        hexCheck.setOnClickListener {
            receiveEdit.setText(String(if (hexCheck.isOpened) receiveBufferHex else receiveBuffer))
        }
        clear.setOnClickListener {
            sendCRC.reset()
            receiveCRC.reset()
            byteCountSend.text = "0"
            packgeCountSend.text = "0"
            crcSend.text = "0"
            crcReceive.text = "0"
            byteCountReceive.text = "0"
            packgeCountReceive.text = "0"
            receiveBuffer.delete(0, receiveBuffer.length)
            receiveBufferHex.delete(0, receiveBufferHex.length)
            receiveEdit.setText("")

        }
        switchServiceButton.setOnClickListener {
            ServiceSelectActivity.actionStart(this, deviceDetail)
        }
    }

    fun send(){
        if (BluetoothDeviceWrapper.BLE_MODE == currentMode) {
            fscBleCentralApi.send(sendEdit.text.toString().toByteArray())
        } else if (BluetoothDeviceWrapper.SPP_MODE == currentMode) {
            fscSppApi.send(sendEdit.text.toString().toByteArray())
        }
    }

    private fun disconnectDevice() {
        if (BluetoothDeviceWrapper.BLE_MODE == currentMode) {
            fscBleCentralApi.disconnect()
        } else if (BluetoothDeviceWrapper.SPP_MODE == currentMode) {
            fscSppApi.disconnect()
        }
    }

    private fun setCallBacks(){
        if (BluetoothDeviceWrapper.SPP_MODE == currentMode) {
            fscSppApi = FscSppApiImp.getInstance(this)
            fscSppApi.connect(deviceDetail?.address)
            fscSppApi.setCallbacks(object : FscSppCallbacksImp() {
                override fun sppConnected(device: BluetoothDevice) {
                    uiHandlerDeviceConnected()
                }

                override fun sppDisconnected(device: BluetoothDevice) {
                    uiHandlerDeviceDisconnected()
                }

                override fun packetReceived(dataByte: ByteArray, dataString: String, dataHexString: String) {
                    uiHandlerReceiveData(dataByte, dataString, dataHexString)
                }

                override fun sendPacketProgress(device: BluetoothDevice, percentage: Int, tempByte: ByteArray) {
                    uiHandlerSendDataProgress(percentage, tempByte)
                }
            })
        }else{
            fscBleCentralApi = FscBleCentralApiImp.getInstance(this)
            fscBleCentralApi.connect(deviceDetail!!.address)
            fscBleCentralApi.setCallbacks(object : FscBleCentralCallbacksImp() {
                override fun blePeripheralConnected(gatt: BluetoothGatt, device: BluetoothDevice) {
                    Log.e(TAG, "blePeripheralConnected: 连接成功")
                    Log.e(TAG, "blePeripheralConnected: ${Thread.currentThread().name}")
                    uiHandlerDeviceConnected()
                }

                override fun blePeripheralDisonnected(gatt: BluetoothGatt?, device: BluetoothDevice?) {
                    Log.e(TAG, "blePeripheralDisonnected: ${Thread.currentThread().name}")
                    uiHandlerDeviceDisconnected()
                }

                override fun servicesFound(gatt: BluetoothGatt?, device: BluetoothDevice?, services: ArrayList<BluetoothGattService?>?) {
                    Log.e(TAG, "servicesFound: ${Thread.currentThread().name}")
                    serviceList = services
                }

                override fun packetReceived(gatt: BluetoothGatt, device: BluetoothDevice, service: BluetoothGattService, ch: BluetoothGattCharacteristic, strValue: String, hexString: String, rawValue: ByteArray, timestamp: String) {
                    Log.e(TAG, "packetReceived: ${Thread.currentThread().name}")
                    uiHandlerReceiveData(rawValue, strValue, hexString)
                }

                override fun sendPacketProgress(gatt: BluetoothGatt, device: BluetoothDevice, ch: BluetoothGattCharacteristic?, percentage: Int, tempByte: ByteArray) {
                    uiHandlerSendDataProgress(percentage, tempByte)
                }
            })
        }
    }

    // 收到模块发送的数据时更新UI
    private fun uiHandlerReceiveData(rawValue: ByteArray, strValue: String, hexString: String) {
        receiveBuffer.append(strValue)
        receiveBufferHex.append(hexString)
        receiveCRC.update(rawValue)
        runOnUiThread {
            receiveEdit.setText(String(if (hexCheck.isOpened) receiveBufferHex else receiveBuffer))
            crcReceive.text = java.lang.Long.toHexString(receiveCRC.value).toUpperCase(Locale.ROOT)
            byteCountReceive.text = byteCountReceive.text.toString().toInt().plus(rawValue.size).toString()
            packgeCountReceive.text = packgeCountReceive.text.toString().toInt().plus(1).toString()
        }
    }

    // 连接成功时更新UI
    fun uiHandlerDeviceConnected(){
        runOnUiThread {
            switchServiceButton.isEnabled = true
            header_title_msg.text = resources.getString(R.string.connected)
        }
    }

    // 断开连接时更新UI
    fun uiHandlerDeviceDisconnected(){
        runOnUiThread {
            switchServiceButton.isEnabled = false
            header_title_msg.text = resources.getString(R.string.disconnected)
        }
    }

    // 发送文件进度时更新UI
    fun uiHandlerSendDataProgress(percentage: Int, tempByte: ByteArray){
        runOnUiThread {
            progress.text = "$percentage %"
            byteCountSend.text = (byteCountSend.text.toString().toInt() + tempByte.size).toString()
            packgeCountSend.text = (packgeCountSend.text.toString().toInt().plus(1)).toString()
            sendCRC.update(tempByte)
            // crcSend.text = sendCRC.value.toString().toUpperCase(Locale.ROOT)
            crcSend.text = java.lang.Long.toHexString(sendCRC.value).toUpperCase(Locale.ROOT)
        }
    }



    /**
     * requestCode和startActivityForResult中的requestCode相对应
     * resultCode和Intent是由子Activity通过其setResult()方法返回
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            FILE_SIZE -> {
                val size = data?.getIntExtra("size", 0)!!
                if (BluetoothDeviceWrapper.SPP_MODE == currentMode) {
                    fscSppApi.sendFile(size)
                    // fscSppApi.sendFile(filePath);
                } else {
                    fscBleCentralApi.sendFile(size)
                    // fscBleCentralApi.sendFile(filePath);
                }

            }
            FILE_PATH -> {
                val uri = data!!.data
                val filePath = DefaultSelectorActivity.getDataFromIntent(data)[0]
                if (BluetoothDeviceWrapper.SPP_MODE == currentMode) {
                    fscSppApi.sendFile(filePath)
                    // fscSppApi.sendFile(filePath);
                } else {
                    fscBleCentralApi.sendFile(filePath)
                    // fscBleCentralApi.sendFile(filePath);
                }
            }
        }
    }



    /*var char_table = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )


    fun getTestFile(size: Int): ByteArray {
        var i = 0
        val str = CharArray(size)
        var index = 0;
        while (i < size) {
            str[i] = '['
            str[i + 8] = char_table[index and 0x0000000F]
            str[i + 7] = char_table[index shr 4 and 0x0000000F]
            str[i + 6] = char_table[index shr 8 and 0x0000000F]
            str[i + 5] = char_table[index shr 12 and 0x0000000F]
            str[i + 4] = char_table[index shr 16 and 0x0000000F]
            str[i + 3] = char_table[index shr 20 and 0x0000000F]
            str[i + 2] = char_table[index shr 24 and 0x0000000F]
            str[i + 1] = char_table[index shr 28 and 0x0000000F]
            str[i + 9] = ']'
            index += 1
            i += 10
        }
        Log.e(TAG, "getTestFile: " + str.size)
        return String(str).toByteArray(charset("UTF-8"))
    }*/


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        disconnectDevice()
        return super.onKeyDown(keyCode, event)
    }


    companion object{
        private const val TAG = "ThroughputActivity"

        @JvmField
        var serviceList: ArrayList<BluetoothGattService?>? = null

        @JvmStatic
        fun actionStart(context: Context, deviceDetail: BluetoothDeviceWrapper?) {
            val intent = Intent(context, ThroughputActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("device", deviceDetail)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }


}