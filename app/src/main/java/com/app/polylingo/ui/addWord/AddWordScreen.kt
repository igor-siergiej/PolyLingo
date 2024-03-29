package com.app.polylingo.ui.addWord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.polylingo.R
import com.app.polylingo.datasource.fileStorage.LanguageViewModel
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFAB
import com.app.polylingo.ui.navigation.Screen
import com.app.polylingo.ui.theme.PolyLingoTheme

@Composable
fun AddWordScreenTopLevel(
    navController: NavHostController,
    entryViewModel: EntryViewModel,
    languageViewModel: LanguageViewModel
) {
    AddWordScreen(
        navController = navController,
        languages = languageViewModel.readLanguages(),
        insertEntry = { newEntry ->
            entryViewModel.addEntry(newEntry)
        }
    )
}

@Composable
fun AddWordScreen(
    navController: NavHostController,
    languages: Pair<String, String>,
    insertEntry: (Entry) -> Unit = {}
) {
    MainScaffoldWithoutFAB(
        navController = navController,
        titleText = stringResource(id = R.string.add_entry)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AddWordScreenContent(
                modifier = Modifier.padding(8.dp),
                navController = navController,
                languages = languages,
                insertEntry = insertEntry
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWordScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    languages: Pair<String, String>,
    insertEntry: (Entry) -> Unit = {}
) {
    var word by remember { mutableStateOf("") }
    var translatedWord by remember { mutableStateOf("") }
    var currentTextFieldError by remember { mutableStateOf(false) }
    var learningTextFieldError by remember { mutableStateOf(false) }

    val supportingText = stringResource(id = R.string.please_enter_word)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = word,
            onValueChange = {
                word = it
                currentTextFieldError = false
            },
            label = {
                Text(
                    text = stringResource(id = R.string.enter_word)
                            + " " + languages.first
                )
            },
            isError = currentTextFieldError,
            supportingText = {
                if (currentTextFieldError) {
                    Text(supportingText)
                } else {
                    Text("")
                }
            }
        )

        OutlinedTextField(
            value = translatedWord,
            onValueChange = {
                translatedWord = it
                learningTextFieldError = false
            },
            label = {
                Text(
                    text = stringResource(id = R.string.enter_word)
                            + " " + languages.second
                )
            },
            isError = learningTextFieldError,
            supportingText = {
                if (learningTextFieldError) {
                    Text(supportingText)
                } else {
                    Text("")
                }
            }
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // get the values from the textFields and set them to language names
                    if (word.isEmpty()) {
                        currentTextFieldError = true

                    }
                    if (translatedWord.isEmpty()) {
                        learningTextFieldError = true
                    }
                    //TODO robustness: catch exception when word is already in the dictionary
                    if (!currentTextFieldError && !learningTextFieldError && word != translatedWord) {
                        insertEntry(Entry(word, translatedWord))
                        navController.navigate(Screen.Dictionary.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    } else {
                        currentTextFieldError = true
                        learningTextFieldError = true
                    }
                },
                content = {
                    Text(text = stringResource(id = R.string.add_word))
                },
                modifier = Modifier.padding(top = 15.dp, end = 15.dp)
            )
        }
    }
}

@Preview
@Composable
private fun AddWordScreenPreview() {
    PolyLingoTheme(dynamicColor = false) {
        val navController = rememberNavController()
        AddWordScreen(
            navController,
            "Preview" to "Preview",
        )
    }
}