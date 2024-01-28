package pizza.xyz.befake.model.dtos.comment

import kotlinx.serialization.SerialName

data class CommentResponseDTO(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: Any?
)