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
import com.app.polylingo.ui.components.scaffolds.MainScaffold
import com.app.polylingo.ui.navigation.Screen

@Composable
fun GamesScreen(navController: NavHostController) {

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
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamesScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val gameNamesList = stringArrayResource(id = R.array.game_names_list).toList()
    val gameDescriptionList = stringArrayResource(id = R.array.game_description_list).toList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        gameNamesList.forEachIndexed { index, gameName ->
            ListItem(
                headlineText = { Text(gameNamesList[index]) },
                supportingText = { Text(gameDescriptionList[index]) },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable {
                        navigateToGamesConfigScreen(navController,gameName)
                    }
            )
        }

    }
}

fun navigateToGamesConfigScreen(navController: NavHostController,  gameType: String) {
    navController.navigate("${Screen.GameConfig.route}/$gameType") {
        // this should be navigating without being able to go back
        popUpTo(Screen.Games.route)
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}