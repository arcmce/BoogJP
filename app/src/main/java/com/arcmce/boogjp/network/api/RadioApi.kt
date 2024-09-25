package com.arcmce.boogjp.network.api

import retrofit2.http.GET
import retrofit2.Call
import com.arcmce.boogjp.network.model.RadioInfo

interface RadioApi {
    @GET("stations/sb88c742f0/status")
    fun getRadioInfo(): Call<RadioInfo>
}