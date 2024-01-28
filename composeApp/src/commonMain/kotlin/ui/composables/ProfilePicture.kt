package ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun ProfilePicture(
    modifier: Modifier,
    profilePicture: String?,
    username: String?
) {
    Spacer(modifier = modifier.clip(CircleShape))
    /*AsyncImage(
        modifier = modifier.clip(CircleShape),
        placeholder = Utils.debugPlaceholderProfilePicture(id = MR.drawable.profile_picture_example),
        model = getProfilePictureUrl(profilePicture, username),
        contentDescription = "profilePicture"
    )*/
}

private fun getProfilePictureUrl(profilePicture: String?, username: String?): String {
    return profilePicture
        ?: if (username.isNullOrEmpty()) {
            "https://ui-avatars.com/api/?name=&background=8B8B8B&size=100"
        } else {
            "https://ui-avatars.com/api/?name=${username.first()}&background=random&size=100"
        }
}
