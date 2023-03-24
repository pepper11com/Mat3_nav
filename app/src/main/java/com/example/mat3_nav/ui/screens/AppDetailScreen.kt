package com.example.mat3_nav.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
            val username = remember { mutableStateOf(TextFieldValue(profile.username)) }
            val firstName = remember { mutableStateOf(TextFieldValue(profile.firstName)) }
            val lastName = remember { mutableStateOf(TextFieldValue(profile.lastName)) }
            //if there is no description, set it to an empty string
            val description = remember { mutableStateOf(TextFieldValue(profile.description ?: "")) }



            val showUsernameDialog = remember { mutableStateOf(false) }
            val showFirstNameDialog = remember { mutableStateOf(false) }
            val showLastNameDialog = remember { mutableStateOf(false) }
            val showDescriptionDialog = remember { mutableStateOf(false) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextFieldClickable(
                        icon = Icons.Filled.Person,
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = "Username",
                        placeholder = "Enter your username",
                        onClick = { showUsernameDialog.value = true }
                    )
                    OverlayDialog(
                        showDialog = showUsernameDialog,
                        title = "Enter your username",
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = "Username",
                        placeholder = "Enter your username",
                        onDismissRequest = { showUsernameDialog.value = false },
                        onOkClicked = {
                            // Set the username that was entered in the dialog to the username field
                            username.value = username.value
                            showUsernameDialog.value = false
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
                            // Set the first name that was entered in the dialog to the first name field
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
                            // Set the last name that was entered in the dialog to the last name field
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
                                // Set the description that was entered in the dialog to the description field
                                description.value = description.value
                                showDescriptionDialog.value = false
                            },
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
    onOkClicked: () -> Unit
) {

    if (showDialog.value) {
        Dialog(
            onDismissRequest = onDismissRequest
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
                ,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val focusRequester = remember { FocusRequester() } // Add this line
                    LaunchedEffect(showDialog.value) {
                        focusRequester.requestFocus()

                        // Set the cursor position to the end of the text
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

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedButton(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                onOkClicked()
                                focusRequester.requestFocus() // Add this line
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
