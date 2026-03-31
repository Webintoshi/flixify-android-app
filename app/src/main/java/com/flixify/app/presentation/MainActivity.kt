package com.flixify.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flixify.app.presentation.common.theme.Accent
import com.flixify.app.presentation.common.theme.Background
import com.flixify.app.presentation.common.theme.FlixifyTheme
import com.flixify.app.presentation.common.theme.Surface
import com.flixify.app.presentation.common.theme.TextMuted
import com.flixify.app.presentation.common.theme.TextPrimary
import com.flixify.app.presentation.home.HomeScreen
import com.flixify.app.presentation.movies.MoviesScreen
import com.flixify.app.presentation.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlixifyTheme {
                FlixifyApp()
            }
        }
    }
}

@Composable
fun FlixifyApp() {
    val navController = rememberNavController()
    
    val bottomNavItems = listOf(
        BottomNavItem("Ana Sayfa", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Filmler", Icons.Default.Movie, Screen.Movies.route),
        BottomNavItem("Diziler", Icons.Default.Tv, Screen.Series.route),
        BottomNavItem("Canlı TV", Icons.Default.LiveTv, Screen.LiveTv.route),
        BottomNavItem("Profil", Icons.Default.Person, Screen.Profile.route)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Surface,
                contentColor = TextPrimary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { 
                        it.route == item.route 
                    } == true
                    
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Accent,
                            selectedTextColor = Accent,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                MoviesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.Player.createRoute(movieId, "movie"))
                    }
                )
            }
            
            composable(Screen.Series.route) {
                // SeriesScreen()
            }
            
            composable(Screen.SeriesDetail.route) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getString("seriesId")
                // SeriesDetailScreen(seriesId = seriesId)
            }
            
            composable(Screen.LiveTv.route) {
                // LiveTvScreen()
            }
            
            composable(Screen.Player.route) { backStackEntry ->
                val contentId = backStackEntry.arguments?.getString("contentId")
                val contentType = backStackEntry.arguments?.getString("contentType")
                // Player handled separately
            }
            
            composable(Screen.Profile.route) {
                // ProfileScreen()
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
