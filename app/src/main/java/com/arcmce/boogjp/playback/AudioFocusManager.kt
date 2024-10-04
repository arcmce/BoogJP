package com.arcmce.boogjp.playback

import com.google.common.util.concurrent.MoreExecutors
import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken


class AudioFocusManager(private val context: Context) {
    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var audioFocusRequest: AudioFocusRequest? = null

    private lateinit var player: Player

    init {
        getPlayer()
    }

    fun getPlayer() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({player = controllerFuture.get()},
            MoreExecutors.directExecutor()
        )

    }

    fun onAudioFocusChange(focusStatus: Int) {
        Log.d("com.arcmce.boogjp.playback.AudioFocusManager", "onAudioFocusChange")

        when (focusStatus) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d("com.arcmce.boogjp.playback.AudioFocusManager", "AUDIOFOCUS_GAIN")
                // Restore volume or start playback
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d("com.arcmce.boogjp.playback.AudioFocusManager", "AUDIOFOCUS_LOSS")
                player.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d("com.arcmce.boogjp.playback.AudioFocusManager", "AUDIOFOCUS_LOSS_TRANSIENT")
                player.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d("com.arcmce.boogjp.playback.AudioFocusManager", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                // Reduce volume if playing
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun requestAudioFocus(): Boolean {
        Log.d("com.arcmce.boogjp.playback.AudioFocusManager", "requestAudioFocus")

        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener { focusChange ->
                onAudioFocusChange(focusChange)
            }
            .build()

        return audioFocusRequest?.let { request ->
            audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } ?: false
    }

    fun abandonAudioFocus() {
        audioFocusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
        }
    }
}