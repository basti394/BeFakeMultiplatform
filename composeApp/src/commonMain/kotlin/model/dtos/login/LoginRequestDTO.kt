package pizza.xyz.befake.model.dtos.login

import kotlinx.serialization.SerialName

data class LoginRequestDTO(
    @SerialName("phone") val phone: String
)