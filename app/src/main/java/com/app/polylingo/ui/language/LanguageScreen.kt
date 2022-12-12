package com.app.polylingo.ui.language

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.app.polylingo.R
import com.app.polylingo.datasource.fileStorage.LanguageViewModel
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.LanguageScaffold
import com.app.polylingo.ui.navigation.Screen
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*
import kotlin.collections.ArrayList


//TODO create top level
@Composable
fun LanguageScreen(
    navController: NavController,languageViewModel: LanguageViewModel

) {
    LanguageScaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LanguageScreenContent(
                modifier = Modifier.padding(8.dp),
                navController = navController,
                languageViewModel = languageViewModel
            )
        }
    }
}

@Composable
private fun LanguageScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    languageViewModel: LanguageViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        var currentLanguage by remember { mutableStateOf("") }
        var learningLanguage by remember { mutableStateOf("") }
        var currentTextFieldError by remember { mutableStateOf(false) }
        var learningTextFieldError by remember { mutableStateOf(false) }


        // this will get all of the current unique languages from the available locales
        var languages = ArrayList<String>()
        Locale.getAvailableLocales().forEach {
            languages.add(it.displayLanguage)
        }
        languages = languages.distinct() as ArrayList<String>

        Spacer(modifier = Modifier.height(100.dp))

        DropDownTextField(
            selectedItem = currentLanguage,
            changeText = {
                currentLanguage = it
                currentTextFieldError = false
            },
            languages = languages,
            textFieldError = currentTextFieldError,
            label = stringResource(id = R.string.enter_learning_language)
        )

        Spacer(modifier = Modifier.height(10.dp))

        DropDownTextField(
            selectedItem = learningLanguage,
            changeText = {
                learningLanguage = it
                learningTextFieldError = false

            },
            languages = languages,
            textFieldError = learningTextFieldError,
            label = stringResource(id = R.string.enter_language)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // get the values from the textFields and set them to language names
                    if (currentLanguage.isEmpty()) {
                        currentTextFieldError = true
                    }
                    if (learningLanguage.isEmpty()) {
                        learningTextFieldError = true
                    }
                    if (!currentTextFieldError && !learningTextFieldError) {
                        // saving selected languages to file
                        CoroutineScope(Dispatchers.IO).launch {
                            languageViewModel.saveLanguages(currentLanguage,learningLanguage)
                        }
                        navController.navigate(Screen.Home.route) {
                            // this should be navigating without being able to go back
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                content = {
                    Text(text = stringResource(id = R.string.get_started_button))
                },
                modifier = Modifier.padding(top = 15.dp, end = 15.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextField(
    selectedItem: String,
    changeText: (String) -> Unit = {},
    languages: List<String>,
    textFieldError: Boolean,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    var supportingText = stringResource(id = R.string.language_error_message)
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = selectedItem,
        onValueChange = {
            changeText(it)
            expanded = !expanded
        },
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size.toSize()
            }
            .fillMaxWidth(),
        isError = textFieldError,
        label = { Text(label) },
        singleLine = true,
        supportingText = {
            if (textFieldError) {
                Text(supportingText)
            } else {
                Text("")
            }
        }
    )

    var filteredLanguages: List<String> =
        languages.filter { it.contains(selectedItem, ignoreCase = true) }

    DropdownMenu(
        properties = PopupProperties(focusable = false),
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            .requiredSizeIn(maxHeight = 200.dp)
    ) {
        filteredLanguages.take(20).forEach { language ->
            DropdownMenuItem(onClick = {
                changeText(language)
                expanded = false
                keyboardController?.hide()
            },
                text = { Text(text = language) })
        }
    }
}