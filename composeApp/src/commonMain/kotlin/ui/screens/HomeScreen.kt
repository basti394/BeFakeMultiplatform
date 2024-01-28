package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import model.dtos.feed.PostData
import pizza.xyz.befake.model.dtos.feed.User
import ui.composables.Post
import ui.composables.PostLoadingWithHeader
import ui.viewmodel.HomeScreenState
import ui.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    homeScreenViewModel: HomeScreenViewModel = koinInject(),
    openDetailScreen: (String, Int, Boolean, Boolean) -> Unit
) {

    val feed by homeScreenViewModel.feed.collectAsState()
    val state by homeScreenViewModel.state.collectAsState()
    val myUser by homeScreenViewModel.myUser.collectAsState()

    HomeScreenContent(
        feed = feed?.data_,
        state = state,
        myUser = myUser,
        openDetailScreen = openDetailScreen,
    )
}

@Composable
fun HomeScreenContent(
    feed: PostData?,
    state: HomeScreenState,
    myUser: User?,
    openDetailScreen: (String, Int, Boolean, Boolean) -> Unit
) {

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val count = feed?.friendsPosts?.size ?: 3
        item { Spacer(modifier = Modifier.height(100.dp)) }
        when (state) {
            is HomeScreenState.Loading -> {
                items(4) {
                    PostLoadingWithHeader()
                }
            }

            is HomeScreenState.Loaded -> {
                items(
                    count = count,
                    key = { it },
                ) {
                    Post(
                        post = feed?.friendsPosts?.get(it),
                        modifier = Modifier.padding(vertical = 8.dp),
                        myUser = myUser,
                        openDetailScreen = openDetailScreen
                    )
                }
            }

            is HomeScreenState.Error -> {
                item {
                    Text(
                        text = state.message,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(25.dp)) }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreenPreview() {

    val feed = FeedResponseDTO(
        status = 200,
        message = "Success",
        data = Data(
            done = true,
            msg = "msg",
            data = PostData(
                userPosts = UserPosts(
                    user = testFeedUser,
                    region = "de",
                    momentId = "1",
                    posts = listOf(
                        testFeedPostNoLocation
                    )
                ),
                friendsPosts = listOf(
                    FriendsPosts(
                        user = testFeedUser,
                        momentId = "1",
                        region = "de",
                        moment = Moment(
                            id = "1",
                            region = "de"
                        ),
                        posts = listOf(
                            Posts(
                                id = "1",
                                takenAt = "2021-09-18T12:00:00.000Z",
                                caption = "caption",
                                primary = PostMedia(
                                    url = "https://picsum.photos/1500/2000",
                                    width = 1500,
                                    height = 2000,
                                    mediaType = "image"
                                ),
                                secondary = PostMedia(
                                    url = "https://picsum.photos/1500/2000",
                                    width = 1500,
                                    height = 2000,
                                    mediaType = "image"
                                ),
                                comments = listOf(),
                                location = Location(
                    latitude = 52.5207,
                    longitude = 13.3733
                ),
                                retakeCounter = 0,
                                isLate = true,
                                isMain = true,
                                realMojis = emptyList(),
                                tags = emptyList(),
                                creationDate = "2021-09-18T12:00:00.000Z",
                                updatedAt = "2021-09-18T12:00:00.000Z",
                                visibility = emptyList(),
                                lateInSeconds = 187,
                                postType = "post",
                                btsMedia = PostMedia(
                                    url = "https://picsum.photos/1500/2000",
                                    width = 1500,
                                    height = 2000,
                                    mediaType = "image"
                                ),
                                origin = "repost",
                                parentPostId = "",
                                parentPostUserId = "",
                                parentPostUsername = "",
                            )
                        )
                    ),
                    FriendsPosts(
                        user = User(
                            id = "1",
                            username = "username",
                            profilePicture = ProfilePicture(
                                url = "https://picsum.photos/1000/1000",
                                width = 500,
                                height = 500
                            )
                        ),
                        momentId = "1",
                        region = "de",
                        moment = Moment(
                            id = "1",
                            region = "de"
                        ),
                        posts = listOf(
                            testFeedPostLateThreeMinLocationBerlin
                        )
                    ),
                    FriendsPosts(
                        user = User(
                            id = "1",
                            username = "username",
                            profilePicture = ProfilePicture(
                                url = "",
                                width = 500,
                                height = 500
                            )
                        ),
                        momentId = "1",
                        region = "de",
                        moment = Moment(
                            id = "1",
                            region = "de"
                        ),
                        posts = listOf(
                            testFeedPostNoLocation
                        )
                    ),
                ),
                remainingPosts = 0,
                maxPostsPerMoment = 3
            )
        )
    )

    BeFakeTheme(
        darkTheme = true
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBarContent(
                    loginState = LoginState.LoggedIn,
                    profilePicture = "https://picsum.photos/1000/1000",
                    username = "username"
                )
            }
        ) { padding ->
            val o = padding
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Black
            ) {
                HomeScreenContent(
                    feed = feed.data.data,
                    state = HomeScreenState.Loaded,
                    myUser = User(
                        id = "1",
                        username = "username",
                        profilePicture = ProfilePicture(
                            url = "https://picsum.photos/1000/1000",
                            width = 500,
                            height = 500
                        )

                    ),
                    openDetailScreen = {_, _, _, _ -> },
                )
            }
        }
    }

}

@Composable
@Preview
fun HomeScreenLoadingPreview() {

    BeFakeTheme(
        darkTheme = true
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBarContent(
                    loginState = LoginState.LoggedIn,
                    profilePicture = "https://picsum.photos/1000/1000",
                    username = "username"
                )
            }
        ) { padding ->
            val o = padding
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Black
            ) {
                HomeScreenContent(
                    feed = null,
                    state = HomeScreenState.Loading,
                    myUser = User(
                        id = "1",
                        username = "username",
                        profilePicture = ProfilePicture(
                            url = "https://picsum.photos/1000/1000",
                            width = 500,
                            height = 500
                        )

                    ),
                    openDetailScreen = {_, _, _, _ ->},
                )
            }
        }
    }

}*/