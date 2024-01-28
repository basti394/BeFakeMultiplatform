package pizza.xyz.befake.model.dtos.login

import kotlinx.serialization.SerialName

data class LoginResultDTO(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: Data?,
) {

    data class Data(
        @SerialName("otpSession") val otpSession: String,
    )
}