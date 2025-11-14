package com.example.android_meteo_app.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

sealed class Screen(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    object Home : Screen("home")
    object Details : Screen(
        route = "details/{latitude}/{longitude}/{name}",
        arguments = listOf(
            navArgument("latitude") { nullable = false },
            navArgument("longitude") { nullable = false },
            navArgument("name") { nullable = false }
        )
    ) {
        fun createRoute(latitude: Float, longitude: Float, name: String) = "details/$latitude/$longitude/$name"
    }
}
