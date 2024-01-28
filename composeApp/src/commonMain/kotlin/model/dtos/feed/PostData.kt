package pizza.xyz.befake.model.dtos.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostData(
    @SerialName("userPosts") val userPosts: UserPosts?,
    @SerialName("friendsPosts") val friendsPosts: List<FriendsPosts>,
    @SerialName("remainingPosts") val remainingPosts: Int,
    @SerialName("maxPostsPerMoment") val maxPostsPerMoment: Int,
)
