package com.example.mat3_nav.ui.screens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.example.mat3_nav.ui.theme.quickSand

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
                .imePadding(),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Welcome",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = quickSand,
                    color = Color.White,
                    modifier = Modifier.zIndex(1f)
                )
                Text(
                    text = "Back",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = quickSand,
                    color = Color.White,
                    modifier = Modifier.zIndex(1f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))



            Spacer(modifier = Modifier.height(32.dp))

            CustomTextField2(
                icon = Icons.Filled.Email,
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Enter your username"
            )


            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField2(
                icon = Icons.Filled.Lock,
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                isPassword = true
            )


            Spacer(modifier = Modifier.height(16.dp))

            if (loginError) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 16.sp,
                    text = "Login failed. Please check your username and password.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006BFF),
                    contentColor = Color.White,
                ),
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
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            modifier = Modifier.size(16.dp),
                            contentDescription = "Arrow forward",
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

        }

    }

    LoadingPopup(
        isVisible = showLoadingPopup,
        errorMessage = null,
        onDismiss = {
            showLoadingPopup = false
        }
    )


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    icon: ImageVector? = null,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    isMultiLine: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    val focusRequester1 = remember { FocusRequester() }
    val isFocused = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val passwordVisibility = remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary


    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused.value = true
                is FocusInteraction.Unfocus -> isFocused.value = false
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Icon(
                icon,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(26.dp),
                contentDescription = "Login icon",
                tint = if (isFocused.value) primaryColor else Color.LightGray
            )
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = if (isMultiLine) {
                if (focusRequester != null) {
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .heightIn(min = 100.dp)
                } else {
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester1)
                        .heightIn(min = 100.dp)
                }
            } else {
                if (focusRequester != null) {
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                } else {
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester1)
                }
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = !isMultiLine,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
            visualTransformation = if (isPassword && !passwordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            interactionSource = interactionSource,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface
            ),
        )

        if (isPassword) {
            IconButton(
                modifier = Modifier
                    .padding(end = 4.dp),
                onClick = { passwordVisibility.value = !passwordVisibility.value }
            ) {
                Icon(
                    imageVector = if (passwordVisibility.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle password visibility",
                    tint = if (isFocused.value) primaryColor else Color.LightGray
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (isFocused.value) primaryColor else Color.LightGray)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField2(
    icon: ImageVector? = null,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    isMultiLine: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    val focusRequester1 = remember { FocusRequester() }
    val isFocused = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val passwordVisibility = remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused.value = true
                is FocusInteraction.Unfocus -> isFocused.value = false
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Icon(
                icon,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(26.dp),
                contentDescription = "Login icon",
                tint = if (isFocused.value) primaryColor else Color.LightGray
            )
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = if (isMultiLine) {
                if (focusRequester != null) {
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .heightIn(min = 100.dp)
                } else {
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester1)
                        .heightIn(min = 100.dp)
                }
            } else {
                if (focusRequester != null) {
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                } else {
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester1)
                }
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = !isMultiLine,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
            visualTransformation = if (isPassword && !passwordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            interactionSource = interactionSource,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface
            ),
        )

        if (isPassword) {
            IconButton(
                modifier = Modifier
                    .padding(end = 4.dp),
                onClick = { passwordVisibility.value = !passwordVisibility.value }
            ) {
                Icon(
                    imageVector = if (passwordVisibility.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle password visibility",
                    tint = if (isFocused.value) primaryColor else Color.LightGray
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (isFocused.value) primaryColor else Color.LightGray)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoSizableTextField(
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isMultiLine: Boolean = false,
    fontSize: TextUnit = 22.sp,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit,
    scaleFactor: Float = 0.9f,
    maxWords: Int = 256,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val isFocused = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused.value = true
                is FocusInteraction.Unfocus -> isFocused.value = false
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        var nFontSize = fontSize

        val calculateParagraph = @Composable {
            Paragraph(
                text = value,
                style = TextStyle(fontSize = nFontSize),
                density = LocalDensity.current,
                resourceLoader = LocalFontLoader.current,
                maxLines = maxLines,
                width = with(LocalDensity.current) { maxWidth.toPx() }
            )
        }
        var intrinsics = calculateParagraph()
        with(LocalDensity.current) {
            while ((intrinsics.height.toDp() > maxHeight || intrinsics.didExceedMaxLines) && nFontSize >= minFontSize) {
                nFontSize *= scaleFactor
                intrinsics = calculateParagraph()
            }
        }
        val adaptedFontSize = if (nFontSize < minFontSize) minFontSize else nFontSize
        val wordCount = value.split("\\s+".toRegex()).count { it.isNotEmpty() }

        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    icon,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(26.dp),
                    contentDescription = "TextField icon",
                    tint = if (isFocused.value) primaryColor else Color.LightGray
                )
                if (wordCount > 0) {
                    Text(
                        text = "$wordCount/$maxWords",
                        modifier = Modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (wordCount > maxWords) Color.Red else Color.LightGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    val newWordCount = newValue.split("\\s+".toRegex()).count { it.isNotEmpty() }
                    if (newWordCount <= maxWords) {
                        onValueChange(newValue)
                    }
                },
                modifier = if (isMultiLine) {
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .height(450.dp)
                        .fillMaxWidth()
                } else {
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .fillMaxWidth()
                },
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                singleLine = !isMultiLine,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.Transparent
                ),
                interactionSource = interactionSource,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = adaptedFontSize
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(if (isFocused.value) primaryColor else Color.LightGray)
            )
        }
    }
}








