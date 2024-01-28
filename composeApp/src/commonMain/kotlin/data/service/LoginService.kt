package data.service

import BeFake.composeApp.MR
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import model.dtos.login.LoginRequestDTO
import model.dtos.login.LoginResultDTO
import model.dtos.refresh.RefreshTokenRequestDTO
import model.dtos.verify.VerifyOTPRequestDTO
import model.dtos.verify.VerifyOTPResponseDTO
import ui.viewmodel.LoginState
import pizza.xyz.befake.utils.Utils.BASE_URL
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.model.dtos.feed.User

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

    init {
        CoroutineScope(Dispatchers.IO).launch {
            database.userQueries.getToken().asFlow().mapToOne().collect {
                if (it.isNotEmpty()) {
                    _loginState.value = LoginState.LoggedIn
                } else {
                    _loginState.value = LoginState.PhoneNumber
                }
            }
        }
    }

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
        val res = client.post("$baseUrl/login/send-code") {
            setBody(body)
            contentType(ContentType.Application.Json)
        }
        println(res.body<String>())
        return@runCatching res.body<LoginResultDTO>().also { _loginState.value = LoginState.OTPCode }
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
        val res = client.post("$baseUrl/login/verify") {
            setBody(verifyOTPRequestDTO)
            contentType(ContentType.Application.Json)
        }.body<VerifyOTPResponseDTO>()
        res.data?.let {
            database.userQueries.insert(User(), it.token)
        }
        return@runCatching res
    }

    @OptIn(InternalAPI::class)
    override suspend fun refreshToken(): Result<VerifyOTPResponseDTO> = runCatching {
        val refreshTokenRequestDTO = database.userQueries.getToken().let {
            RefreshTokenRequestDTO(
                token = it.executeAsOne()
            )
        }
        val res = client.post("$baseUrl/login/refresh") {
            setBody(refreshTokenRequestDTO)
            contentType(ContentType.Application.Json)
        }.body<VerifyOTPResponseDTO>()
        res.data?.let {
            database.userQueries.insert(User(), it.token)
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
        if (token.isNotEmpty() == true) {
            _loginState.value = LoginState.LoggedIn
        }
    }
}
