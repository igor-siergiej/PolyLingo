package com.app.polylingo.options

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val audioManager = LocalContext.current.getSystemService(AUDIO_SERVICE) as AudioManager
    val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    val maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    val currentVolume = remember {
        mutableStateOf(volumeLevel)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.Speaker,
                contentDescription = stringResource(id = R.string.sound_volume)
            )
            Text(text = stringResource(id = R.string.sound_volume))
            Text(text = currentVolume.value.toString())
        }

        Slider(
            value = currentVolume.value.toFloat(),
            valueRange = 0f..maxVolumeLevel.toFloat(),
            steps = maxVolumeLevel - 2, // minus 2 due to the beginning and ending positions in slider
            onValueChange = {
                currentVolume.value = it.toInt()

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                currentVolume.value,0 // Set volume to slider position
            )
        })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = stringResource(id = R.string.change_language)
            )
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