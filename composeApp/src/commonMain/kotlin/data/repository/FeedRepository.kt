package data.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import data.service.FriendsService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pizza.xyz.befake.db.Post
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import model.dtos.feed.PostData
import pizza.xyz.befake.db.BeFakeDatabase

interface FeedRepository {

    fun getFeed(): Flow<Post>

    suspend fun updateFeed()

    fun getPostByUsername(username: String): Flow<FriendsPosts?>
}

class FeedRepositoryImpl(
    private val friendsService: FriendsService,
) : KoinComponent, FeedRepository {

    private val database: BeFakeDatabase by inject()

    override fun getFeed(): Flow<Post> {
        return database.postQueries.getPost().asFlow().mapToOne()
    }

    override suspend fun updateFeed() {
        friendsService.feed().onSuccess {
            it.data.data?.let { data ->
                database.postQueries.insert(formatFeed(data))
            }
        }
    }

    override fun getPostByUsername(username: String): Flow<FriendsPosts?> {
        return getFeed().map { it.data_?.friendsPosts?.find { fp -> fp.user.username == username } }
    }

    private fun formatFeed(postData: PostData) : PostData {
        return postData.copy(friendsPosts = postData.friendsPosts.map { it.copy(posts = it.posts.sortedBy { post -> post.creationDate }) }.sortedBy { it.posts.last().creationDate }.reversed())
    }
}