package com.arcmce.boogjp.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
//import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.arcmce.boogjp.R
import com.arcmce.boogjp.ui.view.MainActivity
import com.arcmce.boogjp.ui.viewmodel.LiveViewModel

class PlaybackService : MediaSessionService() {

    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var liveViewModel: LiveViewModel

    override fun onCreate() {
        super.onCreate()
        Log.d("PlaybackService", "PlaybackService created")

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        requestAudioFocus()

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


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
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

    private fun playexternal() {
        player.play()
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

    fun onAudioFocusChange(focusStatus: Int) {
        Log.d("PlaybackService", "onAudioFocusChange")

        when (focusStatus) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d("PlaybackService", "AUDIOFOCUS_GAIN")
                // Restore volume or start playback
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d("PlaybackService", "AUDIOFOCUS_LOSS")
                player.pause()
                // Optionally stop playback after a delay
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d("PlaybackService", "AUDIOFOCUS_LOSS_TRANSIENT")
                player.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d("PlaybackService", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                // Reduce volume if playing
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun requestAudioFocus(): Boolean {
        Log.d("PlaybackService", "requestAudioFocus")

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

        return audioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun removeAudioFocus(): Boolean {
        Log.d("PlaybackService", "removeAudioFocus")
        return audioManager.abandonAudioFocusRequest(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d("PlaybackService", "onGetSession called")
        return mediaSession
    }
}
