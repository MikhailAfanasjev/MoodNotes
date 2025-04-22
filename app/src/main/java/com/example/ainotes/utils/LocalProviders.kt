package com.example.ainotes.utils


import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigationController = staticCompositionLocalOf<NavHostController> {
    error("NavController not found")
}