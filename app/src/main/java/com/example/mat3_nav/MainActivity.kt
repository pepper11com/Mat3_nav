package com.example.mat3_nav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mat3_nav.ui.screens.*
import com.example.mat3_nav.ui.theme.Mat3_navTheme
import com.example.mat3_nav.viewmodel.MainViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.mxalbert.sharedelements.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDarkTheme = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
        setContent {
            Mat3_navTheme(
                darkTheme = isDarkTheme
            ) {
//                WindowCompat.setDecorFitsSystemWindows(window, false)
//                window.statusBarColor = Color.Transparent.toArgb()
                NavBarApp()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NavBarApp(
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberAnimatedNavController()

    val scope2 = LocalSharedElementsRootScope


    val scrollBehavior =  TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
//        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val screens = listOf(
        NavScreens.HomeScreen,
        NavScreens.DetailScreen
    )
    val selectedItem = remember { mutableStateOf(screens[0]) }

    val scrollPosition = viewModel.scrollPosition
    val listState = rememberLazyGridState(scrollPosition)



    LaunchedEffect(listState) {
        val previousIndex = (previousSelectedUser).coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            listState.scrollToItem(previousIndex)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                        label = { Text(screen.route) },
                        selected = screen == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = screen

                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    val selectedUserIsNotSelected = selectedUser == -1
                    val cornerAnimation by animateDpAsState(
                        targetValue = if (selectedUserIsNotSelected) 6.dp else 0.dp
                    )

                    TopAppBar(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = cornerAnimation,
                                    bottomEnd = cornerAnimation
                                )
                            ),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            scrolledContainerColor = MaterialTheme.colorScheme.secondary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
                            titleContentColor = MaterialTheme.colorScheme.onSecondary,
                            actionIconContentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        title = {
                            Text(
                                "Simple TopAppBar",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            AnimatedContent(
                                targetState = selectedUserIsNotSelected,
                                transitionSpec = {
                                    fadeIn(animationSpec = TweenSpec(500)) with
                                            fadeOut(animationSpec = TweenSpec(500))
                                }
                            ) { isNotSelected ->
                                if (isNotSelected) {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (selectedUser != -1) {
                                                previousSelectedUser = selectedUser
                                                selectedUser = -1
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            AnimatedVisibility(
                                visible = selectedUserIsNotSelected,
                                enter = fadeIn(animationSpec = TweenSpec(500)),
                                exit = fadeOut(animationSpec = TweenSpec(500))
                            ) {
                                IconButton(
                                    onClick = {
                                        openBottomSheet = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Localized description"
                                    )
                                    BottomDrawer(
                                        openBottomSheet = openBottomSheet,
                                        onDismissRequest = { openBottomSheet = false },
                                        bottomSheetState = bottomSheetState,
                                        scope = scope
                                    )
                                }
                            }
                        }
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                bottomBar = {
                    BottomNav(navController)
                }
            ) { innerPadding ->
                NavHostScreen(navController, innerPadding)
            }
        }
    )
}



@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomDrawer(
    openBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    bottomSheetState: SheetState,
    scope: CoroutineScope
) {
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                            }
                        }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            LazyColumn {
                items(50) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun BottomNav(navController: NavController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        elevation = 0.dp,
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (selectedUser == -1) 6.dp else 0.dp,
                    topEnd = if (selectedUser == -1) 6.dp else 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val screens = listOf(
            NavScreens.HomeScreen,
            NavScreens.DetailScreen,
        )
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NavHostScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel = viewModel(),
) {
    AnimatedNavHost(
        navController,
        startDestination = NavScreens.HomeScreen.route,
        Modifier.padding(innerPadding)
    ) {
        composable(
            route = NavScreens.HomeScreen.route,
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    ) + fadeIn(animationSpec = tween(300))
            },
            ) {
            AppHomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            route = NavScreens.DetailScreen.route,
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),

                    ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            AppDetailScreen(
                navController = navController
            )
        }
    }
}

