package com.flixify.app.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Movies : Screen("movies")
    object Series : Screen("series")
    object SeriesDetail : Screen("series_detail/{seriesId}") {
        fun createRoute(seriesId: String) = "series_detail/$seriesId"
    }
    object LiveTV : Screen("live_tv")
    object Profile : Screen("profile")
    object Packages : Screen("packages")
    object Payments : Screen("payments")
    object Contact : Screen("contact")
    object Settings : Screen("settings")
}

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val CATALOG_ROUTE = "catalog"
    const val LIVE_ROUTE = "live"
    const val PROFILE_ROUTE = "profile"
}
