package com.example.mat3_nav.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mat3_nav.R


val quickSand = FontFamily(
    Font(R.font.quicksand_regular),
    Font(R.font.quicksand_bold, weight = FontWeight.Bold),
)

// Set of Material typography styles to start with
val Typography = Typography(

    displayLarge = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleMedium = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    titleSmall = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = quickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
)

