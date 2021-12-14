package com.feasycom.feasyblue.network

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.http.*

interface SplashService {

    @Streaming
    @POST("https://api.feasycom.com/lanch")
    suspend fun getSplash(@Body jsonObject: JsonObject): Splash


    @Streaming //大文件时要加不然会OOM
    @GET("https://image.feasycom.com/lanchImage/{parameter}/{fileName}")
    suspend fun downImg(@Path("parameter") path: String = "sens", @Path("fileName") fileName: String= "lanch.png" ): ResponseBody

}