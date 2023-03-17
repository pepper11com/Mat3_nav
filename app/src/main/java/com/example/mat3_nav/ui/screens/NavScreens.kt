package com.example.mat3_nav.ui.screens

sealed class NavScreens(
    val route: String
) {
    object HomeScreen : NavScreens("home_screen")
    object DetailScreen : NavScreens("detail_screen")
}

