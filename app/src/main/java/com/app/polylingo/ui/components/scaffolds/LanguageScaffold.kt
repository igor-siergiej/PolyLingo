package com.app.polylingo.ui.components.scaffolds

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.polylingo.R
import com.app.polylingo.ui.theme.PolyLingoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScaffold(
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            Card(
                shape = RectangleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(152.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(stringResource(id = R.string.welcome),
                        textAlign = TextAlign.Center,
                        style = typography.displayMedium)
                }
            }
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        }
    )
}

@Preview
@Composable
private fun LanguageScaffoldPreview() {
    PolyLingoTheme(dynamicColor = false) {
        LanguageScaffold(pageContent = {})
    }
}