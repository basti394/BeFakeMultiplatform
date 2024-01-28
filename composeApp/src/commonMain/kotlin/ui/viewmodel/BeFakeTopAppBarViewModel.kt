package ui.viewmodel

import data.repository.UserRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pizza.xyz.befake.model.dtos.feed.User

class BeFakeTopAppBarViewModel(
    userRepository: UserRepository
) : ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.PhoneNumber)
    val state = _state.asStateFlow()

    private var _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.loggedIn().collect {
                if (it) {
                    _state.value = LoginState.LoggedIn
                    viewModelScope.launch {
                        userRepository.getUser().collect {
                            _user.value = it
                        }
                    }
                } else {
                    _state.value = LoginState.PhoneNumber
                }
            }
        }
    }
}