package com.arcmce.boogjp.ui.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arcmce.boogjp.ui.theme.BoogalooJetpackTheme


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









//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    YourAppTheme {
//        TestPlaybackServiceComposable(context = LocalContext.current)
//    }
//}
