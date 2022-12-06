package com.app.polylingo.ui.addWord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.ui.components.MainScaffold
import com.app.polylingo.R
import com.app.polylingo.model.Entry

@Composable
fun AddWordScreen(navController: NavHostController) {

    MainScaffold(
        navController = navController,
        titleText = stringResource(id = R.string.add_entry)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AddWordScreenContent(
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun AddWordScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

    }
}