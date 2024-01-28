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
import org.koin.dsl.module
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.db.Post
import model.dtos.feed.PostData
import io.ktor.util.InternalAPI
import pizza.xyz.befake.model.dtos.feed.User


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
            ),
            UserAdapter = pizza.xyz.befake.db.User.Adapter(
                data_Adapter = userAdapter
            )
        )
        appDataDatabase
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
actual fun <T> StateFlow<T>.collectAsStateMultiplatform(): State<T> = collectAsState()

actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
): KoinDefinition<T> = factory(qualifier = qualifier, definition = definition)

@OptIn(InternalAPI::class)
actual val defaultPlatformEngine: HttpClientEngine = Darwin.create()