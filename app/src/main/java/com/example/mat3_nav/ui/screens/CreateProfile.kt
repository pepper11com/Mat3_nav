package com.example.mat3_nav.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mat3_nav.R
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 66.dp, bottom = 66.dp)
        ,
        content = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PickImageFromGallery(context, viewModel)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Add image picker and other UI elements if necessary

                    TextField(
                        value = username,
                        placeholder = { Text(text = "Username") },
                        onValueChange = { username = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Username") }
                    )
                    TextField(
                        value = password,
                        placeholder = { Text(text = "Password") },
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    TextField(
                        value = firstName,
                        placeholder = { Text(text = "First Name") },
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "First Name") }
                    )
                    TextField(
                        value = lastName,
                        placeholder = { Text(text = "Last Name") },
                        onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Last Name") }
                    )
                    TextField(
                        value = profileDescription,
                        placeholder = { Text(text = "Profile Description") },
                        onValueChange = { profileDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Profile Description") }
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
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
            }

        }
    )
}


@Composable
fun PickImageFromGallery(context: Context, viewModel: MainViewModel) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.imageUri = uri
    }

    if (viewModel.imageUri != null) {
        // https://stackoverflow.com/questions/58903911/how-to-fix-deprecated-issue-in-android-bitmap
        viewModel.bitmap.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val src = ImageDecoder.createSource(context.contentResolver, viewModel.imageUri!!)
            ImageDecoder.decodeBitmap(src)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, viewModel.imageUri)
        }
        viewModel.bitmap.value?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 40.dp)
                    .width(128.dp)
                    .height(128.dp)
            )
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.baseline_account_box_24),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 40.dp)
                .width(128.dp)
                .height(128.dp)
        )
    }
    androidx.compose.material.Button(
        onClick = { launcher.launch("image/*") },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colors.onPrimary,
            backgroundColor = Color.Black
        )
    )
    {
        androidx.compose.material.Text(
            text = context.getString(R.string.open_picture_gallery).uppercase()
        )
    }
}



