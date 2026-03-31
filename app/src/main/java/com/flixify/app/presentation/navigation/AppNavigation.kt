package com.flixify.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flixify.app.presentation.auth.AuthViewModel
import com.flixify.app.presentation.auth.LoginScreen
import com.flixify.app.presentation.auth.RegisterScreen
import com.flixify.app.presentation.home.HomeScreen
import com.flixify.app.presentation.player.PlayerActivity

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Screens
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        
        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigateUp()
                },
                viewModel = authViewModel
            )
        }
        
        // Main Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigate = { screen ->
                    navController.navigate(screen.route)
                },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Player.createRoute(movieId, "movie"))
                },
                onSeriesClick = { seriesId ->
                    navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                },
                onLiveTvClick = {
                    navController.navigate(Screen.LiveTv.route)
                }
            )
        }
        
        composable(Screen.Movies.route) {
            // MoviesScreen - to be implemented
        }
        
        composable(Screen.Series.route) {
            // SeriesScreen - to be implemented
        }
        
        composable(
            route = Screen.SeriesDetail.route,
            arguments = listOf(
                navArgument("seriesId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val seriesId = backStackEntry.arguments?.getString("seriesId")
            // SeriesDetailScreen(seriesId = seriesId)
        }
        
        composable(Screen.LiveTv.route) {
            // LiveTvScreen - to be implemented
        }
        
        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("contentId") { type = NavType.StringType },
                navArgument("contentType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId")
            val contentType = backStackEntry.arguments?.getString("contentType")
            // Player is handled via PlayerActivity for better video playback
        }
        
        composable(Screen.Profile.route) {
            // ProfileScreen - to be implemented
        }
        
        composable(Screen.Packages.route) {
            // PackagesScreen - to be implemented
        }
        
        composable(Screen.Payments.route) {
            // PaymentsScreen - to be implemented
        }
    }
}

// Extension to check if user is authenticated
fun NavHostController.navigateToAuth() {
    navigate(Screen.Login.route) {
        popUpTo(0) { inclusive = true }
    }
}

fun NavHostController.navigateToHome() {
    navigate(Screen.Home.route) {
        popUpTo(0) { inclusive = true }
    }
}
