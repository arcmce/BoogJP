package com.arcmce.boogjp.ui.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arcmce.boogjp.network.repository.Repository
import com.arcmce.boogjp.ui.theme.BoogalooJetpackTheme
import com.arcmce.boogjp.ui.viewmodel.CatchUpViewModel
import com.arcmce.boogjp.ui.viewmodel.CatchUpViewModelFactory
import com.arcmce.boogjp.ui.viewmodel.LiveViewModel
import com.arcmce.boogjp.ui.viewmodel.LiveViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val radioInfoViewModel: LiveViewModel by viewModels { LiveViewModelFactory(repository) }
        val catchUpViewModel: CatchUpViewModel by viewModels { CatchUpViewModelFactory(repository) }

        setContent {
            BoogalooJetpackTheme {
                AppContent(
                    radioInfoViewModel = radioInfoViewModel,
                    catchUpViewModel = catchUpViewModel,
                    context = this)
            }
        }

        startBackgroundCoroutine(radioInfoViewModel)
    }

    private fun startBackgroundCoroutine(radioInfoViewModel: LiveViewModel) {

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
    radioInfoViewModel: LiveViewModel,
    catchUpViewModel: CatchUpViewModel,
    context: Context,
) {
    val liveTab = TabBarItem(title = "Live", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
    val catchUpTab = TabBarItem(title = "CatchUp", selectedIcon = Icons.Filled.Notifications, unselectedIcon = Icons.Outlined.Notifications)

    // creating a list of all the tabs
    val tabBarItems = listOf(liveTab, catchUpTab)

    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                TabView(tabBarItems, navController)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Navigation host to switch between LiveView and CatchUpView
                NavHost(
                    navController = navController,
                    startDestination = liveTab.title,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(liveTab.title) { LiveView(context, radioInfoViewModel) }
                    composable(catchUpTab.title) { CatchUpView(catchUpViewModel) }
                }

                PlaybackControls(
                    context,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 56.dp)
                )
            }
        }
    }
}

// ----------------------------------------
// This is a wrapper view that allows us to easily and cleanly
// reuse this component in any future project
@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {Text(tabBarItem.title)})
        }
    }
}

// This component helps to clean up the API call from our TabView above,
// but could just as easily be added inside the TabView without creating this custom component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

// This component helps to clean up the API call from our TabBarIconView above,
// but could just as easily be added inside the TabBarIconView without creating this custom component
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}
// end of the reusable components that can be copied over to any new projects
// ----------------------------------------







//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    YourAppTheme {
//        TestPlaybackServiceComposable(context = LocalContext.current)
//    }
//}
