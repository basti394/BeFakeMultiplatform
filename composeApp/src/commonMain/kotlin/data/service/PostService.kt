package data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.InternalAPI
import org.koin.core.component.KoinComponent
import pizza.xyz.befake.model.dtos.comment.CommentRequestDTO
import pizza.xyz.befake.model.dtos.comment.CommentResponseDTO
import pizza.xyz.befake.utils.Utils.BASE_URL

interface PostService {

    suspend fun comment(
        userId: String,
        postId: String,
        comment: String
    ): Result<CommentResponseDTO>
}

class PostServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = BASE_URL
): KoinComponent, PostService {

    @OptIn(InternalAPI::class)
    override suspend fun comment(
        userId: String,
        postId: String,
        comment: String
    ) = runCatching {
        return@runCatching client.get("$baseUrl/post/comment") {
            body = CommentRequestDTO(
                userId = userId,
                postId = postId,
                comment = comment
            )
        }.body<CommentResponseDTO>()
    }
}