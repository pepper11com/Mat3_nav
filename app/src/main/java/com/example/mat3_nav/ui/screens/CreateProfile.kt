package com.example.mat3_nav.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mat3_nav.R
import com.example.mat3_nav.ui.theme.quickSand
import com.example.mat3_nav.viewmodel.MainViewModel
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
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
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

            Spacer(modifier = Modifier.weight(1f))

        }
    }
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









