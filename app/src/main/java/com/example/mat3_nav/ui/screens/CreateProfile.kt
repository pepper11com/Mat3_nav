package com.example.mat3_nav.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mat3_nav.R
import com.example.mat3_nav.ui.theme.quickSand
import com.example.mat3_nav.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateProfileScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var profileDescription by remember { mutableStateOf("") }
    val createSuccess by viewModel.createSuccess.observeAsState(initial = false)
    if (createSuccess) {
        // Navigate to another screen after successful profile creation
        navController.navigate(NavScreens.LoginScreen.route)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .wavyClipPath(waveHeight = 200.dp, wavePosition = .6f)
                .background(Color(0xFF006BFF))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomStart)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = quickSand,
                    color = Color.White,
                    modifier = Modifier.zIndex(1f)
                )
                Text(
                    text = "Profile",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = quickSand,
                    color = Color.White,
                    modifier = Modifier.zIndex(1f)
                )
            }

            Spacer(modifier = Modifier.weight(3f))
            PickImageFromGallery(context, viewModel)
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
                icon = Icons.Filled.Lock,
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                isPassword = true
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
            CustomTextField(
                icon = Icons.Filled.Description,
                value = profileDescription,
                onValueChange = { profileDescription = it },
                label = "Profile Description",
                placeholder = "Enter your profile description"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006BFF),
                    contentColor = Color.White
                ),
                onClick = {
                    // CREATE PROFILE IF INPUT NOT EMPTY
                    if (firstName.isEmpty() || lastName.isEmpty() || profileDescription.isEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.fields_must_not_be_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val uriString: String
                        if (viewModel.imageUri == null) {
                            uriString = context.getString(R.string.no_gallery_image)
                        } else {
                            uriString = viewModel.imageUri.toString()
                        }
                        viewModel.reset()
                        viewModel.createProfile(
                            username = username,
                            password = password,
                            firstName = firstName,
                            lastName = lastName,
                            description = profileDescription,
                            imageUri = uriString
                        )
                        firstName = ""
                        lastName = ""
                        profileDescription = ""
                        navController.navigate(NavScreens.LoginScreen.route)
                    }
                }
            ) {
                Text(text = "Create Profile")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextButton(
                    onClick = {
                        navController.navigate(NavScreens.LoginScreen.route)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account?",
                            color = Color.LightGray.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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

            Spacer(modifier = Modifier.weight(1f))

        }
    }
}


@Composable
fun PickImageFromGallery(context: Context, viewModel: MainViewModel) {
    val avatars = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
        R.drawable.avatar_6,
        R.drawable.avatar_7,
        R.drawable.avatar_8,
        R.drawable.avatar_9,
        R.drawable.avatar_10,
        R.drawable.avatar_11,
        R.drawable.avatar_12,
        R.drawable.avatar_13,
        R.drawable.avatar_14,
        R.drawable.avatar_15,
        R.drawable.avatar_16
    )

    var drawableToString = { drawable: Int ->
        context.resources.getResourceEntryName(drawable)
    }

    var expanded by remember { mutableStateOf(false) }

    // jsut get the file name of the image and store it in the database as a string
    var selectedAvatar by remember { mutableStateOf(avatars[0]) }

    //doesnt have to be uri, can be string
    viewModel.imageUri = drawableToString(selectedAvatar)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = selectedAvatar),
            contentDescription = "Selected Avatar",
            modifier = Modifier
                .width(64.dp)
                .height(64.dp)
                .clip(CircleShape)
                .clickable(onClick = { expanded = !expanded })
        )
//        Button(onClick = { expanded = !expanded }) {
//            Text("Select Avatar")
//        }
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .padding(18.dp),
                shape = RoundedCornerShape(16.dp) // Add corner rounding
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(18.dp),
                    columns = GridCells.Fixed(4),
                ) {
                    //todo store the choses avatars file name in the database as a string so like avatar_1 or avatar_2 and dont make it a uri
                    items(avatars.size) { index ->
                        val avatar = avatars[index]
                        Column(
                            modifier = Modifier
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(id = avatar),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedAvatar = avatar
                                        viewModel.imageUri = drawableToString(avatar)
                                            .toString()
                                        expanded = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}





