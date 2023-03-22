package com.example.mat3_nav.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.mat3_nav.MainViewModelFactory
import com.example.mat3_nav.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    onLoginSuccess: (String) -> Unit // Add this line
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    var showLoadingPopup by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp, top = 66.dp, bottom = 66.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
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
                onClick = {
                    loginError = false
                    viewModel.authenticate(username, password) { setLoading ->
                        showLoadingPopup = setLoading
                    }
                }
            ) {
                Text("Login")
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
            onLoginSuccess(userId)
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



