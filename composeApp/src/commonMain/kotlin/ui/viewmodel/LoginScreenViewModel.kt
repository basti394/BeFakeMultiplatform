package ui.viewmodel

import BeFake.composeApp.MR
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import com.vanniktech.locale.displayName
import data.repository.UserRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import data.service.FriendsService
import data.service.LoginService
import pizza.xyz.befake.model.dtos.countrycode.Country
import pizza.xyz.befake.model.dtos.feed.ProfilePicture
import pizza.xyz.befake.model.dtos.feed.User
import model.dtos.login.LoginRequestDTO
import model.dtos.verify.VerifyOTPRequestDTO
import pizza.xyz.befake.db.BeFakeDatabase

class LoginScreenViewModel(
    private val loginService: LoginService,
    private val friendsService: FriendsService,
    private val userRepository: UserRepository
) : ViewModel() {

    val loginState = loginService.loginState

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _country = MutableStateFlow(Country("", "", ""))
    val country = _country.asStateFlow()

    private val _optCode = MutableStateFlow("")
    val optCode = _optCode.asStateFlow()

    private val _otpSession = MutableStateFlow("")

    fun onPhoneNumberChanged(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    init {
        runBlocking(Dispatchers.IO) {
            loginService.checkIfLoggedIn()
            setDefaultCountry()
        }
        viewModelScope.launch {
            loginService.loginState.collect {
                if (it is LoginState.LoggedIn) {
                    friendsService.me().onSuccess { res ->
                        userRepository.setUserData(User(
                            res.data.id,
                            res.data.username,
                            res.data.profilePicture,
                        ))
                    }
                }
            }
        }
    }

    private fun setDefaultCountry() {
        Locale.from(Locales.currentLocaleString()).country?.let { country ->
            _country.value = Country(country.displayName(), country.callingCodes.first(), country.code, country.emoji)
        }
    }

    fun onCountryChanged(newCountry: Country) {
        _country.value = newCountry
    }

    fun onOptCodeChanged(newOptCode: String) {
        _optCode.value = newOptCode
    }

    fun onLoginClicked() {
        if (phoneNumber.value.isEmpty()) {
            return
        }
        val phoneNumberWithCountry = "${country.value.dialCode}${phoneNumber.value}"
        viewModelScope.launch {
            loginService.sendCode(LoginRequestDTO(phoneNumberWithCountry)).onSuccess {
                if (it.status == 201) {
                    _otpSession.value = it.data?.otpSession ?: ""
                } else {
                    loginService.setLoginState(
                        LoginState.Error(
                            LoginState.PhoneNumber,
                            MR.strings.something_went_wrong_please_try_again,
                            it.message
                        )
                    )
                }
            }.onFailure {
                println(it.message)
                println(it.printStackTrace())
                if (it.message != "Invalid phone number") {
                    loginService.setLoginState(
                        LoginState.Error(
                            LoginState.PhoneNumber,
                            MR.strings.something_went_wrong_please_try_again,
                            it.message
                        )
                    )
                }
            }
        }
    }

    fun onVerifyClicked() {
        viewModelScope.launch {
            loginService.verifyCode(VerifyOTPRequestDTO(_otpSession.value, optCode.value)).onFailure {
                _optCode.value = ""
                loginService.setLoginState(
                    LoginState.Error(
                        LoginState.OTPCode,
                        MR.strings.something_went_wrong_please_try_again,
                        it.message
                    )
                )
            }
        }
    }

    fun onBackToPhoneNumberClicked() {
        resetValues()
        loginService.setLoginState(LoginState.PhoneNumber)
    }

    private fun resetValues() {
        _phoneNumber.value = ""
        _optCode.value = ""
        _otpSession.value = ""
    }
}

sealed class LoginState {
    object PhoneNumber : LoginState()
    object OTPCode : LoginState()
    object LoggedIn : LoginState()

    sealed class LoginStateWithPreviousState : LoginState() {
        abstract val previousState: LoginState
    }

    class Loading(override val previousState: LoginState) : LoginStateWithPreviousState()
    class Error(override val previousState: LoginState, val messageResource: StringResource, val message: String? = null) : LoginStateWithPreviousState()
}
