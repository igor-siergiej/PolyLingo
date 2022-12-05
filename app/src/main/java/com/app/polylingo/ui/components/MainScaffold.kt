package com.app.polylingo.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.app.polylingo.R

import androidx.navigation.NavHostController
import com.app.polylingo.ui.addWord.AddWordScreen
import com.app.polylingo.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {

    Scaffold(
        topBar = {
            MainTopBar()
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
                    /*coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Add cat",
                            actionLabel = "Undo"
                        )
                    }*/
                    navController.navigate(Screen.AddWord.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_word)
                )
                Text(text = stringResource(id = R.string.add_word))
            }
        },
    )
}
