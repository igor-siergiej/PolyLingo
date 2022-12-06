package com.app.polylingo.ui.dictionary

import android.util.Log
import android.util.Log.i
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.MainScaffold

@Composable
fun DictionaryScreenTopLevel(
    navController: NavHostController,
    entryViewModel: EntryViewModel = viewModel()
) {
    val entries by entryViewModel.entryList.observeAsState(listOf())
    DictionaryScreen(
        navController = navController,
        entries = entries
    )
}

@Composable
fun DictionaryScreen(
    navController: NavHostController,
    entries: List<Entry>
) {
    MainScaffold(
        navController = navController,
        titleText = stringResource(id = R.string.dictionary)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            DictionaryScreenContent(
                modifier = Modifier.padding(8.dp),
                entries = entries
            )
        }
    }
}

@Composable
private fun DictionaryScreenContent(
    modifier: Modifier = Modifier,
    entries: List<Entry>
) {
    DictionaryColumn(entryList = entries)
}

@Composable
fun DictionaryColumn(entryList: List<Entry>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (entryList.isNotEmpty()) {
                    Text(
                        entryList[1].word,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()  //fill the max height
                            .width(1.dp)
                    )
                    Text(
                        entryList[1].translatedWord,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            if (entryList.isNotEmpty()) {
                entryList.forEach { entry ->
                    entryCard(entry.word, entry.translatedWord)
                }
            }
        }
    }
}

@Composable
fun entryCard(entry: String, translatedEntry: String) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = entry,
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = translatedEntry,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}