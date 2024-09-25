package com.arcmce.boogjp.ui.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcmce.boogjp.ui.viewmodel.RadioInfoViewModel

@Composable
fun LiveView(
    context: Context,
    viewModel: RadioInfoViewModel
) {

//    // Fetch data when the composable is first shown
//    LaunchedEffect(Unit) {
//        viewModel.fetchRadioInfo()
//    }

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
//                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(text = "No artwork available")
            }
        } ?: Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
    }
}