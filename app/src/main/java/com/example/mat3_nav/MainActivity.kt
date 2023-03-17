package com.example.mat3_nav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.example.mat3_nav.ui.screens.AppHomeScreen
import com.example.mat3_nav.ui.theme.Mat3_navTheme
import com.example.mat3_nav.ui.screens.NavScreens
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDarkTheme = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

        setContent {
            Mat3_navTheme(
                darkTheme = isDarkTheme
            ) {

//                WindowCompat.setDecorFitsSystemWindows(window, false)
//
//                window.statusBarColor = Color.Transparent.toArgb()

                NavBarApp()

            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavBarApp() {
    val navController = rememberAnimatedNavController()

    Scaffold(

    ) { innerPadding ->
        NavHostScreen(navController, innerPadding)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NavHostScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    AnimatedNavHost(
        navController,
        startDestination = NavScreens.HomeScreen.route,
        Modifier.padding(innerPadding)
    ) {
        composable(NavScreens.HomeScreen.route) {
            AppHomeScreen(
                navController = navController
            )
        }
//        composable(NavScreens.DetailScreen.route) {
//            AppDetailScreen(
//                navController = navController
//            )
//
//        }
    }
}

