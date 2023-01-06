package com.app.polylingo.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.R
import com.app.polylingo.ui.components.scaffolds.GameReviewScaffold
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme

@Composable
fun GameReviewScreen(
    navController: NavHostController,
    timeLeft :Int,
    wordsMatchedCorrectly: Int,
    wordsMatchedIncorrectly: Int
) {
    GameReviewScaffold(
        titleText = stringResource(id = R.string.game_review)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GameReviewScreenContent(
                modifier = Modifier.padding(8.dp),
                navController = navController,
                timeLeft = timeLeft,
                wordsMatchedCorrectly = wordsMatchedCorrectly,
                wordsMatchedIncorrectly = wordsMatchedIncorrectly
            )
        }
    }
}

@Composable
private fun GameReviewScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    timeLeft :Int,
    wordsMatchedCorrectly: Int,
    wordsMatchedIncorrectly: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
        ) {
            Text(
                text = stringResource(id = R.string.time_left) + "$timeLeft Seconds",
                modifier = Modifier.padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
        ) {
            Text(
                text = stringResource(id = R.string.words_matched_correctly) + "$wordsMatchedCorrectly",
                modifier = Modifier.padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
        ) {
            Text(
                text = stringResource(id = R.string.words_matched_incorrectly) + "$wordsMatchedIncorrectly",
                modifier = Modifier.padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        // this should be navigating without being able to go back
                        popUpTo(0)
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                content = { Text(text = stringResource(id = R.string.completed)) }
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