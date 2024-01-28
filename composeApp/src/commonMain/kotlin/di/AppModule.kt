package di

import data.repository.FeedRepository
import data.service.FriendsServiceImpl
import data.service.LoginServiceImpl
import data.repository.FeedRepositoryImpl
import data.repository.UserRepository
import data.repository.UserRepositoryImpl
import data.service.FriendsService
import data.service.LoginService
import data.service.PostService
import data.service.PostServiceImpl
import defaultPlatformEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import platformModule
import ui.viewmodel.BeFakeTopAppBarViewModel
import ui.viewmodel.CountryCodeSelectionSheetViewModel
import ui.viewmodel.HomeScreenViewModel
import ui.viewmodel.LoginScreenViewModel
import ui.viewmodel.PostDetailScreenViewModel
import viewModelDefinition

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(platformModule(allowUnsafeTraffic = true))
    modules(
        appModule()
    )
}

fun appModule() = module {

    single<FriendsService> { FriendsServiceImpl(get()) }
    single<LoginService> { LoginServiceImpl(get()) }
    single<PostService> { PostServiceImpl(get()) }

    single<FeedRepository> { FeedRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl() }

    viewModelDefinition { CountryCodeSelectionSheetViewModel() }
    viewModelDefinition { HomeScreenViewModel(get(), get(), get()) }
    viewModelDefinition { LoginScreenViewModel(get(), get(), get()) }
    viewModelDefinition { PostDetailScreenViewModel(get(), get(), get()) }
    viewModelDefinition { BeFakeTopAppBarViewModel(get()) }

    single { createJson() }

    single { createHttpClient(get(), get()) }
}

private fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

private fun createHttpClient(
    json: Json,
    tokenRepository: UserRepository
) = HttpClient(defaultPlatformEngine) {
    install(ContentNegotiation) {
        json(json)
    }
    install(DefaultRequest) {
        apply {
            headers.apply {
                val token = tokenRepository.getTokenString()
                append(HttpHeaders.ContentType, "application/json")
                append("token", token)
            }
        }
    }
}