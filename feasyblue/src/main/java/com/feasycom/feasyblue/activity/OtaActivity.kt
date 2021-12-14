package com.feasycom.feasyblue.activity

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.bean.DfuFileInfo
import com.feasycom.controler.*
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.dfileselector.activity.DefaultSelectorActivity
import com.feasycom.feasyblue.dialogFragment.DfuNameDialogFragment
import com.feasycom.feasyblue.network.RetrofitClient
import com.feasycom.feasyblue.util.SettingConfigUtil
import com.feasycom.util.FileUtil
import com.feasycom.util.ToastUtil
import kotlinx.android.synthetic.main.activity_ota.*
import kotlinx.android.synthetic.main.header_ota_text.*
import kotlinx.android.synthetic.main.header_text_text.*
import kotlinx.android.synthetic.main.header_text_text.header_left_image
import kotlinx.android.synthetic.main.header_text_text.header_title
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.*


class OtaActivity: BaseActivity() {

    private var dfuByte: ByteArray? = null
    private var dfuFileName: String? = null
    private var dfuFilePath: String? = null
    private var dfuModelNameString: String? = null
    private var dfuAppVersionString: String? = null
    private var dfuBootLoaderVersionString: String? = null
    lateinit var api: Any
    private var moduleVersionString: String? = null
    private var moduleBootLoaderVersionString: String? = null
    private var moduleModleNameString: String? = null
    private var device: BluetoothDeviceWrapper? = null
    private var openPath: String? = null
    private var dfuFileInformation: DfuFileInfo? = null

    private var name: String? = null

    override fun refreshFooter(){}
    override fun refreshHeader() {
        header_left_image.setImageResource(R.drawable.goback)
        header_title.text = getString(R.string.OTAUpgrade)
        header_left_image.setOnClickListener {
            finish()
            disconnect(api)
        }
    }

