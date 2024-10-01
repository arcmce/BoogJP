package com.arcmce.boogjp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.arcmce.boogjp.R
import com.arcmce.boogjp.ui.view.MainActivity
import com.arcmce.boogjp.ui.viewmodel.LiveViewModel

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var liveViewModel: LiveViewModel

    override fun onCreate() {
        super.onCreate()
        Log.d("PlaybackService", "PlaybackService created")

        // TODO track playback state for reload and for notification

        // Create notification channel if targeting Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "media_playback_channel",  // Channel ID
                "Media Playback",          // Channel name
                NotificationManager.IMPORTANCE_LOW  // Low importance so it's not intrusive
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

//        liveViewModel.mediaMetadata.observeForever { metadata ->
//            val mediaItem = MediaItem.Builder()
//                .setMediaMetadata(metadata)
//                .build()
//
//            player.setMediaItem(mediaItem)
//            player.prepare()
//        }

        // Initialize the ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // Create a MediaItem with metadata
        val mediaItem = MediaItem.Builder()
            .setUri("https://streams.radio.co/sb88c742f0/listen")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Boogaloo Radio")
                    .setArtist("Boogaloo Radio")
                    .build()
            )
            .build()

        // Set the media item to the player
        player.setMediaItem(mediaItem)

        // Prepare the player to play the media
        player.prepare()

//        // Optionally set playWhenReady to true to start playbacsk automatically
//        player.playWhenReady = true

//        Log.d("PlaybackService", "Player is set to play when ready: ${player.playWhenReady}")

        // Create a PendingIntent for launching the app when the notification is tapped
        val sessionActivityPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Create the MediaSession and link it to the player
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        Log.d("PlaybackService", "MediaSession created")

        val notification = playerNotification()
        startForeground(1, notification)
    }

    fun updateMetadataInPlayer(title: String, artist: String, artworkUri: Uri?) {
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()

        // Update the player with the new media item
        player.setMediaItem(mediaItem)
        player.prepare()  // Prepare and start playback if needed
    }

    private fun playerNotification(): Notification {
        return NotificationCompat.Builder(this, "media_playback_channel")
            .setContentTitle("Playing live stream")
            .setContentText("Boogaloo Radio")
            .setSmallIcon(R.drawable.play_arrow)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionCompatToken)
            )
            .build()
    }

    private fun updateMediaMetadata(artist: String, artworkUri: Uri?) {
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(artist)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()

        player.setMediaItem(mediaItem)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (player.playWhenReady) {
            // Make sure the service is not in foreground.
            player.pause()
        }
        stopSelf()
    }

    override fun onDestroy() {
        Log.d("PlaybackService", "onDestroy called")
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
