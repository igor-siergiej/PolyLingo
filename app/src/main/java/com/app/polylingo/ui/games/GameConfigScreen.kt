package com.app.polylingo.ui.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.ui.components.MainScaffold
import com.app.polylingo.ui.navigation.Screen

@Composable
fun GameConfigScreen(
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {

    MainScaffold(
        navController = navController,
        titleText = stringResource(id = R.string.games)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GamesScreenContent(
                modifier = Modifier.padding(8.dp),
                navController = navController,
                numOfWords = numOfWords,
                time = time
            )
        }
    }
}

@Composable
private fun GamesScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    Card {
        Text(text = numOfWords.toString())
        Text(text = time.toString())
    }
}