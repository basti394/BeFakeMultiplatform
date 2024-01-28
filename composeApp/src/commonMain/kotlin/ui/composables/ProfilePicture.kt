package ui.composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import io.kamel.core.Resource
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job

@Composable
fun ProfilePicture(
    modifier: Modifier,
    profilePicture: String?,
    username: String?
) {
    val painterResource: Resource<Painter> = asyncPainterResource(getProfilePictureUrl(profilePicture, username)) {
        coroutineContext = Job() + Dispatchers.IO
    }
    val fallbackPainter = asyncPainterResource(getFallBackUrl(username)) {
        coroutineContext = Job() + Dispatchers.IO
    }

    when (painterResource) {
        is Resource.Loading -> {
            Spacer(modifier = modifier.clip(CircleShape).background(Color.Gray))
        }
        is Resource.Success -> {
            KamelImage(
                modifier = modifier.clip(CircleShape),
                resource = painterResource,
                contentDescription = "ProfilePicture",
            )
        }
        is Resource.Failure -> {
            when(fallbackPainter) {
                is Resource.Success -> {
                    KamelImage(
                        modifier = modifier.clip(CircleShape),
                        resource = fallbackPainter,
                        contentDescription = "ProfilePicture"
                    )
                }
                else -> {
                    Spacer(modifier = modifier.clip(CircleShape).background(Color.Gray))
                }
            }
        }
    }
}

private fun getProfilePictureUrl(profilePicture: String?, username: String?): String {
    return profilePicture
        ?: getFallBackUrl(username)
}

private fun getFallBackUrl(username: String?) = if (username.isNullOrEmpty()) {
    "https://ui-avatars.com/api/?name=&background=8B8B8B&size=100"
} else {
    "https://ui-avatars.com/api/?name=${username.first()}&background=random&size=100"
}
