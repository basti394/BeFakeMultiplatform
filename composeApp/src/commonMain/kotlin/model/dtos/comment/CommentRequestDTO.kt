package pizza.xyz.befake.model.dtos.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentRequestDTO(
    @SerialName("userId") val userId: String,
    @SerialName("postId") val postId: String,
    @SerialName("comment") val comment: String,
)