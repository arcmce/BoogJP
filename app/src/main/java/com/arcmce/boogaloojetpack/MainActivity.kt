package com.arcmce.boogaloojetpack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arcmce.boogaloojetpack.PlaybackService
import com.arcmce.boogaloojetpack.ui.theme.BoogalooJetpackTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoogalooJetpackTheme {

                TestPlaybackServiceComposable(context = this)
            }
        }
    }
}

@Composable
fun TestPlaybackServiceComposable(context: Context) {
    // State to track whether the player is playing or not
    var isPlaying by remember { mutableStateOf(false) }

    // Top-level layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
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

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    YourAppTheme {
//        TestPlaybackServiceComposable(context = LocalContext.current)
//    }
//}
