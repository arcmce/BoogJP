package com.arcmce.boogjp.service

import android.content.Intent
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        Log.d("PlaybackService", "PlaybackService created")

        // Initialize the ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // Create a MediaItem using the URL you want to play
        val mediaItem = MediaItem.fromUri("https://streams.radio.co/sb88c742f0/listen")

        // Set the media item to the player
        player.setMediaItem(mediaItem)

        // Prepare the player to play the media
        player.prepare()

        // Optionally set playWhenReady to true to start playbacsk automatically
        player.playWhenReady = true

        Log.d("PlaybackService", "Player is set to play when ready: ${player.playWhenReady}")

        // Create the MediaSession and link it to the player
        mediaSession = MediaSession.Builder(this, player).build()

        Log.d("PlaybackService", "MediaSession created")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d("PlaybackService", "onGetSession called")
        return mediaSession
    }
}
