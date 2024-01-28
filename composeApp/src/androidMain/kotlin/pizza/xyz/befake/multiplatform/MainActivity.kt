package pizza.xyz.befake.multiplatform

import App
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import di.appModule
import di.initKoin
import org.koin.android.BuildConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.binds
import org.koin.dsl.module
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.model.dtos.feed.User
import pizza.xyz.befake.multiplatform.theme.BeFakeTheme
import platformModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin() {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            modules(
                module {
                    single { this@MainActivity } binds arrayOf(Context::class, Application::class)
                }
            )
        }
        
        val database: BeFakeDatabase by inject()
        if (database.postQueries.getPost().executeAsOneOrNull() == null) {
            database.postQueries.insert(null)
        }
        if (database.userQueries.getToken().executeAsOneOrNull()?.isBlank() != false) {
            database.userQueries.insert(User(), "")

        }

        setContent {
            BeFakeTheme {
                App()
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}