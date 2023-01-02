package com.app.polylingo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.R
import com.app.polylingo.ui.components.scaffolds.LanguageScaffold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarWithoutOptions(
    titleText: String,
    navController: NavController
) {
    CenterAlignedTopAppBar(
        title = { Text(titleText) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        ),
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

//TODO should probably be in its own file
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(
    titleText: String,
    tipText: String,
    navController: NavController
) {
    var openTipDialog by remember { mutableStateOf(false) }
    var openBackDialog by remember { mutableStateOf(false) }

    if (openTipDialog) {
        CreateTipDialog(tipText = tipText, setCloseDialog = { openTipDialog = false })
    }

    if (openBackDialog) {
        CreateBackDialog(setCloseDialog = { openBackDialog = false }, navController = navController)
    }
    CenterAlignedTopAppBar(
        title = { Text(titleText) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        ),
        navigationIcon = {
            FilledIconButton(
                onClick = {
                    openBackDialog = true
                }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
        actions = {
            FilledIconButton(
                onClick = {
                    openTipDialog = true
                    // Create alert dialog with help tips
                }) {
                Icon(
                    imageVector = Icons.Filled.HelpOutline,
                    contentDescription = stringResource(id = R.string.help)
                )
            }
        }
    )
}

//Todo probably should be in separate file
@Composable
fun CreateTipDialog(
    tipText: String,
    setCloseDialog: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { setCloseDialog() },
        title = {
            Text(text = stringResource(id = R.string.hint))
        },
        text = {
            Text(text = tipText)
        },
        confirmButton = {
            Button(
                onClick = {
                    setCloseDialog()
                }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
    )
}

@Composable
fun CreateBackDialog(
    navController: NavController,
    setCloseDialog: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { setCloseDialog() },
        title = {
            Text(text = stringResource(id = R.string.back_dialog), color = Color.Red)
        },
        text = {
            Text(text = stringResource(id = R.string.back_dialog_text), color = Color.Red)
        },
        confirmButton = {
            Button(
                onClick = {
                    navController.navigateUp()
                }) {
                Text(stringResource(id = R.string.sure))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    setCloseDialog()
                }) {
                Text(stringResource(id = R.string.nevermind))
            }
        }
    )
}

@Preview
@Composable
private fun MainTopBarPreview() {
    PolyLingoTheme(dynamicColor = false) {
        var navController = rememberNavController()
        GameTopBar(titleText = "Test", "tt", navController)
    }
}