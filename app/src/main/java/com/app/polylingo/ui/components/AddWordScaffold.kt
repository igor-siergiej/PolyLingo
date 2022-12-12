package com.app.polylingo.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScaffold(
    navController: NavHostController,
    titleText: String,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MainTopBar(titleText)
        },
        bottomBar = {
            MainBottomBar(navController)
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        },
    )
}
