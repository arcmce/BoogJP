package com.arcmce.boogjp.network.repository

import com.arcmce.boogjp.network.api.RadioApi
import com.arcmce.boogjp.network.model.RadioInfo
import retrofit2.Call

class RadioRepository(private val radioApi: RadioApi) {
    fun getRadioInfo(): Call<RadioInfo> {
        return radioApi.getRadioInfo()
    }
}
