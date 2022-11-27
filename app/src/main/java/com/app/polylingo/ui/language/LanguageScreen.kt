package com.app.polylingo.ui.language

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.app.polylingo.R
import com.app.polylingo.ui.components.LanguageScaffold
import com.app.polylingo.ui.navigation.Screen

@Composable
fun LanguageScreen(navController: NavController) {

    LanguageScaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LanguageScreenContent(
                modifier = Modifier.padding(8.dp),
                navController
            )
        }
    }
}

@Composable
private fun LanguageScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        var currentLanguage = remember { mutableStateOf(TextFieldValue("")) }
        var learningLanguage = remember { mutableStateOf(TextFieldValue("")) }

        var currentLanguageTextError = remember { mutableStateOf(false) }
        var learningLanguageTextError = remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(20.dp))


        TextField(
            value = currentLanguage.value,
            onValueChange = { newText ->
                currentLanguage.value = newText
                currentLanguageTextError.value = false
            },
            label = { Text(text = stringResource(id = R.string.enter_language)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = currentLanguageTextError.value
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = learningLanguage.value,
            onValueChange = { newText ->
                learningLanguage.value = newText
                learningLanguageTextError.value = false
            },
            label = { Text(text = stringResource(id = R.string.enter_learning_language)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = learningLanguageTextError.value
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // get the values from the textFields and set them to language names
                    if (currentLanguage.value.text.isEmpty()) {
                        currentLanguageTextError.value = true
                    }
                    if (learningLanguage.value.text.isEmpty()) {
                        learningLanguageTextError.value = true
                    }
                    if (!currentLanguageTextError.value && !learningLanguageTextError.value) {
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
                    Text(text = "Get Started")
                },
                modifier = Modifier.padding(top = 15.dp, end = 15.dp)
            )
        }
    }
}