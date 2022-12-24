package com.app.polylingo.options

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFABAndOptions

@Composable
fun OptionsScreen(
    navController: NavHostController
) {
    MainScaffoldWithoutFABAndOptions(
        navController = navController,
        titleText = stringResource(id = R.string.options)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OptionScreenContent(
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun OptionScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        var soundSliderPosition by remember { mutableStateOf(0f) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.Speaker,
                contentDescription = stringResource(id = R.string.sound_volume)
            )
            Text(text = stringResource(id = R.string.sound_volume))
        }

        Slider(value = soundSliderPosition, onValueChange = { soundSliderPosition = it })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = Icons.Filled.Language,
                contentDescription = stringResource(id = R.string.change_language))
            Text(text = stringResource(id = R.string.change_language))
        }

        FilledTonalButton(onClick = { 
            //TODO pop up dialog to ask if user is sure then 
            // delete dictionary and languages and send to languages screen
        }) {
            Text(text = stringResource(id = R.string.change))
        }
    }
}