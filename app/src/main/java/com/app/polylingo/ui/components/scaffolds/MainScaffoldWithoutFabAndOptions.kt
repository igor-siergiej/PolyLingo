package com.app.polylingo.ui.components.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.app.polylingo.ui.components.topAppBars.MainTopAppBarWithoutOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldWithoutFABAndOptions(
    navController: NavHostController,
    titleText: String,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MainTopAppBarWithoutOptions(titleText,navController)
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        },
    )
}