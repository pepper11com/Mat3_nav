package com.example.mat3_nav.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    secondary = Teal200
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


@Composable
fun Mat3_navTheme(
    isDynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colors = if (darkTheme) {
        if (dynamicColor) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            DarkColorPalette
        }
    } else {
        if (dynamicColor) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            LightColorPalette
        }
    }

//    SideEffect {
//        systemUiController.setSystemBarsColor(
//            color =  Color(0x86000000),
//            darkIcons = false
//        )
//    }


//    if(darkTheme){
//        systemUiController.setSystemBarsColor(
//            color = Color(0xC6000000)
//        )
//    }else{
//        systemUiController.setSystemBarsColor(
//            color =  Color(0xC6000000)
//        )
//    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}