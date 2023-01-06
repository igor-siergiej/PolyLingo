package com.app.polylingo.ui.components.topAppBars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.polylingo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBarWithoutOptions(
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