package com.app.polylingo.ui.components.topAppBars

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyTopBar(
    titleText: String
) {
    CenterAlignedTopAppBar(
        title = { Text(titleText) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        ),
    )
}