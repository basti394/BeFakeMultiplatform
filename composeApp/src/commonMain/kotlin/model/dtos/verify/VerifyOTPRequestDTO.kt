package model.dtos.verify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOTPRequestDTO(
    @SerialName("otpSession") val otpSession: String,
    @SerialName("code") val code: String
)