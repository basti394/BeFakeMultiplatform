import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.Json.Default.encodeToString
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import pizza.xyz.befake.db.Post
import pizza.xyz.befake.model.dtos.feed.PostData
import pizza.xyz.befake.db.BeFakeDatabase
import kotlin.coroutines.CoroutineContext

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun platformModule(allowUnsafeTraffic: Boolean) = module {
    single {
        val driver = AndroidSqliteDriver(
            BeFakeDatabase.Schema,
            get(),
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
            )
        )
        beFakeDatabase
    }
}

private val postAdapter = object : ColumnAdapter<PostData, String> {
    override fun decode(databaseValue: String) = decodeFromString(PostData.serializer(), databaseValue)
    override fun encode(value: PostData) = encodeToString(PostData.serializer(), value = value)
}

actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
): KoinDefinition<T> = viewModel(qualifier = qualifier, definition = definition)
