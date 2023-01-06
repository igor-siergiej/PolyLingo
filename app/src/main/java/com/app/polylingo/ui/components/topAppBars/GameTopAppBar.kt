package com.app.polylingo.ui.components.topAppBars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.polylingo.R
import com.app.polylingo.ui.components.dialogs.CreateBackDialog
import com.app.polylingo.ui.components.dialogs.CreateTipDialog
import com.app.polylingo.ui.games.Timer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopAppBar(
    titleText: String,
    tipText: String,
    navController: NavController,
    timer: Timer
) {
    var openTipDialog by remember { mutableStateOf(false) }
    var openBackDialog by remember { mutableStateOf(false) }

    if (openTipDialog) {
        timer.pauseTimer()
        CreateTipDialog(tipText = tipText, setCloseDialog = { openTipDialog = false }, resumeTimer = {timer.resumeTimer()})
    }

    if (openBackDialog) {
        timer.pauseTimer()
        CreateBackDialog(setCloseDialog = { openBackDialog = false }, navController = navController, resumeTimer = {timer.resumeTimer()})

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
                }) {
                Icon(
                    imageVector = Icons.Filled.HelpOutline,
                    contentDescription = stringResource(id = R.string.help)
                )
            }
        }
    )
}