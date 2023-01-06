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
// TODO create GameReviewScreen with parameters like time left,
// TODO incorrectly matched words, correctly matched words
// TODO create these variables in the games and pass them through
// TODO user should be sent to review screen if they fail or not

val screens = listOf(
    Screen.Home,
    Screen.Dictionary,
    Screen.Games
)

