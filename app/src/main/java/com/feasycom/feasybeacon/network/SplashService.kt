package com.feasycom.feasyblue.network

import com.feasycom.feasybeacon.network.Lanch
import com.feasycom.feasybeacon.network.Parameter
import okhttp3.ResponseBody
import retrofit2.http.*

interface SplashService {

    /*@Streaming
    @FormUrlEncoded
    @POST("lanch")
    suspend fun getLauch(@FieldMap map: Map<String, String>): Lanch*/

    @Streaming
    // @FormUrlEncoded
    @POST("lanch")
    suspend fun getLauch(@Body parameter: Parameter): Lanch

}