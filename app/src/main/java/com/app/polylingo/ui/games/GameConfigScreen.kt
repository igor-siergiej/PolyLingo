package com.app.polylingo.ui.games

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.R
import com.app.polylingo.ui.components.MainTopBar
import com.app.polylingo.ui.components.scaffolds.MainScaffold
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFAB
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme
import kotlinx.coroutines.launch

@Composable
fun GameConfigScreen(
    navController: NavHostController,
    gameType: String
) {

    MainScaffoldWithoutFAB(
        navController = navController,
        titleText = "$gameType Config"
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GameConfigScreenContent(
                modifier = Modifier.padding(8.dp),
                navController = navController,
                gameType = gameType
            )
        }
    }
}

@Composable
private fun GameConfigScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    gameType: String
) {
    var numOfWordsSliderPosition by remember { mutableStateOf(4f) }
    var timeSliderPosition by remember { mutableStateOf(30f) }

    val context = LocalContext.current

    val gameNamesList = stringArrayResource(id = R.array.game_names_list).toList()
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = "Number of words: $numOfWordsSliderPosition")

        Slider(
            value = numOfWordsSliderPosition,
            onValueChange = { numOfWordsSliderPosition = it },
            valueRange = 4f..8f,
            steps = 3
        )

        Text(text = "Time to do test: $timeSliderPosition")

        Slider(
            value = timeSliderPosition,
            onValueChange = { timeSliderPosition = it },
            valueRange = 30f..120f,
            steps = 4
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    when (gameType) {
                        gameNamesList[0] -> {
                            navController.navigate("${Screen.WordSearch.route}/${numOfWordsSliderPosition.toInt()}/${timeSliderPosition.toInt()}") {
                                // this should be navigating without being able to go back
                                popUpTo(Screen.Games.route)
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }

                        gameNamesList[1] -> {
                            navController.navigate("${Screen.MixAndMatch.route}/${numOfWordsSliderPosition.toInt()}/${timeSliderPosition.toInt()}") {
                                // this should be navigating without being able to go back
                                popUpTo(Screen.Games.route)
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }

                        gameNamesList[2] -> {
                            Toast.makeText(context, gameNamesList[2], Toast.LENGTH_SHORT).show()
                        }
                    }

                },
                content = { Text(text = "Ready!") }
            )
        }
    }
}

@Preview
@Composable
private fun GameConfigScreenPreview() {
    PolyLingoTheme(dynamicColor = false) {
        var navController = rememberNavController()
        GameConfigScreen(gameType = "Test", navController = navController)
    }
}