package com.example.mat3_nav

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.example.mat3_nav.model.Profile
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val isDarkTheme = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
        setContent {
            val context = LocalContext.current
            val userId = mainViewModel.userId.observeAsState().value
            val hasUserId = remember { mutableStateOf(context.hasUserId()) }
            val counter = remember { mutableStateOf(0) }

            // Add this LaunchedEffect block
            mainViewModel.fetchAllProfiles()
            val profiles = mainViewModel.allProfiles.observeAsState().value
            //getProfile( context.getUserId() ) // get the user profile from the database



            LaunchedEffect(counter.value) {
                if (counter.value > 0) {
                    println("LaunchedEffect context.getUserId() 1st " + context.getUserId()) // good
                    hasUserId.value = false
                    context.clearUserId()
                    println("LaunchedEffect context.getUserId() 2nd " + context.getUserId()) // null - good
                }
            }

            Mat3_navTheme(
                darkTheme = isDarkTheme
            ) {
                NavBarApp(hasCreatedProfile = hasUserId, counter = counter, profiles = profiles)
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NavBarApp(
    hasCreatedProfile: MutableState<Boolean>,
    counter: MutableState<Int>,
    viewModel: MainViewModel = viewModel(),
    context: Context = LocalContext.current,
    profiles: List<Profile>?,
) {
    val navController = rememberAnimatedNavController()
    val scope2 = LocalSharedElementsRootScope
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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
        NavScreens.DetailScreen,
    )
    val selectedItem = remember { mutableStateOf(screens[0]) }
    val scrollPosition = viewModel.scrollPosition
    val listState = rememberLazyGridState(scrollPosition)

    val profile by viewModel.profile.observeAsState()
    if (profile == null) {
        if (context.hasUserId()){
            viewModel.getProfile(context.getUserId()!!)
        }
    } else {
        println("profile $profile")
    }


    LaunchedEffect(listState) {
        val previousIndex = (previousSelectedUser).coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            listState.scrollToItem(previousIndex)
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = hasCreatedProfile.value,
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
            val selectedUserIsNotSelected = selectedUser == -1
            val containerColor by animateColorAsState(
                targetValue = if (selectedUserIsNotSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                animationSpec = tween(25)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (hasCreatedProfile.value) {
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
                            containerColor = containerColor,
                            scrolledContainerColor = containerColor,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
                            titleContentColor = MaterialTheme.colorScheme.onSecondary,
                            actionIconContentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            AnimatedContent(
                                targetState = selectedUserIsNotSelected,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(25)) with fadeOut(
                                        animationSpec = tween(
                                            25
                                        )
                                    )
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
                        title = {
                            AnimatedContent(
                                targetState = selectedUserIsNotSelected,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(25)) with fadeOut(
                                        animationSpec = tween(
                                            25
                                        )
                                    )
                                }
                            ) { isNotSelected ->
                                if (isNotSelected) {
                                    Text(
                                        "Simple TopAppBar",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else {
                                    Text(
                                        "",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        actions = {
                            AnimatedVisibility(
                                visible = selectedUserIsNotSelected,
                                enter = fadeIn(animationSpec = tween(25)),
                                exit = fadeOut(animationSpec = tween(25))
                            ) {
                                IconButton(
                                    onClick = {
                                        //todo delete the userId from the database
                                        viewModel.userId.value?.let { viewModel.logout(it) }
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            counter.value++
                                        }, 100)

                                        println("DIT IS HET: " + hasCreatedProfile.value)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = selectedUserIsNotSelected,
                                enter = fadeIn(animationSpec = tween(25)),
                                exit = fadeOut(animationSpec = tween(25))
                            ) {
                                profile?.imageUri?.let {
                                    fileNameToDrawableId(context, it).let {
                                        IconButton(
                                            onClick = {
                                                openBottomSheet = true
                                            }
                                        ) {
                                            Image(
                                                painter = painterResource(id = it),
                                                contentDescription = "Localized description",
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                            )
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(
                                visible = selectedUserIsNotSelected,
                                enter = fadeIn(animationSpec = tween(25)),
                                exit = fadeOut(animationSpec = tween(25))
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
                }
                NavHostScreen(
                    navController,
                    hasCreatedProfile,
                    viewModel,
                    profiles
                )
                if (hasCreatedProfile.value) {
                    BottomNav(
                        navController,
                        Modifier
                            .align(Alignment.BottomCenter)
                    )
                }
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
            modifier = Modifier
                .navigationBarsPadding(),

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
fun BottomNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        elevation = 0.dp,
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
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
    hasCreatedProfile: MutableState<Boolean>,
    viewModel: MainViewModel,
    profiles: List<Profile>?
) {
    val startDestination =
        if (hasCreatedProfile.value) NavScreens.HomeScreen.route else NavScreens.StartScreen.route

    AnimatedNavHost(
        navController,
        startDestination = startDestination,
    ) {
        composable(
            route = NavScreens.HomeScreen.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeIn(animationSpec = tween(300))
            },
        ) {
            LaunchedEffect(Unit) {
                viewModel.fetchAllProfiles()
            }
            if (profiles != null) {
                AppHomeScreen(
                    navController = navController,
                    viewModel = viewModel,
                    profiles = profiles
                )
            }
        }

        composable(
            route = NavScreens.DetailScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            AppDetailScreen(
                navController = navController
            )
        }

        composable(
            route = NavScreens.CreateProfileScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            CreateProfileScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = NavScreens.LoginScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            LoginScreen(
                navController = navController,
                viewModel = viewModel,
                onLoginSuccess = { context, userId ->
                    // Set the user ID
                    context.setUserId(userId)
                    println("LoginScreen - context.setUserId: ${context.getUserId()}") // good
                    // Update the hasUserId state
                    hasCreatedProfile.value = true
                    navController.navigate(NavScreens.HomeScreen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(
            route = NavScreens.StartScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                ) + fadeOut(animationSpec = tween(300))
            }

        ) {
            StartScreen(
                navController = navController
            )
        }
    }
}

fun Context.getUserId(): String? {
    val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPrefs.getString("userId", null)
}

fun Context.setUserId(userId: String) {
    val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().putString("userId", userId).apply()
}

fun Context.hasUserId(): Boolean {
    return getUserId() != null
}

fun Context.getIdOfUser(): String? {
    val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    println("getIdOfUser >>>>>>>>>" + sharedPrefs.getString("userId", null))
    return sharedPrefs.getString("userId", null)
}

fun Context.clearUserId() {
    val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().remove("userId").apply()
}

fun fileNameToDrawableId(context: Context, fileName: String): Int {
    println("fileNameToDrawableId: $fileName")
    return context.resources.getIdentifier(fileName, "drawable", context.packageName)
}




