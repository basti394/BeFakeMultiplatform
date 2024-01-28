package pizza.xyz.befake.model.dtos.countrycode

import kotlinx.serialization.SerialName

data class Country(
    @SerialName("name") val name: String,
    @SerialName("dial_code") val dialCode: String,
    @SerialName("code") val code: String
)