package com.aeris.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aeris.ui.screens.consent.ConsentScreen
import com.aeris.ui.screens.disclaimer.DisclaimerScreen
import com.aeris.ui.screens.emergency.EmergencyScreen
import com.aeris.ui.screens.home.HomeScreen
import com.aeris.ui.screens.profile.ProfileScreen
import com.aeris.ui.screens.protocols.ProtocolListScreen
import com.aeris.ui.screens.session.SessionScreen
import com.aeris.ui.screens.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Disclaimer.route) {
            DisclaimerScreen(
                onAccepted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Disclaimer.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProtocols = { navController.navigate(Screen.ProtocolList.route) },
                onNavigateToEmergency = { navController.navigate(Screen.Emergency.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSession = { protocolId ->
                    navController.navigate(Screen.Session.createRoute(protocolId))
                }
            )
        }
        composable(Screen.ProtocolList.route) {
            ProtocolListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { protocolId ->
                    navController.navigate(Screen.Session.createRoute(protocolId))
                },
                onNavigateToConsent = { protocolId ->
                    navController.navigate(Screen.Consent.createRoute(protocolId))
                }
            )
        }
        composable(
            route = Screen.Session.route,
            arguments = listOf(navArgument("protocolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val protocolId = backStackEntry.arguments?.getString("protocolId") ?: ""
            SessionScreen(
                protocolId = protocolId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Consent.route,
            arguments = listOf(navArgument("protocolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val protocolId = backStackEntry.arguments?.getString("protocolId") ?: ""
            ConsentScreen(
                protocolId = protocolId,
                onConsentGiven = {
                    navController.navigate(Screen.Session.createRoute(protocolId)) {
                        popUpTo(Screen.Consent.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Emergency.route) {
            EmergencyScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProtocols = { navController.navigate(Screen.ProtocolList.route) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
