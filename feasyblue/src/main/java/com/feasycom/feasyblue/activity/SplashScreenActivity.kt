package com.feasycom.feasyblue.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashScreenActivity: AppCompatActivity() {

    companion object{
        private const val TAG = "SplashActivity"
    }

    private var number = 5;
    private val timer = Timer()
    private val timerTask = object : TimerTask(){
        override fun run() {
            if(number > 0){
                runOnUiThread {
                    to_main.text = "${number}s | Close"
                }
                number --
            }else{
                this.cancel()
                startActivity(Intent(this@SplashScreenActivity, SearchDeviceActivity::class.java))
                finish()
            }
        }
    }

    private val mViewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        //全屏设置
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash)

        timer.schedule(timerTask, 1000, 1000)
        /*mViewModel.urlLiveData.observe(this, {
            Glide.with(this@SplashScreenActivity)
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(lanch_img)
        })*/

        to_main.setOnClickListener {
            timer.cancel()
            startActivity(Intent(this@SplashScreenActivity, SearchDeviceActivity::class.java))
            finish()
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        timer.cancel()
        startActivity(Intent(this@SplashScreenActivity, SearchDeviceActivity::class.java))
        finish()
        return false
    }

}