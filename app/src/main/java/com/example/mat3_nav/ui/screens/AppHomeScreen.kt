package com.example.mat3_nav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHomeScreen(
    navController: NavController
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Songs", "Artists", "Playlists")

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {


        BottomSheetScaffold(
            sheetContainerColor= MaterialTheme.colorScheme.onPrimary,
            sheetContentColor= MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onPrimary),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 128.dp,
            sheetContent = {

                    NavigationBar(
                        containerColor= MaterialTheme.colorScheme.onPrimary,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .padding(0.dp),

                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                modifier = Modifier
                                    .padding(0.dp)
                                    .background(MaterialTheme.colorScheme.onPrimary),
                                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }

                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(bottom = 56.dp)
                        .height(128.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Swipe up to expand sheet")
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sheet content")
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        }
                    ) {
                        Text("Click to collapse sheet")
                    }
                }
            }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f),
                contentAlignment = Alignment.BottomCenter
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "This is the home screen",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            scope.launch { scaffoldState.bottomSheetState.expand() }
                        }
                    ) {
                        Text("Click to expand sheet")
                    }
                }
            }
        }


    }


}




