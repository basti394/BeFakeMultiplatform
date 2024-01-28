package model.dtos.login

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResultDTO(
    @SerialName("status") val status: Int? = null,
    @SerialName("message") val message: String,
    @SerialName("data") val data: Data? = null,
) {

    @Serializable
    data class Data(
        @SerialName("otpSession") val otpSession: String,
    )
}