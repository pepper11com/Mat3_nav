package com.example.mat3_nav.ui.screens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.mat3_nav.viewmodel.MainViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    onLoginSuccess: (context: Context, userId: String) -> Unit
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    var showLoadingPopup by remember { mutableStateOf(false) }

    val authenticationResult by viewModel.authenticationResult.observeAsState()
    var failedAttempts by remember { mutableStateOf(0) }


    LaunchedEffect(authenticationResult, failedAttempts) {
        when (authenticationResult) {
            true -> showLoadingPopup = false
            false -> {
                showLoadingPopup = false
                loginError = true
            }
            else -> {
                // Do nothing
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .wavyClipPath(waveHeight = 200.dp, wavePosition = 0.4f)
                .background(Color(0xFF006BFF))
                .zIndex(1f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 66.dp, bottom = 66.dp, start = 46.dp, end = 46.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Login",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(top = 66.dp, bottom = 66.dp, start = 46.dp, end = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {


                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    placeholder = { Text("Enter your username") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (loginError) {
                    Text(
                        text = "Login failed. Please check your username and password.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = {
                        loginError = false
                        viewModel.authenticate(username, password) { setLoading ->
                            showLoadingPopup = setLoading
                        }
                        if (authenticationResult == false) {
                            failedAttempts++
                        }
                    }
                ) {
                    Text("Login")
                }

                TextButton(
                    onClick = {
                        navController.navigate(NavScreens.CreateProfileScreen.route)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            color = Color.LightGray.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign up",
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Add some space between text and icon
                        Icon(
                            Icons.Filled.ArrowForward,
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .size(14.dp),
                            contentDescription = "Login icon",
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
        LoadingPopup(
            isVisible = showLoadingPopup,
            errorMessage = if (loginError) "Login failed. Please check your username and password." else null,
            onDismiss = { showLoadingPopup = false }
        )
    }

    LaunchedEffect(key1 = viewModel.userId.value) {
        viewModel.userId.value?.let { userId ->
            onLoginSuccess(context, userId)
            navController.navigate(NavScreens.HomeScreen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}

@Composable
fun LoadingPopup(
    isVisible: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            if (errorMessage == null) {
                CircularProgressIndicator()
            } else {
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = onDismiss) {
                            Text("OK")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

fun Modifier.wavyClipPath(
    waveHeight: Dp,
    wavePosition: Float
): Modifier = clip(object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val waveHeightPx = with(density) { waveHeight.toPx() }

            moveTo(0f, 0f)
            lineTo(0f, height * wavePosition)
            cubicTo(
                width / 4f, height * wavePosition - waveHeightPx / 2,
                width / 4 * 3f, height * wavePosition + waveHeightPx / 2,
                width, height * wavePosition
            )
            lineTo(width, 0f)
            close()
        }

        return Outline.Generic(path)
    }
})




