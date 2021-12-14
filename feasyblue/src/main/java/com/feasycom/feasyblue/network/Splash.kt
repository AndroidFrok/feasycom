package com.feasycom.feasyblue.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

data class Splash(
        val code: Int,
        val `data`: LauchData,
        val msg: String
)

data class LauchData(
        val image: String,
        val verion: Int
)


data class Parameter(val key: String, val value: String){

    private val gson = Gson()
    private val map = mapOf(key to value)

    fun getJsonObject(): JsonObject {
        val json = gson.toJson(map)
        return JsonParser().parse(json).asJsonObject
    }
}