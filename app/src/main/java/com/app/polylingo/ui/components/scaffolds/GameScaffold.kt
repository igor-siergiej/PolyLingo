package com.app.polylingo.ui.components.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.app.polylingo.ui.components.topAppBars.GameTopAppBar
import com.app.polylingo.ui.games.Timer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScaffold(
    navController: NavHostController,
    titleText: String,
    tipText: String,
    timer: Timer,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            GameTopAppBar(titleText,tipText,navController,timer)
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        },
    )
}