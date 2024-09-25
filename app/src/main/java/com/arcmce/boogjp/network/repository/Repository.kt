package com.arcmce.boogjp.network.repository

import com.arcmce.boogjp.network.api.MixCloudApi
import com.arcmce.boogjp.network.api.RadioApi
import com.arcmce.boogjp.network.api.RetrofitInstance
import com.arcmce.boogjp.network.model.RadioInfo
import retrofit2.Call

class Repository() {
    private val radioApi: RadioApi = RetrofitInstance.createService("https://public.radio.co/", RadioApi::class.java)
    private val mixCloudApi: MixCloudApi = RetrofitInstance.createService("https://api.mixcloud.com/", MixCloudApi::class.java)

    fun getRadioInfo(): Call<RadioInfo> {
        return radioApi.getRadioInfo()
    }
}
