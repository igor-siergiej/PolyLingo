package com.app.polylingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.polylingo.datasource.fileStorage.LanguageViewModel
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.options.OptionsScreen
import com.app.polylingo.ui.addWord.AddWordScreen
import com.app.polylingo.ui.dictionary.DictionaryScreenTopLevel
import com.app.polylingo.ui.games.*
import com.app.polylingo.ui.home.HomeScreen
import com.app.polylingo.ui.language.LanguageScreen
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolyLingoTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BuildNavigationGraph()
                }
            }
        }
    }
}

@Composable
private fun BuildNavigationGraph(
    entryViewModel: EntryViewModel = viewModel(),
    languageViewModel: LanguageViewModel = viewModel()
) {
    // The NavController is in a place where all
    // our composables can access it.
    val navController = rememberNavController()

    //testing word bank
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            entryViewModel.removeAll()
            entryViewModel.addEntry(Entry("Mantequilla", "Butter"))
            entryViewModel.addEntry(Entry("Cafe", "Coffee"))
            entryViewModel.addEntry(Entry("Perro", "Dog"))
            entryViewModel.addEntry(Entry("Gato", "Cat"))
            entryViewModel.addEntry(Entry("Chaqueta", "Jacket"))
            entryViewModel.addEntry(Entry("Marmelada", "Jam"))
            entryViewModel.addEntry(Entry("Pan", "Bread"))
            entryViewModel.addEntry(Entry("Hijo", "Son"))
            entryViewModel.addEntry(Entry("Abuela", "Grandmother"))
            entryViewModel.addEntry(Entry("Rojo", "Red"))
            entryViewModel.addEntry(Entry("Negro", "Black"))
            entryViewModel.addEntry(Entry("Verde", "Green"))
            entryViewModel.addEntry(Entry("Hija", "Daughter"))
        }
    }

    // Each NavController is associated with a NavHost.
    // This links the NavController with a navigation graph.
    // As we navigate between composables the content of
    // the NavHost is automatically recomposed.
    // Each composable destination in the graph is associated with a route.
    var startingDestination = Screen.Language.route
    if (languageViewModel.doesFileExist()) {
        startingDestination = Screen.Home.route
        languageViewModel.readLanguages()
    }
    NavHost(
        navController = navController,
        startDestination = startingDestination
    ) {
        composable(Screen.Home.route) { HomeScreen(navController, entryViewModel) }

        composable(Screen.Dictionary.route) {
            DictionaryScreenTopLevel(
                navController,
                entryViewModel,
                languageViewModel
            )
        }

        composable(Screen.Games.route) { GamesScreen(navController) }

        composable(Screen.Language.route) { LanguageScreen(navController, languageViewModel) }

        composable(Screen.AddWord.route) {
            AddWordScreen(
                navController,
                entryViewModel,
                languageViewModel
            )
        }

        composable(
            route = "${Screen.WordSearch.route}/{numOfWords}/{time}",
            arguments = listOf(
                navArgument("numOfWords") { type = NavType.IntType },
                (navArgument("time") { type = NavType.IntType })
            )
        ) { backStackEntry ->
            WordSearchScreen(
                entryViewModel = entryViewModel,
                navController = navController,
                numOfWords = backStackEntry.arguments?.getInt("numOfWords")!!,
                time = backStackEntry.arguments?.getInt("time")!!
            )
        }

        composable(
            route = "${Screen.MixAndMatch.route}/{numOfWords}/{time}",
            arguments = listOf(
                navArgument("numOfWords") { type = NavType.IntType },
                (navArgument("time") { type = NavType.IntType })
            )
        ) { backStackEntry ->
            MixAndMatchScreen(
                entryViewModel = entryViewModel,
                navController = navController,
                numOfWords = backStackEntry.arguments?.getInt("numOfWords")!!,
                time = backStackEntry.arguments?.getInt("time")!!
            )
        }

        composable(
            route = "${Screen.WordOrder.route}/{numOfWords}/{time}",
            arguments = listOf(
                navArgument("numOfWords") { type = NavType.IntType },
                (navArgument("time") { type = NavType.IntType })
            )
        ) { backStackEntry ->
            WordOrderScreen(
                entryViewModel = entryViewModel,
                navController = navController,
                numOfWords = backStackEntry.arguments?.getInt("numOfWords")!!,
                time = backStackEntry.arguments?.getInt("time")!!
            )
        }

        composable(
            route = "${Screen.GameConfig.route}/{gameType}",
            arguments = listOf(
                navArgument("gameType") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            GameConfigScreen(
                navController = navController,
                gameType = backStackEntry.arguments?.getString("gameType")!!,
            )
        }

        composable(Screen.Options.route) { OptionsScreen(navController,entryViewModel,languageViewModel) }
    }
}