package com.example.mat3_nav.ui.screens


import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mat3_nav.model.Profile
import com.example.mat3_nav.util.PasswordUtils
import com.example.mat3_nav.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppDetailScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val currentProfile by viewModel.profile.observeAsState()

    val isSaving = remember { mutableStateOf(false) }
    val isConfirmed = remember { mutableStateOf(false) }

    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val showConfirmPasswordDialog = remember { mutableStateOf(false) }

    ErrorDialog(
        showDialog = showErrorDialog.value,
        errorMessage = errorMessage.value ?: "",
        onDismissRequest = { showErrorDialog.value = false }
    )

    fun showSaveAnimation() {
        viewModel.viewModelScope.launch {
            isSaving.value = true
            delay(1500)
            isConfirmed.value = true
            delay(1000)
            isSaving.value = false
            isConfirmed.value = false
        }
    }

    Box(
        modifier = Modifier
            .padding(top = 64.dp, bottom = 56.dp)
            .fillMaxSize()
    ) {
        SaveConfirmation(isSaving = isSaving.value, isConfirmed = isConfirmed.value) {
            isSaving.value = false
            isConfirmed.value = false
        }

        currentProfile?.let { profile ->

            val email = remember { mutableStateOf(TextFieldValue(profile.email)) }
            val firstName = remember { mutableStateOf(TextFieldValue(profile.firstName)) }
            val lastName = remember { mutableStateOf(TextFieldValue(profile.lastName)) }
            val description = remember { mutableStateOf(TextFieldValue(profile.description ?: "")) }

            val currentPassword = remember { mutableStateOf(TextFieldValue("")) }
            val newPassword = remember { mutableStateOf(TextFieldValue("")) }
            val showChangePasswordDialog = remember { mutableStateOf(false) }

            val showEmailDialog = remember { mutableStateOf(false) }
            val showFirstNameDialog = remember { mutableStateOf(false) }
            val showLastNameDialog = remember { mutableStateOf(false) }
            val showDescriptionDialog = remember { mutableStateOf(false) }

            ConfirmPasswordDialog(
                showDialog = showConfirmPasswordDialog.value,
                onOkClicked = { password ->
                    showConfirmPasswordDialog.value = false
                    viewModel.viewModelScope.launch {
                        val isPasswordCorrect = PasswordUtils.verifyPassword(password, currentProfile!!.password)
                        if (isPasswordCorrect) {
                            // Save profile updates
                            val updatedProfile = Profile(
                                email = email.value.text,
                                password = if (newPassword.value.text.isNotEmpty()) {
                                    PasswordUtils.hashPassword(newPassword.value.text)
                                } else {
                                    currentProfile!!.password
                                },
                                firstName = firstName.value.text,
                                lastName = lastName.value.text,
                                description = description.value.text,
                                imageUri = currentProfile!!.imageUri,
                                userId = currentProfile!!.userId
                            )
                            currentProfile!!.userId?.let {
                                viewModel.updateProfile(it, updatedProfile)
                                viewModel.getProfile(it)
                            }
                            showSaveAnimation()
                        } else {
                            errorMessage.value = "Incorrect password. Please try again."
                            showErrorDialog.value = true
                        }
                    }
                },
                onDismissRequest = { showConfirmPasswordDialog.value = false }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))


                    CustomTextFieldClickable(
                        icon = Icons.Filled.Person,
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = "Email",
                        placeholder = "Enter your email",
                        onClick = { showEmailDialog.value = true }
                    )
                    OverlayDialog(
                        showDialog = showEmailDialog,
                        title = "Enter your email",
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = "Email",
                        placeholder = "Enter your email",
                        onDismissRequest = { showEmailDialog.value = false },
                        onOkClicked = {
                            email.value = email.value
                            showEmailDialog.value = false
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextFieldClickable(
                        icon = Icons.Filled.PersonOutline,
                        value = firstName.value,
                        onValueChange = { firstName.value = it },
                        label = "First Name",
                        placeholder = "Enter your first name",
                        onClick = { showFirstNameDialog.value = true }
                    )
                    OverlayDialog(
                        showDialog = showFirstNameDialog,
                        title = "Enter your first name",
                        value = firstName.value,
                        onValueChange = { firstName.value = it },
                        label = "First Name",
                        placeholder = "Enter your first name",
                        onDismissRequest = { showFirstNameDialog.value = false },
                        onOkClicked = {
                            firstName.value = firstName.value
                            showFirstNameDialog.value = false
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextFieldClickable(
                        icon = Icons.Filled.PersonOutline,
                        value = lastName.value,
                        onValueChange = { lastName.value = it },
                        label = "Last Name",
                        placeholder = "Enter your last name",
                        onClick = { showLastNameDialog.value = true }
                    )
                    OverlayDialog(
                        showDialog = showLastNameDialog,
                        title = "Enter your last name",
                        value = lastName.value,
                        onValueChange = { lastName.value = it },
                        label = "Last Name",
                        placeholder = "Enter your last name",
                        onDismissRequest = { showLastNameDialog.value = false },
                        onOkClicked = {
                            lastName.value = lastName.value
                            showLastNameDialog.value = false
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    CustomTextFieldClickable(
                        icon = Icons.Filled.PersonOutline,
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = "Description",
                        placeholder = "Enter your description",
                        onClick = { showDescriptionDialog.value = true },
                    )
                    OverlayDialog(
                        showDialog = showDescriptionDialog,
                        title = "Enter your description",
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = "Description",
                        placeholder = "Enter your description",
                        onDismissRequest = { showDescriptionDialog.value = false },
                        onOkClicked = {
                            description.value = description.value
                            showDescriptionDialog.value = false
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextFieldClickable(
                        icon = Icons.Filled.Lock,
                        value = if (newPassword.value.text.isEmpty()) TextFieldValue("******") else newPassword.value,
                        onValueChange = { },
                        label = "Change Password",
                        placeholder = "",
                        onClick = { showChangePasswordDialog.value = true },
                        isPassword = false,
                        isPasswordTransformation = true // Set this to true
                    )
                    OverlayDialog(
                        showDialog = showChangePasswordDialog,
                        title = "Change Password",
                        value = currentPassword.value,
                        onValueChange = { currentPassword.value = it },
                        label = "Current Password",
                        placeholder = "Enter your current password",
                        onDismissRequest = { showChangePasswordDialog.value = false },
                        onOkClicked = { newPass ->
                            newPassword.value = newPass
                            showChangePasswordDialog.value = false
                        },
                        oldPassword = currentPassword,
                        newPassword = newPassword
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006BFF),
                                contentColor = Color.White
                            ),
                            onClick = {
                                if(currentPassword.value.text.isEmpty() && newPassword.value.text.isEmpty()) {
                                    showConfirmPasswordDialog.value = true
                                } else (
                                    if (PasswordUtils.verifyPassword(currentPassword.value.text, currentProfile!!.password)) {
                                        showConfirmPasswordDialog.value = true
                                    } else {
                                        errorMessage.value = "Incorrect password. Please try again."
                                        showErrorDialog.value = true
                                    }
                                )
                            },
                            enabled = currentProfile?.let { isProfileUpdated(it, email.value, firstName.value, lastName.value, description.value, newPassword.value) } ?: false
                        ) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}

fun isProfileUpdated(
    profile: Profile,
    email: TextFieldValue,
    firstName: TextFieldValue,
    lastName: TextFieldValue,
    description: TextFieldValue,
    newPassword: TextFieldValue
): Boolean {
    return profile.email != email.text || profile.firstName != firstName.text || profile.lastName != lastName.text || profile.description != description.text || newPassword.text.isNotEmpty()
}


@Composable
fun ConfirmPasswordDialog(
    showDialog: Boolean,
    onOkClicked: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Confirm changes",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val password = remember { mutableStateOf(TextFieldValue("")) }

                    CustomTextField(
                        icon = Icons.Filled.Lock,
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = "Password",
                        placeholder = "Enter your password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onDismissRequest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon (
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close"
                            )
                        }

                        Button(
                            onClick = {
                                onOkClicked(password.value.text)
                                      },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon (
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Check"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ErrorDialog(
    showDialog: Boolean,
    errorMessage: String,
    onDismissRequest: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = errorMessage,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SaveConfirmation(
    isSaving: Boolean,
    isConfirmed: Boolean,
    onClick: (Offset) -> Unit
) {
    val transition = updateTransition(targetState = isSaving, label = "SaveTransition")
    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "Alpha"
    ) { targetState ->
        if (targetState) 1f else 0f
    }

    Box(
        Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(Color.Black.copy(alpha = 0.8f))
            .pointerInput(Unit) {
                if (isConfirmed) {
                    detectTapGestures(onTap = onClick)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isSaving && !isConfirmed,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            CircularProgressIndicator()
        }

        AnimatedVisibility(
            visible = isConfirmed,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Icon(
                Icons.Filled.Check,
                modifier = Modifier.size(48.dp),
                contentDescription = "Confirmation",
                tint = Color.Green
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextFieldClickable(
    icon: ImageVector,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    isPassword: Boolean = false,
    isPasswordTransformation: Boolean = false, // Add this parameter
    isMultiLine: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }
    val isFocused = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val passwordVisibility = remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> {
                    isFocused.value = true
                    onClick() // Add this line
                }
                is FocusInteraction.Unfocus -> isFocused.value = false
            }
        }
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            icon,
            modifier = Modifier
                .padding(top = 5.dp)
                .size(26.dp),
            contentDescription = "Login icon",
            tint = if (isFocused.value) primaryColor else Color.LightGray
        )

        TextField(
            readOnly = true,
            value = value,
            onValueChange = onValueChange,
            modifier = if (isMultiLine) {
                Modifier
                    .focusRequester(focusRequester)
                    .weight(1f)
                    .heightIn(min = 100.dp)
            } else {
                Modifier
                    .focusRequester(focusRequester)
                    .weight(1f)
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = !isMultiLine,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
            visualTransformation = if (isPasswordTransformation) PasswordVisualTransformation() else VisualTransformation.None,
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
fun OverlayDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    placeholder: String,
    isMultiLine: Boolean = true,
    onDismissRequest: () -> Unit,
    onOkClicked: (TextFieldValue) -> Unit,
    oldPassword: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    newPassword: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
) {
    val localFocus = LocalFocusManager.current
    val resetFocus = {
        onDismissRequest()
        localFocus.clearFocus(force = true)
    }


    if (showDialog.value) {
        Dialog(
            onDismissRequest = resetFocus,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            TopAppBar(
                modifier = Modifier
                    .zIndex(10f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    scrolledContainerColor = MaterialTheme.colorScheme.secondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                navigationIcon = {
                    IconButton(onClick = resetFocus) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                },
                actions = {
                    IconButton(onClick = {
                        resetFocus()
                        onOkClicked(if (title == "Change Password") newPassword.value else value)
                    }) {
                        Text(
                            text = "OK",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                }
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, top = 56.dp, bottom = 0.dp, end = 0.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(46.dp))

                    if (title == "Change Password") {
                        val focusRequester1 = remember { FocusRequester() }
                        val focusRequester2 = remember { FocusRequester() }

                        CustomTextField(
                            value = oldPassword.value,
                            onValueChange = { oldPassword.value = it },
                            label = "Old Password",
                            placeholder = "Enter your current password",
                            isPassword = true,
                            focusRequester = focusRequester1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(
                            value = newPassword.value,
                            onValueChange = { newPassword.value = it },
                            label = "New Password",
                            placeholder = "Enter your new password",
                            isPassword = true,
                            focusRequester = focusRequester2
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(showDialog.value) {
                            focusRequester.requestFocus()
                            onValueChange(value.copy(selection = TextRange(value.text.length)))
                        }

                        CustomTextField(
                            value = value,
                            onValueChange = onValueChange,
                            label = label,
                            placeholder = placeholder,
                            isMultiLine = true,
                            focusRequester = focusRequester
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                }
            }
        }
    }
}
