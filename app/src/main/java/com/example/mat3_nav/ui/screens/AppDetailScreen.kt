package com.example.mat3_nav.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mat3_nav.viewmodel.MainViewModel
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.android.material.progressindicator.CircularProgressIndicator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDetailScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val currentProfile by viewModel.profile.observeAsState()


    Box(
        modifier = Modifier
            .padding(top = 64.dp, bottom = 56.dp)
            .fillMaxSize()
    ) {
        currentProfile?.let { profile ->
            var username by remember { mutableStateOf(profile.username) }
            var firstName by remember { mutableStateOf(profile.firstName) }
            var lastName by remember { mutableStateOf(profile.lastName) }
            var description by remember { mutableStateOf(profile.description) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        icon = Icons.Filled.Person,
                        value = username,
                        onValueChange = { username = it },
                        label = "Username",
                        placeholder = "Enter your username"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        icon = Icons.Filled.PersonOutline,
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First Name",
                        placeholder = "Enter your first name"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        icon = Icons.Filled.PersonOutline,
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last Name",
                        placeholder = "Enter your last name"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    description?.let { it1 ->
                        AutoSizableTextField(
                            value = it1,
                            onValueChange = { description = it },
                            maxLines = 10,
                            minFontSize = 10.sp,
                            modifier = Modifier
                                .padding(top = 20.dp, bottom = 20.dp)
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006BFF),
                            contentColor = Color.White
                        ),
                        onClick = {
                            // Handle save/update profile logic here
                        }
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}


