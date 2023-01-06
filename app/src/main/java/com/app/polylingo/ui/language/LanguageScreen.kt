package com.app.polylingo.ui.language

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.polylingo.R
import com.app.polylingo.datasource.fileStorage.LanguageViewModel
import com.app.polylingo.ui.components.DropDownTextField
import com.app.polylingo.ui.components.scaffolds.LanguageScaffold
import com.app.polylingo.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun LanguageScreenTopLevel(
    navController: NavController,
    languageViewModel: LanguageViewModel
) {
    LanguageScreen(
        navController = navController,
        saveLanguages = {currentLanguage, learningLanguage ->
        CoroutineScope(Dispatchers.IO).launch {
            languageViewModel.saveLanguages(currentLanguage,learningLanguage)
        }
    } )

}

@Composable
fun LanguageScreen(
    navController: NavController,
    saveLanguages: (currentLanguage: String, learningLanguage: String) -> Unit = { _: String, _: String -> }
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
                saveLanguages = saveLanguages
            )
        }
    }
}

@Composable
private fun LanguageScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    saveLanguages: (currentLanguage: String, learningLanguage: String) -> Unit = { _: String, _: String -> }
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

                    if (!currentTextFieldError && !learningTextFieldError && currentLanguage != learningLanguage) {
                        // saving selected languages to file
                        saveLanguages(currentLanguage,learningLanguage)

                        navController.navigate(Screen.Home.route) {
                            // this should be navigating without being able to go back
                            popUpTo(0) {
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

