package com.app.polylingo.ui.components.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.app.polylingo.ui.components.topAppBars.EmptyTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameReviewScaffold(
    titleText: String,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            EmptyTopBar(titleText)
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        },
    )
}