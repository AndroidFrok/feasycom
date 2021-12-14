package com.feasycom.feasybeacon.Activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.feasycom.feasybeacon.R
import com.feasycom.feasybeacon.network.Parameter
import com.feasycom.feasybeacon.network.RetrofitClient.api
import com.feasycom.feasyblue.network.SplashService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.android.synthetic.main.activity_advertising.*
import kotlinx.android.synthetic.main.activity_advertising.lanch_img
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
class AdvertisingActivity : BaseActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("https://api.feasycom.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
    }

    val sp by lazy {
        getSharedPreferences("img_url", Context.MODE_PRIVATE)!!
    }


    val parameter = Parameter("beacon")

    var number = 5;
    val timer = Timer()
    val timerTask = object : TimerTask(){
        override fun run() {
            if(number > 0){
                runOnUiThread {
                    to_main.text = "${number}s | Close"
                }
                number --
            }else{
                this.cancel()
            }
        }
    }


    private val handler = Handler(Handler.Callback {
        false
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertising)
            if (isFirstIn) {
                GuideActivity.actionStart(this)
                finish()
            } else {
                to_main.text = "${number}s | Close"

                to_main.setOnClickListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                timer.schedule(timerTask, 1000, 1000)

                lifecycleScope.launch {
                    launch {
                        try {
                            api.getLauch(parameter).apply {
                                sp.edit {
                                    putString("url", data.image)
                                }
                                Log.e(TAG, "onCreate: " + data.image )
                            }
                        }catch (e: Exception){

                        }
                    }

                    launch {
                        Glide.with(this@AdvertisingActivity)
                                .load(sp.getString("url", "https://image.feasycom.com/lanchImage/beacon/lanch.png"))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(lanch_img)
                    }

                    launch {
                        delay(5000)
                        startActivity(Intent(this@AdvertisingActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
    }

    private val isFirstIn: Boolean
        private get() {
            val sf = getSharedPreferences("data", Context.MODE_PRIVATE)
            val isFirstIn = sf.getBoolean("isFirstIn", true)
            val editor = sf.edit()
            return if (isFirstIn) {
                editor.putBoolean("isFirstIn", false)
                editor.commit()
                true
            } else {
                editor.commit()
                false
            }
        }

    override fun refreshFooter() {}
    override fun refreshHeader() {}
    override fun initView() {}
    override fun setClick() {}
    override fun aboutClick() {}
    override fun searchClick() {}
    override fun sensorClick() {}

    companion object{
        private const val TAG = "AdvertisingActivity"
    }
}