package com.feasycom.feasybeacon.network

data class Lanch(
        val code: Int,
        val `data`: LauchData,
        val msg: String
)

data class LauchData(
        val image: String,
        val verion: Int
)

data class Parameter(var app: String)