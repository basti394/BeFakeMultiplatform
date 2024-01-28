import androidx.compose.ui.window.ComposeUIViewController
import di.appModule
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.model.dtos.feed.User

fun MainViewController() = ComposeUIViewController {

    val database: BeFakeDatabase = koinInject<BeFakeDatabase>()
    if (database.postQueries.getPost().executeAsOneOrNull() == null) {
        database.postQueries.insert(null)
    }
    if (database.userQueries.getToken().executeAsOne().isBlank()) {
        database.userQueries.insert(User(), "")
    }

    App()
}
