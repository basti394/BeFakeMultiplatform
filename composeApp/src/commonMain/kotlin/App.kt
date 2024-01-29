import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import di.appModule
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.composables.BeFakeTopAppBar
import ui.screens.HomeScreen
import ui.screens.LoginScreen
import ui.screens.PostDetailScreen
import ui.viewmodel.LoginScreenViewModel
import ui.viewmodel.LoginState

@Composable
fun App(
    viewModel: LoginScreenViewModel = koinInject()
) {

    val loginState by viewModel.loginState.collectAsState()

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
                    loginState = loginState,
                )
            }
        }
    }

}

@Composable
fun MainContent(
    loginState: LoginState,
) {

    if (
        loginState != LoginState.LoggedIn
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBar()
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
        ) {
            scene(
                "home",
                navTransition = NavTransition(
                    destroyTransition = slideOutHorizontally(targetOffsetX = { -it }),
                    createTransition = slideInHorizontally(initialOffsetX = { -it }),
                    pauseTransition = slideOutHorizontally(targetOffsetX = { it }),
                    resumeTransition = slideInHorizontally(initialOffsetX = { it }),
                ),
            ) {
                Scaffold(
                    topBar = {
                        BeFakeTopAppBar()
                    },
                    containerColor = Color.Black
                ) { paddingValues ->
                    HomeScreen(
                        paddingValues = paddingValues,
                        focusedPostUserName = it.query<String>("username"),
                        openDetailScreen = { username, selectedPost, focusInput, focusRealMojis -> navigator.navigate("post/$username?selectedPost=$selectedPost&focusInput=$focusInput&focusRealMojis=$focusRealMojis") }
                    )
                }
            }
            scene(
                "post/{username}",
                navTransition = NavTransition(
                    destroyTransition = slideOutHorizontally(targetOffsetX = { it }),
                    createTransition = slideInHorizontally(initialOffsetX = { it }),
                    pauseTransition = slideOutHorizontally(targetOffsetX = { -it }),
                    resumeTransition = slideInHorizontally(initialOffsetX = { -it }),
                ),
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
                    onBack = {
                        navigator.navigate("home?username=$username")
                    },
                    focusRealMojis = focusRealMojis,
                )
            }
        }

    }
}