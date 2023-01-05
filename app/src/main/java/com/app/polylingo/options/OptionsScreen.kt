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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.datasource.fileStorage.LanguageViewModel
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFABAndOptions
import com.app.polylingo.ui.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun OptionsScreen(
    navController: NavHostController,
    entryViewModel: EntryViewModel,
    languageViewModel: LanguageViewModel
) {
    val coroutineScope = rememberCoroutineScope()

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
                deleteDictionary = {
                    coroutineScope.launch(Dispatchers.IO) {
                        entryViewModel.removeAll()
                        languageViewModel.deleteLanguages()
                    }

                    navController.navigate(Screen.Language.route) {
                        popUpTo(0)
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun OptionScreenContent(
    modifier: Modifier = Modifier,
    deleteDictionary: () -> Unit = {}
) {
    val audioManager = LocalContext.current.getSystemService(AUDIO_SERVICE) as AudioManager
    val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    val maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    val currentVolume = remember {
        mutableStateOf(volumeLevel)
    }

    var openAreYouSureDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.Speaker,
                contentDescription = stringResource(id = R.string.sound_volume),
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(text = stringResource(id = R.string.sound_volume))
            Text(text = ": " + currentVolume.value.toString())
        }

        Slider(
            value = currentVolume.value.toFloat(),
            valueRange = 0f..maxVolumeLevel.toFloat(),
            steps = maxVolumeLevel - 2, // minus 2 due to the beginning and ending positions in slider
            onValueChange = {
                currentVolume.value = it.toInt()

                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    currentVolume.value, 0 // Set volume to slider position
                )
            }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = stringResource(id = R.string.change_language),
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(text = stringResource(id = R.string.change_language))
        }

        FilledTonalButton(
            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
            onClick = {
                openAreYouSureDialog = true
            }) {
            Text(text = stringResource(id = R.string.change))
        }

        if (openAreYouSureDialog) {
            AreYouSureDialog(
                stringResource(R.string.back_dialog),
                stringResource(R.string.deleting_dictionary),
                setCloseDialog = {
                    openAreYouSureDialog = false
                },
                deleteDictionary = {
                    deleteDictionary()
                }
            )
        }
    }
}

@Composable
fun AreYouSureDialog(
    title: String,
    text: String,
    setCloseDialog: () -> Unit = {},
    deleteDictionary: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { setCloseDialog() },
        title = {
            Text(text = title, color = Color.Red)
        },
        text = {
            Text(text = text, color = Color.Red)
        },
        confirmButton = {
            Button(
                onClick = {
                    deleteDictionary()
                }) {
                Text(stringResource(id = R.string.sure))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    setCloseDialog()
                }) {
                Text(stringResource(id = R.string.nevermind))
            }
        }
    )
}