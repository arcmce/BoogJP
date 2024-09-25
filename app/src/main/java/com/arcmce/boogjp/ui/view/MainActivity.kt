package com.arcmce.boogjp.ui.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arcmce.boogjp.network.api.RetrofitInstance
import com.arcmce.boogjp.network.repository.Repository
import com.arcmce.boogjp.ui.theme.BoogalooJetpackTheme
import com.arcmce.boogjp.ui.viewmodel.RadioInfoViewModel
import com.arcmce.boogjp.ui.viewmodel.RadioInfoViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

//    private lateinit var radioInfoViewModel: RadioInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val radioInfoViewModel: RadioInfoViewModel by viewModels { RadioInfoViewModelFactory(repository) }

        setContent {
            BoogalooJetpackTheme {
                AppContent(radioInfoViewModel = radioInfoViewModel, context = this)
            }
        }

        startBackgroundCoroutine(radioInfoViewModel)
    }

    private fun startBackgroundCoroutine(radioInfoViewModel: RadioInfoViewModel) {

        val context = this

        val scope = CoroutineScope(Dispatchers.Default)

        // Start a coroutine that runs every 10 seconds
        scope.launch {
            while (true) {
                // Update data in the ViewModel or any other relevant logic
                radioInfoViewModel.fetchRadioInfo()

                // Delay for 60 seconds
                delay(10_000)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    radioInfoViewModel: RadioInfoViewModel,
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
            composable("liveView") { LiveView(context, radioInfoViewModel) }
            composable("catchUpView") { CatchUpView() }
        }
        PlaybackControls(context)
    }
}

class MainViewModel : ViewModel() {
    var currentTabIndex = mutableStateOf(0)
    val pagerState = rememberPagerState()
}








//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    YourAppTheme {
//        TestPlaybackServiceComposable(context = LocalContext.current)
//    }
//}
