package com.feasycom.feasyblue.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.footer.*
import kotlinx.android.synthetic.main.store_activity.*
import kotlinx.android.synthetic.main.store_header.*

class StoreActivity: BaseActivity() {

    override fun getLayoutId() = R.layout.store_activity

    override fun refreshFooter() {
        communication_button.setImageResource(R.drawable.communication_off)
        setting_button.setImageResource(R.drawable.setting_off)
        store_button.setImageResource(R.drawable.store_on)
        about_button.setImageResource(R.drawable.about_off)

        communication_button_text.setTextColor(resources.getColor(R.color.color_tb_text))
        setting_button_text.setTextColor(resources.getColor(R.color.color_tb_text))
        store_button_text.setTextColor(resources.getColor(R.color.footer_on_text_color))
        about_button_text.setTextColor(resources.getColor(R.color.color_tb_text))
    }

    override fun refreshHeader() {
        header_title.text = getString(R.string.store)

        refresh.setOnClickListener {
            webView.reload()

        }
        back.setOnClickListener {
            webView.goBack()
        }
    }

    override fun initView() {
        webView.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try{
                    if(url != "https://store.feasycom.com/"){
                        back.visibility = VISIBLE
                    }else{
                        back.visibility = GONE
                    }
                }catch (e: IllegalStateException){
                    e.printStackTrace()
                }
            }
        }
        webView.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                try {
                    progressBar?.progress = newProgress
                    if (newProgress == 100){
                        progressBar?.visibility = GONE
                    }else{
                        if(progressBar?.visibility == GONE){
                            progressBar?.visibility = VISIBLE
                        }
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        webView.loadUrl("https://store.feasycom.com/")

    }

    override fun loadData() {
    }

    companion object{
        private const val TAG = "StoreFragment"
        @JvmStatic
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, StoreActivity::class.java))
        }
    }
}