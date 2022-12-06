package com.app.polylingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.addWord.AddWordScreen
import com.app.polylingo.ui.dictionary.DictionaryScreenTopLevel
import com.app.polylingo.ui.games.GamesScreen
import com.app.polylingo.ui.home.HomeScreen
import com.app.polylingo.ui.language.LanguageScreen
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme

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
    entryViewModel: EntryViewModel = viewModel()
) {
    // The NavController is in a place where all
    // our composables can access it.
    val navController = rememberNavController()

    var test = entryViewModel.entryList.value

    // Each NavController is associated with a NavHost.
    // This links the NavController with a navigation graph.
    // As we navigate between composables the content of
    // the NavHost is automatically recomposed.
    // Each composable destination in the graph is associated with a route.
    var startingDestination = Screen.Home.route;
    if (entryViewModel.entryList.value?.size == 0) {
        startingDestination = Screen.Language.route
    }
    NavHost(
        navController = navController,
        startDestination = startingDestination
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Dictionary.route) { DictionaryScreenTopLevel(navController,entryViewModel) }
        composable(Screen.Games.route) { GamesScreen(navController)}
        composable(Screen.Language.route) { LanguageScreen(navController,entryViewModel)}
        composable(Screen.AddWord.route) { AddWordScreen(navController)}
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PolyLingoTheme {
        BuildNavigationGraph()
    }
}