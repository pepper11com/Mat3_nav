package com.example.mat3_nav.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mat3_nav.R
import com.example.mat3_nav.viewmodel.MainViewModel
import com.mxalbert.sharedelements.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AppHomeScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    UserCardsRoot(
        viewModel = viewModel
    )
}
var selectedUser: Int by mutableStateOf(-1)
var previousSelectedUser: Int = -1
@Composable
fun UserCardsRoot(
    viewModel: MainViewModel
) {
    SharedElementsRoot {
        val user = selectedUser
        val gridState = rememberLazyGridState()
//        val scrollPosition = rememberLazyGridState(viewModel.scrollPosition)
        BackHandler(enabled = user >= 0) {
            changeUser(-1)
        }
        DelayExit(visible = user < 0) {
            UserCardsScreen(
                listState1 = gridState,
                viewModel = viewModel
            )
        }
        DelayExit(visible = user >= 0) {
            val currentUser = remember { users[user] }
            UserCardDetailsScreen(
                currentUser
            )
        }
    }
}
@Composable
private fun UserCardsScreen(
    listState1: LazyGridState,
    viewModel: MainViewModel
) {
    val listState = rememberLazyGridState(viewModel.scrollPosition)
    LaunchedEffect(listState1) {
        val previousIndex = (previousSelectedUser).coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            viewModel.onScrollPositionChanged(previousIndex)
            listState.scrollToItem(previousIndex)
            println("Scrolling to $previousIndex")
            println("Scrolling to viewModel.scrollPosition: ${viewModel.scrollPosition}")
        }
    }
    val scope = LocalSharedElementsRootScope.current!!
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState1,
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier
            .padding(top = 64.dp, bottom = 56.dp)

    ) {
        itemsIndexed(users) { i, user ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                SharedMaterialContainer(
                    key = user.name,
                    screenKey = ListScreen,
                    shape = MaterialTheme.shapes.medium,
                    elevation = 2.dp,
                    transitionSpec = MaterialFadeInTransitionSpec
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(enabled = !scope.isRunningTransition) {
                                scope.changeUser(i)
                            }

                    ) {
                        Image(
                            painterResource(id = user.avatar),
                            contentDescription = user.name,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = user.name,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun UserCardDetailsScreen(user: User) {
    val (fraction, setFraction) = remember { mutableStateOf(1f) }
    // Scrim color
    val scope = LocalSharedElementsRootScope.current!!

    Surface(
        color = Color.Black.copy(alpha = 0.32f * (1 - fraction))
    ) {
        SharedMaterialContainer(
            key = user.name,
            screenKey = DetailsScreen,
            isFullscreen = true,
            transitionSpec = MaterialFadeOutTransitionSpec,
            onFractionChanged = setFraction
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(id = user.avatar),
                        contentDescription = user.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .draggable(
                                orientation = Orientation.Vertical,
                                state = rememberDraggableState { delta ->
                                    if (delta > 10f) {
                                        scope.changeUser(-1)
                                    }
                                }
                            ),
//                            .clickable(enabled = !scope.isRunningTransition) {
//                                scope.changeUser(-1)
//                            },
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = user.name,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
@Composable
fun UserListRoot() {
    SharedElementsRoot {
        BackHandler(enabled = selectedUser >= 0) {
            changeUser(-1)
        }

        val listState = rememberLazyListState()
        Crossfade(
            targetState = selectedUser,
            animationSpec = tween(durationMillis = TransitionDurationMillis)
        ) { user ->
            when {
                user < 0 -> UserListScreen(listState)
                else -> UserDetailsScreen(users[user])
            }
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UserListScreen(listState: LazyListState) {
    LaunchedEffect(listState) {
        val previousIndex = previousSelectedUser.coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            listState.scrollToItem(previousIndex)
        }
    }
    val scope = LocalSharedElementsRootScope.current!!
    LazyColumn(state = listState) {
        itemsIndexed(users) { i, user ->
            ListItem(
                modifier = Modifier.clickable(enabled = !scope.isRunningTransition) {
                    scope.changeUser(i)
                },
                icon = {
                    SharedMaterialContainer(
                        key = user.avatar,
                        screenKey = ListScreen,
                        shape = CircleShape,
                        color = Color.Transparent,
                        transitionSpec = FadeOutTransitionSpec
                    ) {
                        Image(
                            painterResource(id = user.avatar),
                            contentDescription = user.name,
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                text = {
                    SharedElement(
                        key = user.name,
                        screenKey = ListScreen,
                        transitionSpec = CrossFadeTransitionSpec
                    ) {
                        Text(text = user.name)
                    }
                }
            )
        }
    }
}
@Composable
private fun UserDetailsScreen(user: User) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SharedMaterialContainer(
            key = user.avatar,
            screenKey = DetailsScreen,
            shape = MaterialTheme.shapes.medium,
            color = Color.Transparent,
            elevation = 10.dp,
            transitionSpec = FadeOutTransitionSpec
        ) {
            val scope = LocalSharedElementsRootScope.current!!
            Image(
                painterResource(id = user.avatar),
                contentDescription = user.name,
                modifier = Modifier
                    .size(200.dp)
                    .clickable(enabled = !scope.isRunningTransition) { scope.changeUser(-1) },
                contentScale = ContentScale.Crop
            )
        }
        SharedElement(
            key = user.name,
            screenKey = DetailsScreen,
            transitionSpec = CrossFadeTransitionSpec
        ) {
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
fun SharedElementsRootScope.changeUser(user: Int) {
    val currentUser = selectedUser
    if (currentUser != user) {
        val targetUser = if (user >= 0) user else currentUser
        if (targetUser >= 0) {
            users[targetUser].let {
                prepareTransition(it.avatar, it.name)
            }
        }
        previousSelectedUser = selectedUser
        selectedUser = user
    }
}
private data class User(@DrawableRes val avatar: Int, val name: String)
private val users = listOf(
    User(R.drawable.avatar_1, "Adam"),
    User(R.drawable.avatar_2, "Andrew"),
    User(R.drawable.avatar_3, "Anna"),
    User(R.drawable.avatar_4, "Boris"),
    User(R.drawable.avatar_5, "Carl"),
    User(R.drawable.avatar_6, "Donna"),
    User(R.drawable.avatar_7, "Emily"),
    User(R.drawable.avatar_8, "Fiona"),
    User(R.drawable.avatar_9, "Grace"),
    User(R.drawable.avatar_10, "Irene"),
    User(R.drawable.avatar_11, "Jack"),
    User(R.drawable.avatar_12, "Jake"),
    User(R.drawable.avatar_13, "Mary"),
    User(R.drawable.avatar_14, "Peter"),
    User(R.drawable.avatar_15, "Rose"),
    User(R.drawable.avatar_16, "Victor")
)

private const val ListScreen = "list"
const val DetailsScreen = "peopleDetails"
private const val TransitionDurationMillis = 250
private val FadeOutTransitionSpec = MaterialContainerTransformSpec(
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Out
)
private val CrossFadeTransitionSpec = SharedElementsTransitionSpec(
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Cross,
    fadeProgressThresholds = ProgressThresholds(0.10f, 0.40f)
)
private val MaterialFadeInTransitionSpec = MaterialContainerTransformSpec(
    pathMotionFactory = MaterialArcMotionFactory,
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.In
)
private val MaterialFadeOutTransitionSpec = MaterialContainerTransformSpec(
    pathMotionFactory = MaterialArcMotionFactory,
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Out
)



