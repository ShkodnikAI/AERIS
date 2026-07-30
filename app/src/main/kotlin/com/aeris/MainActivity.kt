package com.aeris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aeris.data.datastore.SettingsManager
import com.aeris.navigation.AppNavGraph
import com.aeris.navigation.Screen
import com.aeris.ui.theme.AerisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by settingsManager.theme.collectAsState(initial = "system")
            val darkTheme = when (theme) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            AerisTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AerisApp()
                }
            }
        }
    }
}

@Composable
fun AerisApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomScreens = listOf(
        Screen.Home to Icons.Default.Home,
        Screen.ProtocolList to Icons.Default.List,
        Screen.Emergency to Icons.Default.Warning,
        Screen.Profile to Icons.Default.Person
    )

    val showBottomBar = bottomScreens.any { it.first.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomScreens.forEach { (screen, icon) ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(stringResource(id = getNavLabel(screen))) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}

fun getNavLabel(screen: Screen): Int {
    return when (screen) {
        is Screen.Home -> R.string.nav_home
        is Screen.ProtocolList -> R.string.nav_protocols
        is Screen.Emergency -> R.string.nav_emergency
        is Screen.Profile -> R.string.nav_profile
        else -> R.string.app_name
    }
}
