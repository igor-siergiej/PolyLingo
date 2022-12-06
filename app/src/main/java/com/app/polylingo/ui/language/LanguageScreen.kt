package com.app.polylingo.ui.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.LanguageScaffold
import com.app.polylingo.ui.dictionary.DictionaryScreen
import com.app.polylingo.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LanguageScreen(
    navController: NavController,
    viewModel: EntryViewModel
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
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun LanguageScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: EntryViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        var currentLanguage by remember { mutableStateOf("") }
        var learningLanguage by remember { mutableStateOf("") }
        var currentTextFieldError by remember { mutableStateOf(false) }
        var learningTextFieldError by remember { mutableStateOf(false) }

        val items = listOf(
            "Italian",
            "German",
            "Portuguese",
            "Portuguese",
            "Portuguese",
            "Portuguese",
            "Portuguese",
            "Portuguese",
            "Portuguese",
            "Portuguese"
        )

        Spacer(modifier = Modifier.height(100.dp))

        MyDropDownMenu(
            selectedItem = currentLanguage,
            changeText = {
                currentLanguage = it
                currentTextFieldError = false
            },
            items = items,
            textFieldError = currentTextFieldError,
            label = stringResource(id = R.string.enter_learning_language),
        )

        Spacer(modifier = Modifier.height(10.dp))

        MyDropDownMenu(
            selectedItem = learningLanguage,
            changeText = {
                learningLanguage = it
                learningTextFieldError = false

            },
            items = items,
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
                        var entry = Entry(
                           currentLanguage,
                            learningLanguage
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addEntry(entry)
                        }
                        navController.navigate(Screen.Home.route) {
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
fun MyDropDownMenu(
    selectedItem: String,
    changeText: (String) -> Unit = {},
    items: List<String>,
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

    val filteringOptions =
        items.filter { it.contains(selectedItem, ignoreCase = true) }

    DropdownMenu(
        properties = PopupProperties(focusable = false),
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            .requiredSizeIn(maxHeight = 200.dp)
    ) {
        filteringOptions.forEach { string ->
            DropdownMenuItem(onClick = {
                changeText(string)
                expanded = false
                keyboardController?.hide()
            },
                text = { Text(text = string) })
        }
    }
}
