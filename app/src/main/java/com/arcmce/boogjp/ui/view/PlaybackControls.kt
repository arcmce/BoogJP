package com.arcmce.boogjp.ui.view

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arcmce.boogjp.service.PlaybackService

@Composable
fun PlaybackControls(context: Context, modifier: Modifier = Modifier) {
    // State to track whether the player is playing or not
    var isPlaying by remember { mutableStateOf(false) }

    // Top-level layout
    Column(
        modifier = modifier,
//            .fillMaxSize()
//            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Static text
        Text(text = "Simple Playback Test", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Play/Pause button
        Button(onClick = {
            if (isPlaying) {
                // Stop playback
                stopPlaybackService(context)
            } else {
                // Start playback
                startPlaybackService(context)
            }
            isPlaying = !isPlaying // Toggle playback state
        }) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}

private fun startPlaybackService(context: Context) {
    val intent = Intent(context, PlaybackService::class.java)
    context.startService(intent)
}

private fun stopPlaybackService(context: Context) {
    val intent = Intent(context, PlaybackService::class.java)
    context.stopService(intent)
}