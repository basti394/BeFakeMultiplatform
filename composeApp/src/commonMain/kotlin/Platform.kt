import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun platformModule(allowUnsafeTraffic: Boolean): Module

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>