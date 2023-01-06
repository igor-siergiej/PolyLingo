package com.app.polylingo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.app.polylingo.R

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

    val supportingText = stringResource(id = R.string.language_error_message)
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

    val filteredLanguages: List<String> =
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