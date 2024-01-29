import androidx.compose.ui.window.ComposeUIViewController
import org.koin.compose.koinInject
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.model.dtos.feed.User

fun MainViewController() = ComposeUIViewController {

    val database: BeFakeDatabase = koinInject<BeFakeDatabase>()
    if (database.postQueries.getPost().executeAsOneOrNull() == null) {
        database.postQueries.insert(null)
    }
    if (database.userQueries.getToken().executeAsOneOrNull().isNullOrBlank()) {
        database.userQueries.insert(User(), "")
    }

    App()
}
