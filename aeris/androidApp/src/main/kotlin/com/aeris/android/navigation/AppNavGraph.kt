package com.aeris.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aeris.android.ui.screens.HomeScreen
import com.aeris.android.ui.screens.ProtocolListScreen
import com.aeris.android.ui.screens.SessionScreen
import com.aeris.android.ui.screens.ProfileScreen

/**
 * Navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val PROTOCOLS = "protocols"
    const val SESSION = "session/{protocolId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val SAFETY = "safety"
    
    fun sessionRoute(protocolId: String) = "session/$protocolId"
}

/**
 * Main navigation graph for the app.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToProtocols = { navController.navigate(Routes.PROTOCOLS) },
                onNavigateToSession = { protocolId -> 
                    navController.navigate(Routes.sessionRoute(protocolId))
                },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        
        composable(Routes.PROTOCOLS) {
            ProtocolListScreen(
                onProtocolSelected = { protocolId ->
                    navController.navigate(Routes.sessionRoute(protocolId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Routes.SESSION,
            arguments = listOf(
                navArgument("protocolId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val protocolId = backStackEntry.arguments?.getString("protocolId") ?: ""
            SessionScreen(
                protocolId = protocolId,
                onNavigateBack = { navController.popBackStack() },
                onSessionComplete = { navController.popBackStack() }
            )
        }
        
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
