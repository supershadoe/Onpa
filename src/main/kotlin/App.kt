package me.supershadoe.onpa

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class App: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Header()
                HelloWorld()
            }
        }
    }

    @Composable
    private fun Header() {
        SmallTopAppBar(title = @Composable {Text("Onpa")})
    }

    @Composable
    private fun HelloWorld() {
        Text("Hello World!")
    }

    @Composable
    private fun AppTheme(content: @Composable () -> Unit) {
        val darkTheme = isSystemInDarkTheme()
        MaterialTheme(
            content = content,
            colorScheme =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    if (darkTheme) dynamicDarkColorScheme(applicationContext)
                    else dynamicLightColorScheme(applicationContext)
                else
                    if (darkTheme) darkColorScheme() else lightColorScheme()
        )
    }
}