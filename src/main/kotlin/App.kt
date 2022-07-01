package me.supershadoe.onpa

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class App: ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                BottomSheetScaffold(
                    sheetContent = {
                        Text("test")
                    },
                    sheetBackgroundColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        PlayPause {
                            /* TODO */
                        }
                    }
                ) {
                    Text("test", color=MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }

    @Composable
    private fun PlayPause(onClickTask: () -> Unit) {
        FloatingActionButton(
            onClick = onClickTask,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play_arrow),
                contentDescription = getString(R.string.play_button)
            )
        }
    }

    @Composable
    private fun AppTheme(content: @Composable () -> Unit) {
        val overAPI31 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val darkThemeM3 = darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF313033),
            onSurface = Color(0xFFE6E1E5)
        )
        val lightThemeM3 = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color(0xFFFFFFFF),
            background = Color(0xFFFFFBFE),
            onBackground = Color(0xFF1C1B1F),
            surface = Color(0xFFFFFBFE),
            onSurface = Color(0xFF1C1B1F)
        )

        // Honestly I have no clue how good this is because IDH an Android 12 device
        // nor do I care to run an emulator
        MaterialTheme(
            content = content,
            colorScheme =
                if (isSystemInDarkTheme())
                    if (overAPI31) dynamicDarkColorScheme(applicationContext)
                    else darkThemeM3
                else
                    if (overAPI31) dynamicLightColorScheme(applicationContext)
                    else lightThemeM3
        )
    }
}