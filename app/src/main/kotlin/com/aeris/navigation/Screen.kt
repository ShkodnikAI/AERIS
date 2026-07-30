package com.aeris.navigation

sealed class Screen(val route: String) {
    object Disclaimer : Screen("disclaimer")
    object Consent : Screen("consent/{protocolId}") {
        fun createRoute(protocolId: String) = "consent/$protocolId"
    }
    object Home : Screen("home")
    object ProtocolList : Screen("protocols")
    object ProtocolDetail : Screen("protocol/{protocolId}") {
        fun createRoute(protocolId: String) = "protocol/$protocolId"
    }
    object Session : Screen("session/{protocolId}") {
        fun createRoute(protocolId: String) = "session/$protocolId"
    }
    object Emergency : Screen("emergency")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
