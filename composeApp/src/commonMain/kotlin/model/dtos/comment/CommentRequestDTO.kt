package pizza.xyz.befake.model.dtos.comment

import kotlinx.serialization.SerialName

data class CommentRequestDTO(
    @SerialName("userId") val userId: String,
    @SerialName("postId") val postId: String,
    @SerialName("comment") val comment: String,
)