package com.arcmce.boogjp.network.repository

import com.arcmce.boogjp.network.api.MixCloudApi
import com.arcmce.boogjp.network.api.RadioApi
import com.arcmce.boogjp.network.api.RetrofitInstance
import com.arcmce.boogjp.network.model.MixCloudCloudcast
import com.arcmce.boogjp.network.model.MixCloudPlaylist
import com.arcmce.boogjp.network.model.RadioInfo
import retrofit2.Call
import retrofit2.http.GET

class Repository() {
    private val radioApi: RadioApi = RetrofitInstance.createService("https://public.radio.co/", RadioApi::class.java)
    private val mixCloudApi: MixCloudApi = RetrofitInstance.createService("https://api.mixcloud.com/", MixCloudApi::class.java)

    fun getRadioInfo(): Call<RadioInfo> {
        return radioApi.getRadioInfo()
    }

    fun getPlaylist(): Call<MixCloudPlaylist> {
        return mixCloudApi.getPlaylist()
    }

    fun getCloudcast(key: String): Call<MixCloudCloudcast> {
        return mixCloudApi.getCloudcast(key)
    }
}
