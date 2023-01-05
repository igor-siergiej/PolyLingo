package com.app.polylingo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.ui.components.scaffolds.MainScaffold
import com.app.polylingo.R
import com.app.polylingo.model.EntryViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    entryViewModel: EntryViewModel
) {
    val entries by entryViewModel.entryList.observeAsState(listOf())

    MainScaffold(
        navController = navController,
        titleText = stringResource(id = R.string.app_name)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (entries.isNotEmpty()) {
                HomeScreenContent(
                    modifier = Modifier.padding(8.dp),
                    dictionaryWords = entries.size
                )
            } else {
                //TODO create a message saying add some words in the dictionary to begin learning
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    dictionaryWords: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
        ) {
            Text(
                text = stringResource(id = R.string.words_in_dictionary) + "$dictionaryWords",
                modifier = Modifier.padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
            )
        }
        // TODO create more of these stats to put on home screen like
    }
}