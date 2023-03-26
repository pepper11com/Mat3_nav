package com.example.mat3_nav.ui.screens

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PasswordResetScreen(
    token: String
) {
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Reset Password")
        OutlinedTextField(
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            label = { Text("New Password") },
            modifier = Modifier.padding(top = 16.dp)
        )
        OutlinedTextField(
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            onClick = {
                if (newPassword.value == confirmPassword.value) {
                    // Perform password reset action using the token and new password
                } else {
                    // Show an error message, such as a Snackbar or Toast, indicating that the passwords do not match
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Reset Password")
        }
    }
}
