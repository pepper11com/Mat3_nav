package com.example.mat3_nav.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mat3_nav.R
import com.example.mat3_nav.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.lang.Float.max

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
            ,
        containerColor = Color.Transparent,
        content = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PickImageFromGallery(context, viewModel)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Add image picker and other UI elements if necessary

                    OutlinedTextField(
                        value = username,
                        placeholder = { Text(text = "Username") },
                        onValueChange = { username = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Username") }
                    )
                    OutlinedTextField(
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
                    OutlinedTextField(
                        value = firstName,
                        placeholder = { Text(text = "First Name") },
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "First Name") }
                    )
                    OutlinedTextField(
                        value = lastName,
                        placeholder = { Text(text = "Last Name") },
                        onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Last Name") }
                    )
                    OutlinedTextField(
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

        }
    )
}


@Composable
fun PickImageFromGallery(context: Context, viewModel: MainViewModel) {
    val minScale = remember { mutableStateOf(1f) }
    val maxScale = remember { mutableStateOf(3f) }
    val scale = remember { mutableStateOf(1f) }
    val imageSize = 150.dp
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.imageUri = uri
    }

    LaunchedEffect(viewModel.imageUri) {
        if (viewModel.imageUri != null) {
            viewModel.bitmap.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val src = ImageDecoder.createSource(context.contentResolver, viewModel.imageUri!!)
                ImageDecoder.decodeBitmap(src)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, viewModel.imageUri)
            }
        }
    }

    if (viewModel.imageUri != null) {
        // https://stackoverflow.com/questions/58903911/how-to-fix-deprecated-issue-in-android-bitmap
        viewModel.bitmap.value?.let { btm ->
            // Calculate the minimum scale to always fill the circle
            minScale.value = max(imageSize.value / btm.width, imageSize.value / btm.height)


            Box(
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 40.dp)
                    .size(imageSize)
                    .clip(CircleShape) // Clip the box to a circle
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            // Apply the pan and zoom while maintaining a minimum scale and maximum scale
                            scale.value =
                                (scale.value * zoom).coerceIn(minScale.value, maxScale.value)

                            // Limit pan offset
                            val maxOffsetX = (btm.width * scale.value - imageSize.value) / 40
                            val maxOffsetY = (btm.height * scale.value - imageSize.value) / 40
                            offsetX.value =
                                (offsetX.value + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                            offsetY.value =
                                (offsetY.value + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                        }
                    }
            ) {
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .graphicsLayer(
                            translationX = offsetX.value,
                            translationY = offsetY.value,
                            scaleX = scale.value,
                            scaleY = scale.value,
                            transformOrigin = TransformOrigin(0.5f, 0.5f)
                        )
                )
            }
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
    Button(
        onClick = { launcher.launch("image/*") },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = Color.Black
        )
    )
    {
        Text(
            text = context.getString(R.string.open_picture_gallery).uppercase()
        )
    }
}



