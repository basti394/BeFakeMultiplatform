package data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.core.component.KoinComponent
import pizza.xyz.befake.model.dtos.feed.FeedResponseDTO
import pizza.xyz.befake.model.dtos.friendsOfFriends.FriendsOfFriendsResponseDTO
import pizza.xyz.befake.model.dtos.me.MeResponseDTO
import pizza.xyz.befake.utils.Utils.BASE_URL

interface FriendsService {

    suspend fun feed(): Result<FeedResponseDTO>

    suspend fun friendsOfFriends(): Result<FriendsOfFriendsResponseDTO>

    suspend fun me(): Result<MeResponseDTO>
}

class FriendsServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = BASE_URL
): KoinComponent, FriendsService {

    override suspend fun feed(): Result<FeedResponseDTO> = runCatching {
        return@runCatching client.get("$baseUrl/friends/feed") {
            header("token", "sadf")
        }.body<FeedResponseDTO>()
    }

    override suspend fun friendsOfFriends(): Result<FriendsOfFriendsResponseDTO> = runCatching {
        return@runCatching client.get("$baseUrl/friends/friends-of-friends") {
            header("token", "sadf")
        }.body<FriendsOfFriendsResponseDTO>()
    }

    override suspend fun me(): Result<MeResponseDTO> = runCatching {
        return@runCatching client.get("$baseUrl/friends/me") {
            header("token", "sadf")
        }.body<MeResponseDTO>()
    }
}
