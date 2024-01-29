package ui.viewmodel

import data.repository.FeedRepository
import data.repository.UserRepository
import data.service.FriendsService
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pizza.xyz.befake.db.Post
import pizza.xyz.befake.model.dtos.feed.User

class HomeScreenViewModel(
    private val feedRepository: FeedRepository,
    private val friendsService: FriendsService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _feed: MutableStateFlow<Post?> = MutableStateFlow(null)
    val feed = _feed.asStateFlow()

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState.Loading)
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private val _myUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val myUser = _myUser.asStateFlow()

    private var updating = true

    init {
        val tempFeed = feedRepository.getFeedNow().data_
        if (tempFeed != null && tempFeed.friendsPosts.isNotEmpty()) {
            _state.value = HomeScreenState.Loaded
        }
        viewModelScope.launch(Dispatchers.Default) {
            updating = true
            getProfilePicture()
            feedRepository.updateFeed()
            updating = false
        }
        viewModelScope.launch {
            feedRepository.getFeed().collect {
                _feed.value = it
                if (feed.value != null && !updating) _state.value = HomeScreenState.Loaded
            }
        }
    }

    private suspend fun getProfilePicture() {
        friendsService.me().onSuccess {
            _myUser.value = User(
                id = it.data.id,
                username = it.data.username,
                profilePicture = it.data.profilePicture,
            )
        }
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    object Loaded : HomeScreenState()
    class Error(val message: String) : HomeScreenState()
}