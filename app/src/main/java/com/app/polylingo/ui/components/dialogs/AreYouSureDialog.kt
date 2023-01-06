package com.app.polylingo.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.app.polylingo.R

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