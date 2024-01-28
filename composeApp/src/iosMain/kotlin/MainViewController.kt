import androidx.compose.ui.window.ComposeUIViewController
import di.appModule
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pizza.xyz.befake.db.BeFakeDatabase

fun MainViewController() = ComposeUIViewController {

    val database: BeFakeDatabase = koinInject<BeFakeDatabase>()
    database.postQueries.insert(null)

    App()
}
