package com.app.polylingo.ui.games

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.R
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFABAndOptions
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme

@Composable
fun GameConfigScreen(
    navController: NavHostController,
    gameType: String
) {
    MainScaffoldWithoutFABAndOptions(
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
    var numOfWordsSliderPosition by remember { mutableStateOf(3) }
    var timeSliderPosition by remember { mutableStateOf(30) }

    val context = LocalContext.current

    val gameNamesList = stringArrayResource(id = R.array.game_names_list).toList()
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(5.dp),
                imageVector = Icons.Filled.Psychology,
                contentDescription = stringResource(id = R.string.num_of_words)
            )
            Text(text = "Number of words: $numOfWordsSliderPosition")
        }

        Slider(
            value = numOfWordsSliderPosition.toFloat(),
            onValueChange = { numOfWordsSliderPosition = it.toInt() },
            valueRange = 3f..12f,
            steps = 2
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,) {
            Icon(
                modifier = Modifier.padding(5.dp),
                imageVector = Icons.Filled.Timer,
                contentDescription = stringResource(id = R.string.time_left_description)
            )
            Text(text = "Time to do test: $timeSliderPosition")
        }


        Slider(
            value = timeSliderPosition.toFloat(),
            onValueChange = { timeSliderPosition = it.toInt() },
            valueRange = 2f..120f,
            steps = 2
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // TODO check that the number of entries allows to play a game with a certain amount of words
                    when (gameType) {
                        gameNamesList[0] -> {
                            navController.navigate("${Screen.WordSearch.route}/${numOfWordsSliderPosition}/${timeSliderPosition}") {
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
                            navController.navigate("${Screen.MixAndMatch.route}/${numOfWordsSliderPosition}/${timeSliderPosition}") {
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
        val navController = rememberNavController()
        GameConfigScreen(gameType = "Test", navController = navController)
    }
}