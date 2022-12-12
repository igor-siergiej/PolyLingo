package com.app.polylingo.ui.dictionary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.datasource.fileStorage.LanguageViewModel
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.MainScaffold

@Composable
fun DictionaryScreenTopLevel(
    navController: NavHostController,
    entryViewModel: EntryViewModel,
    languageViewModel: LanguageViewModel
) {
    DictionaryScreen(
        navController = navController,
        entryViewModel = entryViewModel,
        languages = languageViewModel.readLanguages()
    )
}

@Composable
fun DictionaryScreen(
    navController: NavHostController,
    entryViewModel: EntryViewModel,
    languages: Pair<String, String>
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
                entryViewModel = entryViewModel,
                languages = languages
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun DictionaryScreenContent(
    entryViewModel: EntryViewModel,
    languages: Pair<String, String>
) {
    //TODO uncomment for dictionary to work from db
    //val entries by entryViewModel.entryList.observeAsState(mutableListOf())
    val entries = remember {
        mutableStateListOf(
            Entry("word1", "word2"),
            Entry("Word2", "Word3"),
            Entry("word4", "word6"),
            Entry("Word5", "Word37"),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 100.dp),
        contentPadding = PaddingValues(5.dp),
    ) {
        stickyHeader {
            Card(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    Text(
                        languages.first,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 20.dp)
                    )

                    Divider(
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Text(
                        languages.second,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 20.dp)
                    )
                }
            }
        }
        itemsIndexed(
            items = entries, key = { _, entry -> entry.hashCode() })
        { _, entry ->
            if (entries.isNotEmpty()) {
                val state = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToStart) {
                            //TODO uncomment for dictionary to work from db
                            /*CoroutineScope(Dispatchers.IO).launch {
                                entryViewModel.removeEntry(entry)
                            }*/
                            entries.remove(entry)
                        }
                        true
                    }
                )
                SwipeToDismiss(
                    state = state, background = {
                        val color = when (state.dismissDirection) {
                            DismissDirection.StartToEnd -> Color.Transparent
                            DismissDirection.EndToStart -> Color.Red
                            null -> Color.Magenta
                        }
                        Box(
                            modifier = Modifier
                                .background(color = color)
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Black,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    },
                    dismissThresholds = {
                        androidx.compose.material.FractionalThreshold(0.6f)
                    },
                    dismissContent = {
                        entryCard(entry)
                    },
                    directions = setOf(DismissDirection.EndToStart)
                )
                Divider()
            }
        }
    }
}

@Composable
fun entryCard(entry: Entry) {
    OutlinedCard(
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = entry.word,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 20.dp)
            )

            Divider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )

            Text(
                text = entry.translatedWord,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 20.dp)
            )
        }
    }
}