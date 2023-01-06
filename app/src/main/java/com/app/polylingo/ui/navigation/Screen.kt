package com.app.polylingo.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Dictionary : Screen("dictionary")
    object Games : Screen("games")
    object Language : Screen("language")
    object AddWord : Screen("addWord")
    object WordSearch : Screen("wordSearch")
    object MixAndMatch: Screen("mixAndMatch")
    object WordOrder: Screen("wordOrder")
    object GameConfig: Screen("gameConfig")
    object Options: Screen("options")
    object GameReview: Screen("gameReview")
}

val screens = listOf(
    Screen.Home,
    Screen.Dictionary,
    Screen.Games
)

