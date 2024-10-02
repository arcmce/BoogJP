package com.arcmce.boogjp.ui.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.arcmce.boogjp.service.PlaybackService
import com.arcmce.boogjp.ui.viewmodel.SharedViewModel
import com.arcmce.boogjp.util.AppConstants
import com.google.common.util.concurrent.MoreExecutors
import java.lang.Thread.sleep

@Composable
fun PlaybackControls(context: Context, sharedViewModel: SharedViewModel, modifier: Modifier = Modifier) {

    lateinit var player: Player

    // State to track whether the player is playing or not
    var isPlaying by remember { mutableStateOf(false) }

    val title by sharedViewModel.liveTitle.observeAsState()

    // TODO make into floating (looking) object
    // TODO actually play pause logo not button

    Log.d("PlaybackControls", "pre sessiontoken")
    val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    Log.d("PlaybackControls", "pre controllerFuture build")
    val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    controllerFuture.addListener(
        {
            Log.d("PlaybackControls", "pre controllerfuture get")
            player = controllerFuture.get()

//            player
            player.addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d("PlaybackControls", "update playback state callback")
                        updatePlaybackState(isPlaying)
                    }

                    private fun updatePlaybackState(playing: Boolean) {
                        isPlaying = playing
                    }
                }
            )
        },
        MoreExecutors.directExecutor()
    )



    // Top-level layout
    Column(
        modifier = modifier,
//            .fillMaxSize()
//            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Static text
        Text(text = title ?: "Boogaloo Radio", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Play/Pause button
        Button(onClick = {
            if (isPlaying) {
//                stopPlaybackService(context)
                player.pause()
                Log.d("PlaybackControls", "Stopping playback")
            } else {
//                startPlaybackService(context)
                player.seekToDefaultPosition()
                player.play()
                Log.d("PlaybackControls", "Starting playback")
            }
            isPlaying = !isPlaying // Toggle playback state
        }) {
            Text(if (isPlaying) "Pause" else "Play")
        }

//        Button(onClick = {
//            val mediaItem = MediaItem.Builder()
//                .setUri(AppConstants.RADIO_STREAM_URL)
//                .setMediaMetadata(
//                    MediaMetadata.Builder()
//                        .setTitle("")
//                        .setArtist(title)
////                            .setArtworkUri()
//                        .build()
//                )
//                .build()
//
//            // Update the player with the new media item
//            player.replaceMediaItem(0, mediaItem)
//        }) {
//            Text("update")
//        }
//
//        Button(onClick = {
//            player.seekToDefaultPosition()
//            player.play()
////            player.seekBack()
//        }) {
//            Text("play from live")
//        }
//
//        Button(onClick = {
//            Log.d("PlaybackControls", "${player.totalBufferedDuration}")
//            player.seekForward()
//            Log.d("PlaybackControls", "${player.totalBufferedDuration}")
////            player.seekBack()
//        }) {
//            Text("misc")
//        }




    }
}

private fun startPlaybackService(context: Context) {
    val intent = Intent(context, PlaybackService::class.java)
    context.startService(intent)
    Log.d("PlaybackService", "playback service started")
}

private fun stopPlaybackService(context: Context) {
    val intent = Intent(context, PlaybackService::class.java)
    context.stopService(intent)
}