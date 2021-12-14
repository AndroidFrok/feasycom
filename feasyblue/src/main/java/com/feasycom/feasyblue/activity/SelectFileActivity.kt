package com.feasycom.feasyblue.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.dfileselector.activity.DefaultSelectorActivity
import com.feasycom.feasyblue.dialogFragment.SelectFlieDialogFragment
import kotlinx.android.synthetic.main.activity_select_file.*
import java.io.File
import java.util.*



val FILE_SIZE = 2000
val FILE_PATH = 2001

class SelectFileActivity: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_file)
        customizeButton.setOnClickListener(this)
        /*customizeButton.setOnClickListener {
            when (rangeSeekBar.leftSeekBar.progress.toInt()) {
                0 -> {
                    Log.e(TAG, "onCreate: 1024 KB")
                    *//*getTestFile(if (editText.text.isBlank()) {
                        1024 * 1024
                    } else {
                        editText.text.toString().toInt() * 1024
                    })*//*
                }
                33 -> {
                    Log.e(TAG, "onCreate: 1024 MB")
                    *//*getTestFile(if (editText.text.isBlank()) {
                        1024 * 1024 * 1024
                    } else {
                        editText.text.toString().toInt() * 1024 * 1024
                    })*//*
                }
                66 -> {
                    Log.e(TAG, "onCreate: 1024 10^3")
                    *//*getTestFile(if (editText.text.isBlank()) {
                        1024 * 10 * 10 * 10
                    } else {
                        editText.text.toString().toInt() * 10 * 10 * 10
                    })*//*
                }
                100 -> {
                    Log.e(TAG, "onCreate: 1024 10^6")
                    *//*getTestFile(if (editText.text.isBlank()) {
                        1024 * 10 * 10 * 10 * 10 * 10 * 10
                    } else {
                        editText.text.toString().toInt() * 10 * 10 * 10 * 10 * 10 * 10
                    })*//*
                }
                else -> "错误"
            }

            Log.e(TAG, "onCreate: ${
                when (rangeSeekBar.leftSeekBar.progress.toInt()) {
                    0 -> {
                        "1024 KB"
                    }
                    33 -> {
                        "1024 MB"
                    }
                    66 -> {
                        " 1024 10^3"
                    }
                    100 -> {
                        "1024 10^6"
                    }
                    else -> "错误"
                }
            }")
        }*/
        selectFile.setOnClickListener {
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resources.configuration.locales[0]
            } else {
                resources.configuration.locale
            }
            val language: String = locale.language
            SelectFlieDialogFragment().apply {
                mCallback = {
                    val openPath = if(language.endsWith("zh")){
                        when(it){
                            0 -> {
                                "${getExternalStorageDirectory()}/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv"
                            }
                            1 -> {
                                "${getExternalStorageDirectory()}/Android/data/com.tencent.mm/MicroMsg/Download"
                            }
                            2 -> {
                                "${getExternalFilesDir(null)}${File.separator}"
                            }
                            else -> {
                                "${getExternalStorageDirectory()}"
                            }
                        }
                    }else {
                        when(it){
                            0 -> {
                                "${getExternalFilesDir(null)}${File.separator}"
                            }
                            else -> {
                                "${getExternalStorageDirectory()}"
                            }
                        }
                    }
                    val file = File(openPath)
                    //判断文件夹是否存在,如果不存在则创建文件夹
                    if (!file.exists()) {
                        Log.e(TAG, "onCreate: 路径不存在  ${openPath}")
                    }else {
                        DefaultSelectorActivity.startActivityForResult(
                                this@SelectFileActivity, false,
                                false, 1, openPath.trim(), false
                        )
                    }
                }
                show(supportFragmentManager, null)
            }
        }
        // 10K
        button.setOnClickListener(this)
        // 1M
        button16.setOnClickListener(this)
        // 20k
        button17.setOnClickListener(this)
        // 2M
        button18.setOnClickListener(this)
        // 100K
        button19.setOnClickListener(this)
        // 5M
        button20.setOnClickListener(this)
        // 200K
        button21.setOnClickListener(this)
        // 10M
        button22.setOnClickListener(this)
        // 500K
        button23.setOnClickListener(this)
        // 20MB
        button24.setOnClickListener(this)
    }
    /*var char_table = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )
    fun getTestFile(size: Int){
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
        Log.e(TAG, "getTestFile: ${String(str)}")
    }*/

    companion object{
        private const val TAG = "SelectFileActivity"
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.button, R.id.button17, R.id.button19, R.id.button21, R.id.button23, R.id.button16, R.id.button18, R.id.button20, R.id.button22, R.id.button24, R.id.customizeButton -> {
                val intent = Intent()
                intent.putExtra("size", when (v.id) {
                    R.id.button -> {
                        // 10k
                        10000
                    }
                    R.id.button17 -> {
                        // 20K
                        20000
                    }
                    R.id.button19 -> {
                        // 100k
                        100000
                    }
                    R.id.button21 -> {
                        // 200K
                        200000
                    }
                    R.id.button23 -> {
                        // 500k
                        500000
                    }
                    R.id.button16 -> {
                        // 1M
                        1000000
                    }
                    R.id.button18 -> {
                        // 2M
                        2000000
                    }
                    R.id.button20 -> {
                        // 5M
                        5000000
                    }
                    R.id.button22 -> {
                        // 10M
                        10000000
                    }
                    R.id.button24 -> {
                        // 20M
                        20000000
                    }
                    R.id.customizeButton -> {
                        // 滑块数据
                        Log.e(TAG, "onClick: 选择" )
                        getTestFileSize()
                    }
                    else -> 0
                })
                Log.e(TAG, "onClick: ${intent.getIntExtra("size", 0)}")
                // 设置返回码和返回携带的数据
                setResult(FILE_SIZE, intent)
                finish()
            }
        }
    }

    private fun getTestFileSize(): Int{
        var size = 0
        size = if(editText.text.isNotBlank()){
            when (rangeSeekBar.leftSeekBar.progress.toInt()) {
                0 -> {
                    Log.e(TAG, "getTestFileSize: ${editText.text.toString().toInt() * 1000}" )
                    editText.text.toString().toInt() * 1000
                }
                33 -> {
                    Log.e(TAG, "getTestFileSize: ${editText.text.toString().toInt() * 1000 * 1000}" )
                    editText.text.toString().toInt() * 1000 * 1000
                }
                66 -> {
                    Log.e(TAG, "getTestFileSize: ${editText.text.toString().toInt() * 10 * 10 * 10}" )
                    editText.text.toString().toInt() * 10 * 10 * 10
                }
                100 -> {
                    Log.e(TAG, "getTestFileSize: ${editText.text.toString().toInt() * 10 * 10 * 10 * 10 * 10 * 10}" )
                    editText.text.toString().toInt() * 10 * 10 * 10 * 10 * 10 * 10
                }
                else -> 0
            }
        }else{
            when (rangeSeekBar.leftSeekBar.progress.toInt()) {
                0 -> {
                    Log.e(TAG, "getTestFileSize: ${50 * 1000}" )
                    50 * 1000
                }
                33 -> {
                    Log.e(TAG, "getTestFileSize: ${50 * 1000 * 1000}" )
                    50 * 1000 * 1000
                }
                66 -> {
                    Log.e(TAG, "getTestFileSize: ${50 * 10 * 10 * 10}" )
                    50 * 10 * 10 * 10
                }
                100 -> {
                    Log.e(TAG, "getTestFileSize: ${50 * 10 * 10 * 10 * 10 * 10 * 10}" )
                    50 * 10 * 10 * 10 * 10 * 10 * 10
                }
                else -> 0
            }
        }
        return size
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            // data?.flags = FLAG_ACTIVITY_NEW_TASK
            setResult(FILE_PATH,data)
            finish()
        }
    }


    
}