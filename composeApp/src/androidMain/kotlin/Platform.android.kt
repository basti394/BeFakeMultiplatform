import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.Json.Default.encodeToString
import model.dtos.feed.PostData
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.db.Post
import pizza.xyz.befake.model.dtos.feed.User

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun platformModule(allowUnsafeTraffic: Boolean) = module {
    single<BeFakeDatabase> {
        val driver = AndroidSqliteDriver(
            BeFakeDatabase.Schema,
            androidContext(),
            "befake.db",
            callback = object : AndroidSqliteDriver.Callback(BeFakeDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;");
                }
            }
        )
        val beFakeDatabase = BeFakeDatabase(
            driver = driver,
            PostAdapter = Post.Adapter(
                data_Adapter = postAdapter
            ),
            UserAdapter = pizza.xyz.befake.db.User.Adapter(
                data_Adapter = userAdapter
            )
        )
        beFakeDatabase
    }
}

private val postAdapter = object : ColumnAdapter<PostData, String> {
    override fun decode(databaseValue: String) = decodeFromString(PostData.serializer(), databaseValue)
    override fun encode(value: PostData) = encodeToString(PostData.serializer(), value = value)
}

private val userAdapter = object : ColumnAdapter<User, String> {
    override fun decode(databaseValue: String) = decodeFromString(User.serializer(), databaseValue)
    override fun encode(value: User) = encodeToString(User.serializer(), value = value)
}

@Composable
actual fun getScreenSize(): Pair<Int, Int> {
    return Pair(LocalConfiguration.current.screenWidthDp, LocalConfiguration.current.screenHeightDp)
}

@Composable
actual fun <T> StateFlow<T>.collectAsStateMultiplatform(): State<T> = collectAsStateWithLifecycle()

actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
): KoinDefinition<T> = viewModel(qualifier = qualifier, definition = definition)

actual val defaultPlatformEngine: HttpClientEngine = Android.create()