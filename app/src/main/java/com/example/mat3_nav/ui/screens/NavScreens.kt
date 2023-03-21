package com.example.mat3_nav.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavScreens(
    val route: String,
    val icon: ImageVector? = null,
) {
    object HomeScreen : NavScreens("Home", Icons.Default.Home)
    object DetailScreen : NavScreens("Details", Icons.Default.Favorite)

    object CreateProfileScreen : NavScreens("create_profile_screen")
    object LoginScreen : NavScreens("profile_screen")
}

