package com.feasycom.feasyblue.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val TAG = "RetrofitClient"
        /*
        **打印retrofit信息部分
       */
        private val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.e(TAG, "log: " + message )
            }

        })


    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    val api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.feasycom.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
        return@lazy retrofit.create(SplashService::class.java)
    }

    val reqApi by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://dfu.feasycom.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()
        return@lazy retrofit.create(ApiService::class.java)
    }

}