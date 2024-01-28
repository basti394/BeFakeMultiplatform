package pizza.xyz.befake.model.dtos.refresh

import kotlinx.serialization.SerialName

data class RefreshTokenRequestDTO(
    @SerialName("token") val token: String,
)