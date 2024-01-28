import platform.UIKit.UIDevice
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.Json.Default.encodeToString
import org.jetbrains.skiko.OS
import org.koin.dsl.module
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.db.Post
import pizza.xyz.befake.model.dtos.feed.PostData
import io.ktor.client.engine.engines
import io.ktor.util.InternalAPI
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import io.ktor.client.engine.ios.Ios


class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun platformModule(allowUnsafeTraffic: Boolean) = module {
    single {
        val driver = NativeSqliteDriver(
            BeFakeDatabase.Schema,
            "befake.db",
        )
        val appDataDatabase = BeFakeDatabase(
            driver = driver,
            PostAdapter = Post.Adapter(
                data_Adapter = postAdapter
            )
        )
        appDataDatabase
    }
}

private val postAdapter = object : ColumnAdapter<PostData, String> {
    override fun decode(databaseValue: String) = decodeFromString(PostData.serializer(), databaseValue)
    override fun encode(value: PostData) = encodeToString(PostData.serializer(), value = value)
}

actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
): KoinDefinition<T> = factory(qualifier = qualifier, definition = definition)

@OptIn(InternalAPI::class)
actual val defaultPlatformEngine: HttpClientEngine = Darwin.create()