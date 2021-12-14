package com.feasycom.feasyblue.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.view.MenuItem
import android.webkit.*
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.footer.*
import kotlinx.android.synthetic.main.store_activity.*
import kotlinx.android.synthetic.main.store_header.*


class StoreActivity1 : BaseActivity() {


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

        /*back.setImageResource(R.drawable.goback)*/
        // header_right_text.visibility = View.GONE
        header_title.text = getString(R.string.store)
        /*refresh.setImageResource(R.drawable.ic_baseline_refresh_24)*/
        refresh.setOnClickListener {
            webView.reload()

        }
        /*setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.store)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.addOnMenuVisibilityListener {
            Log.e(TAG, "refreshHeader: **************" )
        }
        toolbar_title.setText(R.string.store)*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }
    override fun initView() {
        webView.webViewClient = object : WebViewClient(){
            override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                Log.e(TAG, "onReceivedSslError: " )
            }

            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                if(url != "https://store.feasycom.com/"){
                    // (requireActivity() as MainActivity).storeBackVisibility(View.VISIBLE)
                }else{
                    // (requireActivity() as MainActivity).storeBackVisibility(View.GONE)
                }
            }
        }
        webView.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                /*progressBar?.progress = newProgress
                if (newProgress == 100){
                    progressBar?.visibility = View.GONE
                }else{
                    if(progressBar?.visibility == View.GONE){
                        progressBar?.visibility = View.VISIBLE
                    }
                }*/
            }
        }
        Log.e(TAG, "initView: 加载网页" )
        webView.loadUrl("https://store.feasycom.com/")

        back.setOnClickListener {
            SettingActivity.actionStart(this)
            finishActivity()
        }
    }
    override fun loadData() {}



    companion object{
        private const val TAG = "StoreActivity"
        @JvmStatic
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, StoreActivity1::class.java))
        }
    }

}