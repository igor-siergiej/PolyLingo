package com.app.polylingo.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Dictionary : Screen("dictionary")
    object Games : Screen("games")
    object Language : Screen("language")
}

val screens = listOf(
    Screen.Home,
    Screen.Dictionary,
    Screen.Games
)

