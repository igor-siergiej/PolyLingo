package com.app.polylingo.ui.components.topAppBars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.R
import com.app.polylingo.ui.games.Timer
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    titleText: String,
    navController: NavController
) {
    CenterAlignedTopAppBar(
        title = { Text(titleText) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        ),
        actions = {
            FilledIconButton(
                onClick = {
                    navController.navigate(Screen.Options.route) {
                        // this should be navigating without being able to go back

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }/* here there should be an onclick to open the settings screen*/
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        },
        navigationIcon = {
            FilledIconButton(
                onClick = {
                    navController.navigateUp()
                }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        }
    )
}

@Preview
@Composable
private fun MainTopBarPreview() {
    PolyLingoTheme(dynamicColor = false) {
        val navController = rememberNavController()
        GameTopAppBar(titleText = "Test", "tt", navController, Timer())
    }
}