package com.arcmce.boogaloojetpack.ui.view

import com.arcmce.boogaloojetpack.ui.viewmodel.RadioInfoViewModel
import com.arcmce.boogaloojetpack.ui.viewmodel.RadioInfoViewModelFactory
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.arcmce.boogaloojetpack.network.api.RetrofitInstance
import com.arcmce.boogaloojetpack.network.repository.RadioRepository
import com.arcmce.boogaloojetpack.service.PlaybackService
import com.arcmce.boogaloojetpack.ui.theme.BoogalooJetpackTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoogalooJetpackTheme {
                AppContent(context = this)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    context: Context,
) {

    val navController = rememberNavController()
    val tabs = listOf("Live", "Catch Up")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            navController.navigate(if (index == 0) "liveView" else "catchUpView")
                        },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Navigation host to switch between LiveView and CatchUpView
        NavHost(
            navController = navController,
            startDestination = "liveView",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("liveView") { LiveView(context) }
            composable("catchUpView") { CatchUpView() }
        }
        PlaybackControls(context)
    }
}

@Composable
fun LiveView(
    context: Context,
    viewModel: RadioInfoViewModel = viewModel(factory = RadioInfoViewModelFactory(RadioRepository(RetrofitInstance.getRadioAPI())))
) {

    // Fetch data when the composable is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchRadioInfo()
    }

    // Observe the artwork URL from the ViewModel
    val artworkUrl by viewModel.artworkUrl.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display image if artworkUrl is available
        artworkUrl?.let { url ->
            if (url.isNotEmpty()) {
                AsyncImage(
                    model = url,
                    contentDescription = "Current show artwork",
                    modifier = Modifier.size(200.dp),
//                    contentScale = ContentScale.Crop
                )
            } else {
                Text(text = "No artwork available")
            }
        } ?: Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CatchUpView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Catch Up View Content")
    }
}


@Composable
fun PlaybackControls(context: Context) {
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
