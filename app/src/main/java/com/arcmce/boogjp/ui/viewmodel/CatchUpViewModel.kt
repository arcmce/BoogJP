package com.arcmce.boogjp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.arcmce.boogjp.network.model.CloudcastData
import com.arcmce.boogjp.network.model.MixCloudCloudcast
import com.arcmce.boogjp.network.model.MixCloudPlaylist
import com.arcmce.boogjp.network.model.RadioInfo
import com.arcmce.boogjp.network.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


data class CatchUpCardItem(
    val name: String,
    var thumbnail: String,
    val key: String,
//    val url: ArrayList<String>
)

class CatchUpViewModel(private val repository: Repository) : ViewModel() {

    private val _dataset = MutableLiveData<List<CatchUpCardItem>>()
    val dataset: LiveData<List<CatchUpCardItem>> get() = _dataset

    private val _cloudcastData = MutableStateFlow<Map<String, MixCloudCloudcast>>(emptyMap())
    val cloudcastData: StateFlow<Map<String, MixCloudCloudcast>> = _cloudcastData

    fun fetchPlaylist() {
        viewModelScope.launch {
            val call = repository.getPlaylist()
            call.enqueue(object : Callback<MixCloudPlaylist> {
                override fun onResponse(call: Call<MixCloudPlaylist>, response: Response<MixCloudPlaylist>) {
                    if (response.isSuccessful) {
                        val dataset = ArrayList<CatchUpCardItem>()

                        for (playlist in response.body()?.data!!) {
                            dataset.add(CatchUpCardItem(
                                name = playlist.name,
                                thumbnail = response.body()?.data!![0].owner.pictures.large,
                                key = playlist.key
//                                url = cloudcastUrlList
                            ))
                        }
                        _dataset.value = dataset
//                        _artworkUrl.value = response.body()?.currentTrack?.artworkUrlLarge
//                    } else {
//                        _artworkUrl.value = null
//                    }
                        Log.d("CatchUpViewModel", "fetchPlaylist success")
                    }
                }

                override fun onFailure(call: Call<MixCloudPlaylist>, t: Throwable) {
//                    _artworkUrl.value = null
                    Log.d("CatchUpViewModel", "fetchPlaylist fail")
                }
            })
        }
    }

    fun fetchCloudcastData(key: String) {
        viewModelScope.launch {
            val call = repository.getCloudcast(key)
            call.enqueue(object : Callback<MixCloudCloudcast> {
                override fun onResponse(call: Call<MixCloudCloudcast>, response: Response<MixCloudCloudcast>) {
                    if (response.isSuccessful) {

                        Log.d("CatchUpViewModel", "fetchCloudcastData success")
                    }
                }

                override fun onFailure(call: Call<MixCloudCloudcast>, t: Throwable) {

                    Log.d("CatchUpViewModel", "fetchCloudcastData fail")
                }
            })
        }
    }

    fun getCloudcastData(key: String): MixCloudCloudcast? {
        return _cloudcastData.value[key]
    }
}

class CatchUpViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatchUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatchUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
