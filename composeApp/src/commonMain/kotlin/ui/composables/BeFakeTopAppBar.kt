package ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import org.koin.compose.koinInject
import pizza.xyz.befake.model.dtos.feed.User
import ui.viewmodel.BeFakeTopAppBarViewModel
import ui.viewmodel.LoginScreenViewModel
import ui.viewmodel.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeFakeTopAppBar(
    viewModel: BeFakeTopAppBarViewModel = koinInject()
) {

    val loginState by viewModel.state.collectAsState()
    val user by viewModel.user.collectAsState()

    BeFakeTopAppBarContent(loginState = loginState, profilePicture = user?.profilePicture?.url, username = user?.username )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeFakeTopAppBarContent(
    loginState: LoginState,
    profilePicture: String?,
    username: String?
) {
    Column {
        TopAppBar(
            modifier = Modifier
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                    )
                ),
            title = {
                Header(
                    loginState = loginState,
                    profilePicture = profilePicture,
                    username = username
                )
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun Header(
    loginState: LoginState,
    profilePicture: String?,
    username: String?
) {
    println("LOL: $profilePicture in Header")
    Box(
        modifier = Modifier
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 30.dp),
            horizontalArrangement = if (loginState is LoginState.LoggedIn) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loginState is LoginState.LoggedIn) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Rounded.Group,
                        contentDescription = "More",
                        tint = Color.White
                    )
                }
            }
            Text(
                text = "BeFake.",
                color = Color.White,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            )
            if (loginState is LoginState.LoggedIn) {

                val painterResource: Resource<Painter>? = profilePicture?.let {
                    asyncPainterResource(it) {
                        coroutineContext = Job() + Dispatchers.IO
                    }
                }

                when (painterResource) {
                    is Resource.Success -> {
                        KamelImage(
                            modifier = Modifier
                                .size(30.dp)
                                .border(1.dp, Color.Black, CircleShape).clip(CircleShape),
                            resource = painterResource,
                            contentDescription = "ProfilePicture",
                        )
                    }

                    else -> {
                        Spacer(modifier = Modifier
                            .size(30.dp)
                            .border(1.dp, Color.Black, CircleShape).clip(CircleShape).background(Color.Gray))}
                }

            }
        }
    }
}

@Composable
//@Preview
fun HeaderPreview() {
    BeFakeTopAppBarContent(
        loginState = LoginState.LoggedIn,
        profilePicture = "https://picsum.photos/1000/1000",
        username = "username"
    )
}

@Composable
//@Preview
fun HeaderPreviewLetterPb() {
    BeFakeTopAppBarContent(
        loginState = LoginState.LoggedIn,
        profilePicture = null,
        username = "username"
    )
}

@Composable
//@Preview
fun HeaderPreviewNotLoggedIn() {
    BeFakeTopAppBarContent(
        loginState = LoginState.PhoneNumber,
        profilePicture = null,
        username = "username"
    )
}