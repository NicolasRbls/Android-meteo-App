package com.example.android_meteo_app.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

sealed class Screen(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    object Home : Screen("home")
    object Details : Screen(
        route = "details/{cityId}/{latitude}/{longitude}/{name}",
        arguments = listOf(
            navArgument("cityId") { nullable = false },
            navArgument("latitude") { nullable = false },
            navArgument("longitude") { nullable = false },
            navArgument("name") { nullable = false }
        )
    ) {
        fun createRoute(cityId: Int, latitude: Float, longitude: Float, name: String) = "details/$cityId/$latitude/$longitude/$name"
    }
}
