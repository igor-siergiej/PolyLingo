package com.app.polylingo.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.polylingo.R

@Composable
fun CreateTipDialog(
    tipText: String,
    setCloseDialog: () -> Unit = {},
    resumeTimer: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { setCloseDialog() },
        title = {
            Text(text = stringResource(id = R.string.hint))
        },
        text = {
            Text(text = tipText)
        },
        confirmButton = {
            Button(
                onClick = {
                    setCloseDialog()
                    resumeTimer()
                }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
    )
}