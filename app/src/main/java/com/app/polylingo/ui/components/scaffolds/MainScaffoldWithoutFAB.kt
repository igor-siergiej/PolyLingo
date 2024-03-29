package com.app.polylingo.ui.components.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.ui.components.*
import com.app.polylingo.ui.components.topAppBars.MainTopBar
import com.app.polylingo.ui.theme.PolyLingoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldWithoutFAB(
    navController: NavHostController,
    titleText: String,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MainTopBar(titleText,navController)
        },
        bottomBar = {
            MainBottomBar(navController)
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        },
    )
}

@Preview
@Composable
private fun MainScaffoldWithoutFABPreview() {
    PolyLingoTheme(dynamicColor = false) {
        val navController = rememberNavController()
        MainScaffoldWithoutFAB(navController = navController, titleText = "test", pageContent = {})
    }
}