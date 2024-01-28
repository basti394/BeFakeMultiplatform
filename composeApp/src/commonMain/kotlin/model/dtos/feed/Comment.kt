package model.dtos.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pizza.xyz.befake.model.dtos.feed.User

@Serializable
data class Comment (

    @SerialName("id") val id : String,
    @SerialName("user") val user : User,
    @SerialName("content") val content : String,
    @SerialName("postedAt") val postedAt : String
)