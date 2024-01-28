package model.dtos.verify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOTPResponseDTO(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: Data?,
) {

    @Serializable
    data class Data(
        @SerialName("token") val token: String,
    )
}