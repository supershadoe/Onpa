/*
 * SPDX-FileCopyrightText: 2022 supershadoe <supershadoe@proton.me>
 * SPDX-License-Identifier: Apache-2.0
 */

package me.supershadoe.onpa.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal object Colors {
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
}
