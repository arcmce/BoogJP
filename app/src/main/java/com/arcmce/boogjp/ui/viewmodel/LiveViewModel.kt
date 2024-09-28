package com.arcmce.boogjp.ui.viewmodel

import androidx.lifecycle.*
import com.arcmce.boogjp.network.model.RadioInfo
import com.arcmce.boogjp.network.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiveViewModel(private val repository: Repository) : ViewModel() {

    private val _artworkUrl = MutableLiveData<String?>()
    val artworkUrl: LiveData<String?> get() = _artworkUrl

    fun fetchRadioInfo() {
        viewModelScope.launch {
            val call = repository.getRadioInfo()
            call.enqueue(object : Callback<RadioInfo> {
                override fun onResponse(call: Call<RadioInfo>, response: Response<RadioInfo>) {
                    if (response.isSuccessful) {
                        _artworkUrl.value = response.body()?.currentTrack?.artworkUrlLarge
                    } else {
                        _artworkUrl.value = null
                    }
                }

                override fun onFailure(call: Call<RadioInfo>, t: Throwable) {
                    _artworkUrl.value = null
                }
            })
        }
    }
}

class LiveViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LiveViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
