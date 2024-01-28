package pizza.xyz.befake.model.dtos.verify

import kotlinx.serialization.SerialName

data class VerifyOTPRequestDTO(
    @SerialName("otpSession") val otpSession: String,
    @SerialName("code") val code: String
)