package com.app.polylingo.ui.components.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.app.polylingo.R

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.ui.components.MainBottomBar
import com.app.polylingo.ui.components.topAppBars.MainTopBar
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    titleText: String,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MainTopBar(titleText, navController)
        },
        bottomBar = {
            MainBottomBar(navController)
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddWord.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_entry)
                )
                Text(text = stringResource(id = R.string.add_entry))
            }
        },
    )
}

@Preview
@Composable
private fun MainScaffoldPreview() {
    PolyLingoTheme(dynamicColor = false) {
        val navController = rememberNavController()
        MainScaffold(navController = navController, titleText = "test", pageContent = {})
    }
}