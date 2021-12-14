package com.feasycom.feasyblue.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming


interface ApiService {

    @Streaming
    @GET("{file_name}.dfu")
    suspend fun downloadDFU(@Path("file_name") name: Any): ResponseBody


    @POST("/lanch")
    suspend fun downloadImg(@Path("app") app: String): ResponseBody

}