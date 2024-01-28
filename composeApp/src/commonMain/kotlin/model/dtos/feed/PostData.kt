package model.dtos.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.UserPosts

@Serializable
data class PostData(
    @SerialName("userPosts") val userPosts: UserPosts? = null,
    @SerialName("friendsPosts") val friendsPosts: List<FriendsPosts>,
    @SerialName("remainingPosts") val remainingPosts: Int,
    @SerialName("maxPostsPerMoment") val maxPostsPerMoment: Int,
)
