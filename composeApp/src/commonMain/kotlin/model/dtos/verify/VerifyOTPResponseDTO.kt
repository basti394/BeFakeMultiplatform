package pizza.xyz.befake.model.dtos.verify

import kotlinx.serialization.SerialName

data class VerifyOTPResponseDTO(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: Data?,
) {
    data class Data(
        @SerialName("token") val token: String,
    )
}