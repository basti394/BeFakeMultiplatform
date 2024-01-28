package data.service

import BeFake.composeApp.MR
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pizza.xyz.befake.model.dtos.login.LoginRequestDTO
import pizza.xyz.befake.model.dtos.login.LoginResultDTO
import pizza.xyz.befake.model.dtos.refresh.RefreshTokenRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPResponseDTO
import ui.viewmodel.LoginState
import pizza.xyz.befake.utils.Utils
import pizza.xyz.befake.utils.Utils.BASE_URL
import pizza.xyz.befake.db.BeFakeDatabase

interface LoginService {

    val loginState: StateFlow<LoginState>

    suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO>

    suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO>

    suspend fun refreshToken(): Result<VerifyOTPResponseDTO>

    suspend fun logOut(): Result<Boolean>

    fun setLoginState(loginState: LoginState)

    suspend fun checkIfLoggedIn()
}

class LoginServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = BASE_URL,
): KoinComponent, LoginService {

    private var _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.PhoneNumber)
    override val loginState = _loginState.asStateFlow()

    private val database: BeFakeDatabase by inject()

    //TODO Move Logic to Repository

    @OptIn(InternalAPI::class)
    override suspend fun sendCode(
        body: LoginRequestDTO
    ): Result<LoginResultDTO> = runCatching {
        if (
            !(_loginState.value is LoginState.PhoneNumber
            || (_loginState.value is LoginState.Error && (_loginState.value as LoginState.Error).previousState == LoginState.PhoneNumber))
        ) throw Exception("Invalid state")

        _loginState.value = LoginState.Loading(LoginState.PhoneNumber)
        if (!body.phone.startsWith("+")) {
            _loginState.value = LoginState.Error(LoginState.PhoneNumber, MR.strings.phone_numer_start_with_plus)
            throw Exception("Invalid phone number")
        }
        return@runCatching client.get("$baseUrl/login/send-code") {
            this.body = body
        }.body<LoginResultDTO>().also { _loginState.value = LoginState.OTPCode }
    }

    @OptIn(InternalAPI::class)
    override suspend fun verifyCode(
        verifyOTPRequestDTO: VerifyOTPRequestDTO
    ): Result<VerifyOTPResponseDTO> = runCatching {
        if (
            !(_loginState.value is LoginState.OTPCode
            || (_loginState.value is LoginState.Error && (_loginState.value as LoginState.Error).previousState == LoginState.OTPCode))
        ) throw Exception("Invalid state")

        _loginState.value = LoginState.Loading(LoginState.OTPCode)
        val res = client.get("$baseUrl/login/verify") {
            this.body = verifyOTPRequestDTO
        }.body<VerifyOTPResponseDTO>()
        res.data?.let {
            database.userQueries.insert("1", it.token)
        }
        return@runCatching res.also { _loginState.value = LoginState.LoggedIn }
    }

    @OptIn(InternalAPI::class)
    override suspend fun refreshToken(): Result<VerifyOTPResponseDTO> = runCatching {
        val refreshTokenRequestDTO = database.userQueries.getToken().let {
            RefreshTokenRequestDTO(
                token = it.executeAsOne()
            )
        }
        val res = client.get("$baseUrl/login/refresh") {
            this.body = refreshTokenRequestDTO
        }.body<VerifyOTPResponseDTO>()
        res.data?.let {
            database.userQueries.insert("1", it.token)
        }
        return@runCatching res
    }

    override suspend fun logOut(): Result<Boolean> {

        database.userQueries.deleteToken()
        _loginState.value = LoginState.Error(LoginState.PhoneNumber, MR.strings.log_out_text)
        return Result.success(true)
    }

    override fun setLoginState(loginState: LoginState) {
        _loginState.value = loginState
    }

    override suspend fun checkIfLoggedIn() {
        val token = database.userQueries.getToken().executeAsOne()
        if (token?.isNotEmpty() == true) {
            _loginState.value = LoginState.LoggedIn
        }
    }
}
