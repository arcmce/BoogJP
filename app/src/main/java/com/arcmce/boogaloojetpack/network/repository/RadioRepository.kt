package com.arcmce.boogaloojetpack.network.repository

import com.arcmce.boogaloojetpack.network.api.RadioApi
import com.arcmce.boogaloojetpack.network.model.RadioInfo
import retrofit2.Call

class RadioRepository(private val radioApi: RadioApi) {
    fun getRadioInfo(): Call<RadioInfo> {
        return radioApi.getRadioInfo()
    }
}
