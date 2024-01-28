package di

import data.repository.FeedRepository
import data.service.FriendsServiceImpl
import data.service.LoginServiceImpl
import data.repository.FeedRepositoryImpl
import data.service.FriendsService
import data.service.PostServiceImpl
import defaultPlatformEngine
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import platformModule
import ui.viewmodel.CountryCodeSelectionSheetViewModel
import ui.viewmodel.HomeScreenViewModel
import ui.viewmodel.LoginScreenViewModel
import ui.viewmodel.PostDetailScreenViewModel
import viewModelDefinition

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(
        platformModule(allowUnsafeTraffic = true),
        appModule()
    )
}

fun appModule() = module {

    single<FriendsService> { FriendsServiceImpl(get()) }
    single<LoginServiceImpl> { LoginServiceImpl(get()) }
    single<PostServiceImpl> { PostServiceImpl(get()) }

    single<FeedRepository> { FeedRepositoryImpl(get()) }

    viewModelDefinition { CountryCodeSelectionSheetViewModel() }
    viewModelDefinition { HomeScreenViewModel(get(), get()) }
    viewModelDefinition { LoginScreenViewModel(get(), get()) }
    viewModelDefinition { PostDetailScreenViewModel(get(), get()) }

    single { createHttpClient() }
}

private fun createHttpClient() = HttpClient(defaultPlatformEngine)