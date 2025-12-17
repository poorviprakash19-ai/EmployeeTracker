package com.company.employeetracker.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ScreenSize {
    @Composable
    fun isSmallScreen(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp < 700
    }

    @Composable
    fun isMediumScreen(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp in 700..900
    }

    @Composable
    fun isLargeScreen(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp > 900
    }

    @Composable
    fun screenWidth(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp.dp
    }

    @Composable
    fun screenHeight(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp.dp
    }

    @Composable
    fun adaptiveSpacing(): Dp {
        return when {
            isSmallScreen() -> 8.dp
            isMediumScreen() -> 12.dp
            else -> 16.dp
        }
    }

    @Composable
    fun adaptivePadding(): Dp {
        return when {
            isSmallScreen() -> 16.dp
            isMediumScreen() -> 20.dp
            else -> 24.dp
        }
    }
}