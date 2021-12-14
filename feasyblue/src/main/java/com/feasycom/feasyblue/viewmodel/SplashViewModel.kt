package com.feasycom.feasyblue.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.feasycom.feasybeacon.worker.SplashWorker
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.util.getFileList

class SplashViewModel: ViewModel() {

    var urlLiveData = MutableLiveData<Any>()

    var files: List<String> = ArrayList<String>()

    private val workManager by lazy {
        WorkManager.getInstance()
    }

    private val request by lazy {
        OneTimeWorkRequest.Builder(SplashWorker::class.java)
            .build()
    }

    init {
        files = getFileList()
        if(files.isNotEmpty()){
            urlLiveData.value = files[files.lastIndex]
        }else{
            urlLiveData.value = R.drawable.loading
        }

        workManager.enqueue(request)
    }

}