package pizza.xyz.befake.model.dtos.comment

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDTO(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String,
    @Contextual @SerialName("data") val data: Any?
)