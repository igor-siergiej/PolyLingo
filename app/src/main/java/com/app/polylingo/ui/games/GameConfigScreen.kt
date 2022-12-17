package com.app.polylingo.ui.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.ui.components.scaffolds.MainScaffold

@Composable
fun GameConfigScreen(
    navController: NavHostController,
    gameType: String
) {

    MainScaffold(
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
    var numOfWordsSlider by remember { mutableStateOf(4f) }
    var timeSliderPosition by remember { mutableStateOf(30f) }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = numOfWordsSlider.toString())

        Slider(
        value = numOfWordsSlider,
            onValueChange = { numOfWordsSlider = it },
            valueRange = 4f..8f,
            steps = 3
        )

        Text(text = timeSliderPosition.toString())

        Slider(
            value = timeSliderPosition,
            onValueChange = { timeSliderPosition = it },
            valueRange = 30f..120f,
            steps = 3
        )
    }
}