    override fun initView() {
        selectFile.setOnClickListener {
            DefaultSelectorActivity.startActivityForResult(this, false,
                    false,1 , openPath?.trim() , true);

        }

        resetFlag.isChecked = SettingConfigUtil.getData(applicationContext, "resetFlag", false) as Boolean

        resetFlag.setOnCheckedChangeListener { _, isChecked ->
            SettingConfigUtil.saveData(applicationContext, "resetFlag", isChecked)
        }

        download.setOnClickListener {
            val dfuNameDialogFragment = DfuNameDialogFragment()
            dfuNameDialogFragment.show(supportFragmentManager, "duf")
            dfuNameDialogFragment.onClickComplete = {
                if(it.isNotBlank()){
                    name = it
                    val path = "${getExternalFilesDir(null)}${File.separator}${it}.dfu"
                    val futureStudioIconFile = File(path)
                    if(!futureStudioIconFile.exists()){
                        MainScope().launch(Dispatchers.Main) {
                            try {
                                RetrofitClient.reqApi.downloadDFU(it).apply {
                                    writeResponseBodyToDisk(this)
                                }
                            }catch (e : Exception){
                                runOnUiThread {
                                    Toast.makeText(this@OtaActivity,"Network or file name error",Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }else{
                        dfuByte = FileUtil.readFileToByte(path)
                        SettingConfigUtil.saveData(applicationContext, "filePath", path)
                        dfuFileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
                        runOnUiThread {
                            fileName.text = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
                        }

                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this,"File name error",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        updateBegin.setOnClickListener {
            Log.e(TAG, "initView: 设置为false" )
            resetFlag.isEnabled = false
            if ((dfuByte != null) && (dfuFilePath != null) && (!dfuFilePath.equals(" "))
                    && (dfuFileName != null) && (!dfuFileName.equals(""))) {
                setCallBacks(api)
                MainScope().launch {
                    delay(500)
                    if (!startOTA(api, dfuByte!!, SettingConfigUtil.getData(applicationContext, "resetFlag", false) as Boolean)) {
                        if (((otaState!!.text.toString() != resources.getString(R.string.versionIsLow))
                                        && (otaState!!.text.toString() != resources.getString(R.string.nameNotMatch)))) {
                            otaState!!.text = resources.getString(R.string.updateFailed)
                        }
                        disconnect(api)
                        updateBegin!!.isEnabled = true
                    }
                }
                Log.e(TAG, "initView1: 设置为false" )
                updateBegin.isEnabled = false
                otaProgress.visibility = View.VISIBLE
                progressCount.visibility = View.VISIBLE
            } else {
                ToastUtil.show(this, resources.getString(R.string.pleaseSelectTheFirmware));
            }
        }
        if ((dfuFilePath != null) && (dfuFilePath != " ")) {
            handlerFileByte(dfuFilePath!!)
        }
        uiUpdate()
    }

    override fun loadData() {
        device = intent.getSerializableExtra("device").let {
            when(it){
                is BluetoothDeviceWrapper -> {
                    api = when(it.model){
                        "BLE" -> {
                            when(it.feasyBeacon?.module){
                                "21","25","26","27","28","29","30","31","34","35","36","39" ->{
                                    FscBeaconApiImp.getInstance(this).run {
                                       connect(this, it)
                                    }
                                }
                            }
                            if(it.feasyBeacon == null){
                                FscBleCentralApiImp.getInstance(this).run {
                                    Log.e(TAG, "loadData: BLE" )
                                    connect(this, it)
                                }
                            }else{

                            }
                        }
                        "SPP" -> {
                            FscSppApiImp.getInstance(this).run {
                                connect(this, it)
                            }
                        }
                        else -> {

                        }
                    }
                    it
                }
                else -> {
                    null
                }
            }
        }
        dfuFilePath = SettingConfigUtil.getData(applicationContext, "filePath", " ") as String
        Log.e(TAG, "loadData: " + SettingConfigUtil.getData(applicationContext, "filePath", " ") )
        // openPath = SettingConfigUtil.getData(applicationContext, "openPath", " ") as String

        // openPath = "${getExternalFilesDir(null)}${File.separator}"
        openPath = "${Environment.getExternalStorageDirectory()}"
        Log.e(TAG, "loadData: " + openPath )
        /*Log.e(TAG, "loadData: ${openPath}"  )
        Log.e(TAG, "${getExternalFilesDir(null)}${File.separator}")*/
        fileName.text = dfuFileName;
    }



    override fun getLayoutId(): Int = R.layout.activity_ota

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            /*resetFlag!!.isEnabled = false*/
            when(requestCode){
                DefaultSelectorActivity.FILE_SELECT_REQUEST_CODE ->{
                    val filePath = DefaultSelectorActivity.getDataFromIntent(data)[0]
                    if (filePath == null) {
                        ToastUtil.show(applicationContext, resources.getString(R.string.openSendFileError))
                        dfuByte = null
                        dfuFilePath = null
                        openPath = ""
                        return
                    } else {
                        dfuFilePath = filePath
                        handlerFileByte(dfuFilePath!!)
                    }
                    openPath = filePath.substring(0, filePath.lastIndexOf("/"))
                    SettingConfigUtil.saveData(applicationContext, "openPath", openPath)
                    moduleVersionString = null
                    moduleBootLoaderVersionString = null
                    moduleModleNameString = null
                    otaProgress.progress = 0
                    otaProgress.visibility = View.INVISIBLE
                    progressCount.text = "0 %"
                    progressCount.visibility = View.INVISIBLE
                    uiUpdate()
                }
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        disconnect(api)
        finish()
        return true
    }

    private fun setCallBacks(api: Any){
        when(api){
            is FscBeaconApiImp -> {
                api.setCallbacks(fscBeaconCallbacks)
            }
            is FscBleCentralApiImp -> {
                api.setCallbacks(fscBleCentralCallbacksImp)
            }
            is FscSppApiImp -> {
                api.setCallbacks(fscSppCallbacksImp)
            }
        }
    }
    
    private var fscBeaconCallbacks = object : FscBeaconCallbacksImp(){

    }
    private var fscBleCentralCallbacksImp = object : FscBleCentralCallbacksImp(){
        override fun otaProgressUpdate(percentage: Int, status: Int) {
            runOnUiThread(object : Runnable {
                override fun run() {
                    try {
                        otaProgress!!.progress = percentage
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                        return
                    }
                    progressCount!!.text = "$percentage %"
                    if (status == FscSppApi.OTA_STATU_FINISH) {
                        otaState!!.text = resources.getString(R.string.updateSuccess)
                        if(clear.isChecked){
                            // 清除配对记录
                            clearDevice(api)
                        }


                        disconnect(api)
                        updateBegin!!.isEnabled = true

                    } else if (status == FscSppApi.OTA_STATU_FAILED) {
                        /**
                         * if the OTA information does not match, this state does not change
                         */
                        if (((otaState!!.text.toString() != resources.getString(R.string.versionIsLow))
                                        && (otaState!!.text.toString() != resources.getString(R.string.nameNotMatch)))) {
                            otaState!!.text = resources.getString(R.string.updateFailed)
                        }
                        //                            LogUtil.i(TAG,"disconnect 6");
                        disconnect(api)
                        updateBegin!!.isEnabled = true
                    } else if (status == FscSppApi.OTA_STATU_BEGIN) {
                        if (!checkOtaInformation()) {
                            disconnect(api)
                            updateBegin!!.isEnabled = true
                        } else {
                            otaState!!.text = resources.getString(R.string.toUpgrade)
                        }
                    } else if (status == FscSppApi.OTA_STATU_PROCESSING) {
                        otaState!!.text = resources.getString(R.string.updating)

                        resetFlag.isEnabled = true
                    }
                }

            })
        }

        override fun packetReceived(p0: BluetoothGatt?, p1: BluetoothDevice?, p2: BluetoothGattService?, p3: BluetoothGattCharacteristic?, dataString: String, dataHexString: String?, dataByte: ByteArray, p7: String?) {
            if (dataString.contains("OK")) {

                if (dataString.length >= 15) {
                    moduleVersionString = Integer.valueOf(FileUtil.stringToInt(dataString.substring(3, 7))).toString()
                    moduleBootLoaderVersionString = Integer.valueOf(FileUtil.stringToInt(dataString.substring(7, 11))).toString()
                    moduleModleNameString = FileUtil.getModelName(FileUtil.stringToInt(dataString.substring(11, 15)))
                } else {
                    moduleVersionString = null
                    moduleBootLoaderVersionString = null
                    moduleModleNameString = null
                }
                if (isConnected(api)) {
                    connect(api,device!!)
                }
            } else if (dataString.contains("C")) {
            } else if (dataString.contains("S")) {
            } else if ((dataByte[0].toInt() != 0x06) && (dataByte[0].toInt() != 0x15) && (dataByte[0].toInt() != 0x7F)) {
                moduleVersionString = null
                moduleBootLoaderVersionString = null
                moduleModleNameString = null
            }
            runOnUiThread {
                if (moduleVersionString != null) {
                    moduleVersion!!.text = moduleVersionString
                } else {
                    moduleVersion!!.text = "-"
                }
                if (moduleBootLoaderVersionString != null) {
                    moduleBootloader!!.text = moduleBootLoaderVersionString
                } else {
                    moduleBootloader!!.text = "-"
                }
                if (moduleModleNameString != null) {
                    moduleModelName!!.text = moduleModleNameString
                } else {
                    moduleModelName!!.text = "-"
                }
            }
        }
    }
    private var fscSppCallbacksImp = object : FscSppCallbacksImp(){
        override fun otaProgressUpdate(percentage: Int, status: Int) {
            runOnUiThread(object : Runnable {
                override fun run() {
                    try {
                        otaProgress!!.progress = percentage
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                        return
                    }
                    progressCount!!.text = "$percentage %"
                    if (status == FscSppApi.OTA_STATU_FINISH) {
                        otaState!!.text = resources.getString(R.string.updateSuccess)
                        Log.e(TAG, "run: " + clear.isChecked )
                        if(clear.isChecked){
                            // 清除配对记录
                            clearDevice(api)
                        }
                        disconnect(api)
                        updateBegin!!.isEnabled = true

                    } else if (status == FscSppApi.OTA_STATU_FAILED) {
                        /**
                         * if the OTA information does not match, this state does not change
                         */
                        if (((otaState!!.text.toString() != resources.getString(R.string.versionIsLow))
                                        && (otaState!!.text.toString() != resources.getString(R.string.nameNotMatch)))) {
                            otaState!!.text = resources.getString(R.string.updateFailed)
                        }
                        //                            LogUtil.i(TAG,"disconnect 6");
                        disconnect(api)
                        updateBegin!!.isEnabled = true
                    } else if (status == FscSppApi.OTA_STATU_BEGIN) {
                        if (!checkOtaInformation()) {
                            disconnect(api)
                            updateBegin!!.isEnabled = true
                        } else {
                            otaState!!.text = resources.getString(R.string.toUpgrade)
                        }
                    } else if (status == FscSppApi.OTA_STATU_PROCESSING) {
                        otaState!!.text = resources.getString(R.string.updating)
                        /*resetFlag.isEnabled = true*/
                    }
                }
            })
        }

        override fun packetReceived(dataByte: ByteArray, dataString: String, dataHexString: String) {
            if (dataString.contains("OK")) {
                Log.e(TAG, "packetReceived: " + dataString )
                if (dataString.length >= 15) {
                    moduleVersionString = Integer.valueOf(FileUtil.stringToInt(dataString.substring(3, 7))).toString()
                    moduleBootLoaderVersionString = Integer.valueOf(FileUtil.stringToInt(dataString.substring(7, 11))).toString()
                    moduleModleNameString = FileUtil.getModelName(FileUtil.stringToInt(dataString.substring(11, 15)))
                } else {
                    moduleVersionString = null
                    moduleBootLoaderVersionString = null
                    moduleModleNameString = null
                }
                /*thread {
                    Thread.sleep(500)
                    if (isConnected(api)) {
                        connect(api,device!!)
                    }
                }*/

            } else if (dataString.contains("C")) {
            } else if (dataString.contains("S")) {
            } else if ((dataByte[0].toInt() != 0x06) && (dataByte[0].toInt() != 0x15) && (dataByte[0].toInt() != 0x7F)) {
                moduleVersionString = null
                moduleBootLoaderVersionString = null
                moduleModleNameString = null
            }
            runOnUiThread {
                if (moduleVersionString != null) {
                    moduleVersion!!.text = moduleVersionString
                } else {
                    moduleVersion!!.text = "-"
                }
                if (moduleBootLoaderVersionString != null) {
                    moduleBootloader!!.text = moduleBootLoaderVersionString
                } else {
                    moduleBootloader!!.text = "-"
                }
                if (moduleModleNameString != null) {
                    moduleModelName!!.text = moduleModleNameString
                } else {
                    moduleModelName!!.text = "-"
                }
            }
        }

        override fun sppConnected(device: BluetoothDevice?) {
            super.sppConnected(device)

            runOnUiThread {
                header_title_msg.setText(R.string.connected)
            }


        }

        override fun sppDisconnected(device: BluetoothDevice?) {
            super.sppDisconnected(device)
            runOnUiThread {
                header_title_msg.setText(R.string.disconnected)
            }

        }
    }




    private fun checkOtaInformation(): Boolean {
        /**
         * make sure the module type is the same
         */
        if (moduleModleNameString != null && moduleModleNameString == " " && moduleModleNameString == "-" && moduleModleNameString != dfuModelNameString) {
            runOnUiThread { otaState.text = resources.getString(R.string.nameNotMatch) }
            return false
        }
        /**
         * make sure the firmware app version is higher
         */
        if (moduleVersionString != null && moduleVersionString != " " && moduleVersionString != "-" && dfuAppVersionString != "-") {
            Log.e(TAG, "moduleVersionString:    ${moduleVersionString}" )
            Log.e(TAG, "dfuAppVersionString:    ${dfuAppVersionString}" )
            if (moduleVersionString!!.toInt() < dfuAppVersionString!!.toInt()) {
                runOnUiThread { otaState.text = resources.getString(R.string.versionIsLow) }
                return false
            }
        }
        /**
         * make sure the firmware bootloader version is higher
         * note that if the module is already in the bootloader state you will get C or S
         */
        if (moduleBootLoaderVersionString != null && moduleBootLoaderVersionString != " " && moduleBootLoaderVersionString != "-"
                && moduleBootLoaderVersionString != "S" && moduleBootLoaderVersionString != "C" && moduleBootLoaderVersionString != "-" && dfuBootLoaderVersionString != "-" ) {
            if (moduleBootLoaderVersionString!!.toInt() > dfuBootLoaderVersionString!!.toInt()) {
                runOnUiThread { otaState.text = resources.getString(R.string.blIsHight) }
                return false
            }
        }
        return true
    }


    private fun connect(api: Any, device: BluetoothDeviceWrapper): Any  {
        when (api) {
            is FscBeaconApiImp -> {
                api.connect(device, "000000")
            }
            is FscBleCentralApiImp -> {
                Log.e(TAG, "connect: 开始连接" )
                api.connect(device.address)
            }
            is FscSppApiImp -> {
                Log.e(TAG, "connect: 连接" )
                api.connect(device.address)
            }
            else -> {

            }
        }
        runOnUiThread {
            header_title_msg.setText(R.string.connecting)
        }
        return api
    }

    fun disconnect(api: Any) {
        when (api) {
            is FscBeaconApiImp -> {
                api.disconnect()
            }
            is FscBleCentralApiImp -> {
                api.disconnect()
            }
            is FscSppApiImp -> {
                api.disconnect()
            }
            else -> {

            }
        }
    }

    fun clearDevice(api: Any){
        when(api){
            is FscBeaconApiImp -> {
                // api.disconnect()
            }
            is FscBleCentralApiImp -> {
                // api.disconnect()
            }
            is FscSppApiImp -> {
                // api.disconnect()
                // api.clearDevice(device)
            }
            else -> {

            }
        }
    }

    fun isConnected(api: Any): Boolean{
        return api.let {
            when (it) {
                is FscBeaconApiImp -> {
                    it.isConnected
                }
                is FscBleCentralApiImp -> {
                    it.isConnected
                }
                is FscSppApiImp -> {
                    it.isConnected
                }
                else -> {
                    false
                }
            }
        }
    }
    private fun startOTA(api: Any, var1: ByteArray, var2: Boolean): Boolean{
        return api.run {
            when(this){
                is FscBeaconApiImp -> {
                    false
                }
                is FscBleCentralApiImp -> {
                    startOTA(var1, var2)
                }
                is FscSppApiImp -> {
                    // startOTA(var1, var2)
                    startOTA(var1, var2)
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun uiUpdate() {
        runOnUiThread {
            /**
             * dfu file information update
             */
            if (dfuByte != null && dfuFileName != null) {
                otaState.text = resources.getString(R.string.waitingForUpdate)
                fileName.text = dfuFileName
                api.apply {
                    when(this){
                        is FscSppApiImp -> {
                            dfuFileInformation = this.checkDfuFile(dfuByte)
                        }
                        is FscBleCentralApiImp -> {
                            // dfuFileInformation = this.checkDfuFile(dfuByte)
                        }
                    }
                }
                if (dfuFileInformation != null) {
                    /**
                     * 固件信息改成从.dfu 中解析获取
                     */
                    dfuAppVersionString = Integer.valueOf(dfuFileInformation!!.versonStart).toString()
                    dfuModelNameString = FileUtil.getModelName(dfuFileName)
                    dfuBootLoaderVersionString = Integer.valueOf(dfuFileInformation!!.bootloader).toString()
                } else {
                    dfuAppVersionString = "-"
                    dfuModelNameString = "-"
                    dfuBootLoaderVersionString = "-"
                    ToastUtil.show(applicationContext, resources.getString(R.string.dfuIllegal))
                }
            } else {
                otaState.text = resources.getString(R.string.waitingForUpdate)
                fileName.text = ""
                dfuAppVersionString = "-"
                dfuModelNameString = "-"
                dfuBootLoaderVersionString = "-"

            }
            dfuModelName.text = dfuModelNameString
            dfuBootloader.text = dfuBootLoaderVersionString
            dfuVersion.text = dfuAppVersionString
        }
    }

    private fun handlerFileByte(filePath: String) {
        var end = 0
        try {
            dfuFileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."))
            end = filePath.lastIndexOf(".")
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.show(applicationContext, resources.getString(R.string.openSendFileError))
            end = 0
            dfuFileName = null
        }
        if (end > 0) {
            val suffix = filePath.substring(end + 1)
            if (suffix.contains("dfu")) {
                try {
                    dfuByte = FileUtil.readFileToByte(filePath)
                    SettingConfigUtil.saveData(applicationContext, "filePath", filePath)
                } catch (e: IOException) {
                    ToastUtil.show(applicationContext, resources.getString(R.string.openSendFileError))
                    e.printStackTrace()
                }
            } else {
                dfuFileName = null
                ToastUtil.show(applicationContext, resources.getString(R.string.selectDfu))
            }
        }
    }


    private suspend fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return withContext(Dispatchers.IO){
            try {
                if(name!!.isEmpty()){
                    return@withContext false
                }
                // val path = "${getExternalFilesDir(null)}${File.separator}${name}.dfu"
                val path = "${Environment.getExternalStorageDirectory()}/${File.separator}/${name}.dfu"
                Log.e(TAG, "writeResponseBodyToDisk: "+ path )
                val futureStudioIconFile = File(path)
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    val fileReader = ByteArray(4096)
                    val fileSize = body.contentLength()
                    var fileSizeDownloaded: Long = 0
                    inputStream = body.byteStream()
                    outputStream = FileOutputStream(futureStudioIconFile)
                    while (true) {
                        val read: Int = inputStream.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read.toLong()
                        Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
                    }
                    outputStream.flush()
                    dfuFilePath = path
                    runOnUiThread {
                        fileName.text = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
                    }
                    dfuByte = FileUtil.readFileToByte(path)
                    SettingConfigUtil.saveData(applicationContext, "filePath", path)
                    dfuFileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
                    true
                } catch (e: IOException) {
                    false
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            } catch (e: IOException) {
                false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    companion object{
        private const val TAG = "OtaActivity"

        @JvmStatic
        fun actionStart(context: Context, device: BluetoothDeviceWrapper?) {
            val intent = Intent(context, OtaActivity::class.java)
            intent.putExtra("device", device)
            context.startActivity(intent)
        }
    }

}

