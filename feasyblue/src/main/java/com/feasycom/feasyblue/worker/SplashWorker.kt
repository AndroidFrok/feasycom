package com.feasycom.feasybeacon.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.feasycom.feasyblue.network.Parameter
import com.feasycom.feasyblue.network.RetrofitClient.api
import com.feasycom.feasyblue.util.getFileList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class SplashWorker(var context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    private val parameter = Parameter("app","beacon")
    private var verion: Int = 0
    private var files: List<String> = ArrayList<String>()

    override fun doWork(): Result {
        GlobalScope.launch(Dispatchers.IO) {
            files = getFileList()
            try {
                val splash = api.getSplash(parameter.getJsonObject())
                val substring = splash.data.image.substring(splash.data.image.lastIndexOf('/') + 1)
                Log.e(TAG, "doWork: " + substring )
                if(splash.code == 200){
                    verion = splash.data.verion
                    files.filter {
                        it.contains("${verion}.png")
                    }.apply {
                        if(size == 0){
                            val downImage = api.downImg("beacon",substring )
                            val filePath = context.filesDir.absolutePath +
                                    File.separator + "${verion}.png"
                            File(filePath).writeBytes(downImage.bytes())
                        }
                    }
                }
            }catch (e: Exception){
                Log.e(TAG, "doWork: $e")
                e.printStackTrace()
            }catch (e: HttpException){

            }
        }
        return  Result.success()
    }

    companion object{
        private const val TAG = "SplashWorker"
    }
}