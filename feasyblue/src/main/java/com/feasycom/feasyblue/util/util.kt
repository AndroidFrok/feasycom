package com.feasycom.feasyblue.util

import android.util.Log
import com.feasycom.feasyblue.App
import java.io.File

fun getFileList(): List<String>{
    var separator = App.context.filesDir.absolutePath+
            File.separator
    val file: File = File(separator)
    val files = file.listFiles()
    if (files == null) {
        Log.e("error", "空目录")
        return ArrayList()
    }
    val s: MutableList<String> = ArrayList()
    for (i in files.indices) {
        files[i].apply {
            if(absolutePath.contains("png")){
                s.add(files[i].absolutePath)
            }
        }
    }
    return s
}