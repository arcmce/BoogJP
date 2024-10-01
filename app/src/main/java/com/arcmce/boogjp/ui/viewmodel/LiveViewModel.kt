package com.arcmce.boogjp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.media3.common.MediaMetadata
import com.arcmce.boogjp.network.model.RadioInfo
import com.arcmce.boogjp.network.repository.Repository
import com.arcmce.boogjp.service.PlaybackService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiveViewModel(private val repository: Repository) : ViewModel() {
    var playbackService: PlaybackService? = null

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _artworkUrl = MutableLiveData<String?>()
    val artworkUrl: LiveData<String?> get() = _artworkUrl

    private val _mediaMetadata = MutableLiveData<MediaMetadata>()
    val mediaMetadata: LiveData<MediaMetadata> get() = _mediaMetadata

    fun onMetadataFetched(artist: String, artworkUri: Uri?) {
        val metadata = MediaMetadata.Builder()
            .setTitle("Boogaloo Radio")
            .setArtist(artist)
            .setArtworkUri(artworkUri)
            .build()
        _mediaMetadata.value = metadata
    }

    fun fetchRadioInfo() {
        viewModelScope.launch {
            val call = repository.getRadioInfo()
            call.enqueue(object : Callback<RadioInfo> {
                override fun onResponse(call: Call<RadioInfo>, response: Response<RadioInfo>) {
                    if (response.isSuccessful) {
                        _title.value = response.body()?.currentTrack?.title
                        _artworkUrl.value = response.body()?.currentTrack?.artworkUrlLarge

                        playbackService?.updateMetadataInPlayer(
                            "Boogaloo Radio",
                            _title.value ?: "Boogaloo Radio",
                            Uri.parse(_artworkUrl.value))

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
