import BeFake.composeApp.MR
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.compose.stringResource
import di.appModule
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication.Companion.init
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.model.dtos.feed.ProfilePicture
import pizza.xyz.befake.model.dtos.feed.User
import ui.composables.BeFakeTopAppBar
import ui.screens.HomeScreen
import ui.screens.LoginScreen
import ui.screens.PostDetailScreen
import ui.viewmodel.LoginState

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    PreComposeApp {
        KoinApplication(application = {
            modules(
                listOf(
                    appModule()
                )
            )
        }) {
            MaterialTheme {
                MainContent(
                    loginState = LoginState.LoggedIn,
                    user = User(
                        id = "1",
                        username = "test",
                        profilePicture = ProfilePicture(
                            "https://picsum.photos/200/300",
                            30,
                            30
                        )
                    )
                )
            }
        }
    }

}

@Composable
fun MainContent(
    loginState: LoginState,
    user: User?
) {
    if (
        loginState != LoginState.LoggedIn
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBar(
                    loginState,
                    user
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                LoginScreen(paddingValues = paddingValues)
            }
        }
    } else {
        val navigator = rememberNavigator()

        NavHost(
            navigator = navigator,
            initialRoute = "home",
            navTransition = NavTransition(),
        ) {
            scene(
                "home",
                navTransition = NavTransition(
                    /*createTransition = slideInHorizontally(AnimatedContentTransitionScope.SlideDirection.End
                    ) { it },
                    destroyTransition = {
                        return@composable slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                        )
                    }*/
                ),
                /**/
            ) {

                Scaffold(
                    topBar = {
                        BeFakeTopAppBar(
                            loginState,
                            user
                        )
                    },
                    containerColor = Color.Black
                ) { paddingValues ->
                    HomeScreen(
                        paddingValues = paddingValues,
                        openDetailScreen = { username, selectedPost, focusInput, focusRealMojis -> navigator.navigate("post/$username?selectedPost=$selectedPost&focusInput=$focusInput&focusRealMojis=$focusRealMojis") }
                    )
                }
            }
            scene(
                "post/{username}?selectedPost={selectedPost}&focusInput={focusInput}&focusRealMojis={focusRealMojis}",
                /*arguments = listOf(
                    navArgument("username") {
                        defaultValue = ""
                    },
                    navArgument("selectedPost") {
                        defaultValue = 0
                    },
                    navArgument("focusInput") {
                        defaultValue = false
                    },
                    navArgument("focusRealMojis") {
                        defaultValue = false
                    }
                ),
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(100)
                    )
                },
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                    )
                }*/
            ) {
                val username = it.path<String>("username")
                val selectedPost = it.query<Int>("selectedPost")
                val focusInput = it.query<Boolean>("focusInput")
                val focusRealMojis = it.query<Boolean>("focusRealMojis")
                if (username.isNullOrBlank()) throw IllegalStateException("Username cannot be null or blank")
                PostDetailScreen(
                    postUsername = username,
                    selectedPost = selectedPost,
                    focusInput = focusInput,
                    onBack = { navigator.popBackStack() },
                    focusRealMojis = focusRealMojis,
                    myUser = user
                )
            }
        }

    }
}