package com.app.polylingo.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.app.polylingo.R

@Composable
fun CreateBackDialog(
    navController: NavController,
    setCloseDialog: () -> Unit = {},
    resumeTimer: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { setCloseDialog() },
        title = {
            Text(text = stringResource(id = R.string.back_dialog), color = Color.Red)
        },
        text = {
            Text(text = stringResource(id = R.string.back_dialog_text), color = Color.Red)
        },
        confirmButton = {
            Button(
                onClick = {
                    navController.navigateUp()
                }) {
                Text(stringResource(id = R.string.sure))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    setCloseDialog()
                    resumeTimer()
                }) {
                Text(stringResource(id = R.string.nevermind))
            }
        }
    )
}