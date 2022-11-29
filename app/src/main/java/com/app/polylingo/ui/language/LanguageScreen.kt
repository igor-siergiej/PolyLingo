package com.app.polylingo.ui.language

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.app.polylingo.R
import com.app.polylingo.ui.components.LanguageScaffold
import com.app.polylingo.ui.navigation.Screen

@Composable
fun MyUI() {
    var selectedItem by remember { mutableStateOf("") }
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
    MyDropDownMenu(
        selectedItem = selectedItem,
        changeText = { selectedItem = it },
        items = items
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyDropDownMenu(
    selectedItem :String,
    changeText : (String) -> Unit = {},
    items: List<String>
) {

    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
    var offset = DpOffset.Zero

    // THIS WORKS
    // CHANGE THIS SLOWLY TO THE EDITABLE BOX

    OutlinedTextField(
        value = selectedItem,
        onValueChange = {
            changeText(it)
            expanded = !expanded
        },
        modifier = Modifier.onGloballyPositioned { coordinates ->
            mTextFieldSize = coordinates.size.toSize()
        }
    )
    val filteringOptions =
        items.filter { it.contains(selectedItem, ignoreCase = true) }
    DropdownMenu(
        offset = offset,
        properties = PopupProperties(focusable = false),
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
            .background(
                Color.Red
            )
            .requiredSizeIn(maxHeight = 200.dp)
    ) {
        if (filteringOptions.isEmpty()) {
            expanded = false
        } else {
            if (filteringOptions.size == 1){
                offset = DpOffset(x = 0.dp,y = (300).dp)
            }
            filteringOptions.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    changeText(s)
                    expanded = false
                    keyboardController?.hide()
                },
                    text = { Text(text = s) })
            }
        }
    }
}


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

@OptIn(ExperimentalMaterial3Api::class)
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

        //CHANGE THIS TO OUTLINED WHICH HAS SUPPORTING TEXT
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

        MyUI()
    }
}
