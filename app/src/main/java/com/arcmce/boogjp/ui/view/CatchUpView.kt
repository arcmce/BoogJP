package com.arcmce.boogjp.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcmce.boogjp.ui.viewmodel.CatchUpCardItem
import com.arcmce.boogjp.ui.viewmodel.CatchUpViewModel
import com.arcmce.boogjp.ui.viewmodel.LiveViewModel

@Composable
fun CatchUpView(
    viewModel: CatchUpViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.fetchPlaylist()
    }

    // Observe the artwork URL from the ViewModel
    val cardItems by viewModel.dataset.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CatchUpVerticalGrid(cardItems)
    }
}

@Composable
fun CatchUpVerticalGrid(items: List<CatchUpCardItem>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            CardItemView(item)
        }
    }
}
//
//LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
//    itemsIndexed(playlistItems) { index, item ->
//        LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
//            if (index == listState.firstVisibleItemIndex) {
//                catchUpViewModel.fetchCloudcastData(item.key)
//            }
//        }
//
//        val thumbnailUrl = catchUpViewModel.getCloudcastThumbnail(item.key) ?: item.genericThumbnailUrl
//        Image(
//            painter = rememberImagePainter(thumbnailUrl),
//            contentDescription = null
//        )
//        Text(item.title)
//    }
//}

@Composable
fun CardItemView(item: CatchUpCardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Load image using Coil
            AsyncImage(
                model = item.thumbnail,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}