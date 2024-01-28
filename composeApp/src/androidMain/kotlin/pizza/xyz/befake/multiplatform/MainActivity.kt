package pizza.xyz.befake.multiplatform

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import di.appModule
import org.koin.core.context.startKoin
import pizza.xyz.befake.multiplatform.theme.BeFakeTheme
import platformModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            modules(
                platformModule(true),
                appModule()
            )
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