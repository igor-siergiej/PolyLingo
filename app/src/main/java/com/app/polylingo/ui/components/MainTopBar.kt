package com.app.polylingo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.polylingo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    titleText : String
){
    CenterAlignedTopAppBar(
        title = { Text(titleText) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),

        actions = {
            FilledIconButton(onClick = { Unit }/* here there should be an onclick to open the settings dialog*/) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        },

    )

}