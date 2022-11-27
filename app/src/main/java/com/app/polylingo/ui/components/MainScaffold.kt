package com.app.polylingo.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

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
        }
    )
}
