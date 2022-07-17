/*
 * SPDX-FileCopyrightText: 2022 supershadoe <supershadoe@proton.me>
 * SPDX-License-Identifier: Apache-2.0
 */

package me.supershadoe.onpa

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

import me.supershadoe.onpa.ui.Colors

class OnpaAct: ComponentActivity() {

    private lateinit var mediaService: MediaService
    private var bound: Boolean = false

    private val servConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mediaService = (service as MediaService.LocalBinder).getService()
            bound = true
            TODO("Not yet implemented")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            var isPlaying: Boolean by remember { mutableStateOf(false) }
            AppTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }
                Scaffold(
                    topBar = { TopAppBar() },
                    floatingActionButton = {
                        PlayPause(isPlaying) {
                            isPlaying = !isPlaying
                        }
                    },
                    content = {}
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MediaService::class.java).also {
            bindService(it, servConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(servConnection)
    }

    @Composable
    private fun TopAppBar() {
        SmallTopAppBar(
            title={
                Text(
                    text=stringResource(id = R.string.app_name)
                )
            },
            colors = smallTopAppBarColors(
                // The background isn't as smooth as light theme
                // in dark theme
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
            actions = {
                IconButton({}) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_info_button),
                        contentDescription = stringResource(
                            id = R.string.info_button
                        )
                    )
                }
            }
        )
    }

    @Composable
    private fun PlayPause(isPlaying: Boolean, onClickTask: () -> Unit) {
        FloatingActionButton(
            onClick = onClickTask
        ) {
            Icon(
                painter = painterResource(
                    id =
                        if (isPlaying) R.drawable.ic_pause_button
                        else R.drawable.ic_play_button
                ),
                contentDescription = stringResource(
                    id =
                        if (isPlaying) R.string.pause_button
                        else R.string.play_button
                )
            )
        }
    }

    @Composable
    private fun AppTheme(content: @Composable () -> Unit) {
        val overAPI31 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        // Honestly I have no clue how good this is because IDH an Android 12 device
        // nor do I care to run an emulator
        MaterialTheme(
            content = content,
            colorScheme =
            if (isSystemInDarkTheme())
                if (overAPI31) dynamicDarkColorScheme(applicationContext)
                else Colors.darkThemeM3
            else
                if (overAPI31) dynamicLightColorScheme(applicationContext)
                else Colors.lightThemeM3
        )
    }
}
