package ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import data.repository.FeedRepository
import data.repository.UserRepository
import data.service.PostService
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.User

class PostDetailScreenViewModel(
    private val feedRepository: FeedRepository,
    private val postService: PostService,
    private var userRepository: UserRepository
) : ViewModel() {

    private var _post = MutableStateFlow<FriendsPosts?>(null)
    val post = _post.asStateFlow()

    private var _myUser = MutableStateFlow<User?>(null)
    val myUser = _myUser.asStateFlow()

    fun getPost(username: String) {
        viewModelScope.launch {
            _post.value = feedRepository.getPostByUsername(username).first()
        }
        viewModelScope.launch {
            _myUser.value = userRepository.getUser().first()
        }
    }

    fun commentPost(userId: String, postId: String, comment: String) {
        if (postId.isBlank() || comment.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            postService.comment(
                userId = userId,
                postId = postId,
                comment = comment
            ).onSuccess {
                feedRepository.updateFeed()
                getPost(post.value?.user?.username ?: "")
            }.onFailure {
                println("Error: ${it.message}, ${it.stackTraceToString()}, ${it.cause}")
            }
        }
    }
}