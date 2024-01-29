import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun platformModule(allowUnsafeTraffic: Boolean): Module

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>

@Composable
expect fun getScreenSize(): Pair<Int, Int>

@Composable
expect fun VideoPlayer(
    modifier: Modifier,
    url: String,
    state: VideoPlayerState,
    onEnd: () -> Unit,
)

enum class VideoPlayerState {
    Playing,
    Ended,
}

@Composable
expect fun <T> StateFlow<T>.collectAsStateMultiplatform(): State<T>

expect val defaultPlatformEngine: HttpClientEngine