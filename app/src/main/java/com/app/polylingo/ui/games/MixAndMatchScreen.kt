package com.app.polylingo.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.GameScaffold
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFAB
import com.app.polylingo.ui.navigation.Screen
import java.util.*

@Composable
fun MixAndMatchScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    GameScaffold(
        navController = navController,
        titleText = "$numOfWords $time",
        tipText = stringResource(id = R.string.mix_and_match_tip)
        //titleText = stringArrayResource(id = R.array.game_names_list).toList()[0]
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MixAndMatchScreenContent(
                modifier = Modifier.padding(8.dp),
                entryViewModel = entryViewModel,
                numOfWords = numOfWords,
                time = time,
                navController = navController
            )
        }
    }
}

@Composable
private fun MixAndMatchScreenContent(
    modifier: Modifier,
    entryViewModel: EntryViewModel,
    numOfWords: Int,
    time: Int,
    navController: NavHostController
) {
    var numOfColumns = 0
    when (numOfWords) {
        3 -> {
            numOfColumns = 3
        }
        6,9,12 -> {
            numOfColumns = 4
        }
    }

    val entryList = entryViewModel.entryList.value

    val numOfRows = numOfWords * 2 / numOfColumns

    var entries = remember { entryList!!.asSequence().shuffled().take(numOfWords).toList().distinct() }

    var isFound = remember { mutableListOf<Boolean>() }
    if (isFound.isEmpty()) {
        for (i in 0 until numOfWords*2) {
            isFound.add(false)
        }
    }

    var words = mutableListOf<Entry>()

    entries.forEach { entry ->
        words.add(entry)
    }

    var openOutOfTimeDialog by remember { mutableStateOf(false) }
    var openCompletedDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        CreateWordGrid(numOfColumns, numOfRows, words, isFound,
            setOpenDialog = {
                openCompletedDialog = true
            }
        )

        CreateTimer(time = time,
            setOpenDialog = {
                openOutOfTimeDialog = true
            })

        if (openCompletedDialog) {
            CreateCompletedDialog(
                stringResource(R.string.congratulations),
                stringResource(R.string.completed_game),
                Screen.Home,
                navController,
            )
        }

        if (openOutOfTimeDialog) {
            CreateErrorDialog(
                stringResource(R.string.out_of_time),
                stringResource(R.string.better_luck),
                Screen.WordSearch,
                navController,
                numOfWords,time
            )
        }
    }
}

@Composable
fun CreateWordGrid(
    numOfColumns: Int,
    numOfRows: Int,
    entries: List<Entry>,
    isFound: MutableList<Boolean>,
    setOpenDialog: () -> Unit = {}
) {
    var words = remember{ mutableListOf<String>()}

    if (words.isEmpty()) {
        entries.forEach { entry ->
            words.add(entry.word)
            words.add(entry.translatedWord)
        }
        words = words.shuffled() as MutableList<String>
    }

    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val colors = remember { mutableStateMapOf<Int, Color>() }

    words.forEachIndexed { index, _ ->
        if (colors[index] == null) {
            colors[index] = cellBackgroundColor
        }
    }

    val foundWordsIndex = remember { mutableListOf<Int>() }

    var currentlySelected by remember{ mutableStateOf("") }

    val lazyListState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(numOfColumns),
        state = lazyListState,
        content = {
            items(words.size) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                        .background(colors[index]!!)
                        .fillMaxSize()
                        .clickable {
                            // if something is selected, check that it matches against entryList
                            // if it does match mark the pair as is found and clear selected
                            if (currentlySelected.isEmpty()) {
                                // if blue then ignore
                                currentlySelected = words[index]
                                colors[index] = Color.Green
                                return@clickable
                            } else {
                                if (doSelectedWordsMatch(currentlySelected,words[index],entries)) {
                                    foundWordsIndex.add(index)
                                    foundWordsIndex.add(words.indexOf(currentlySelected))
                                    isFound[index] = true
                                    isFound[words.indexOf(currentlySelected)] = true
                                }
                                currentlySelected = ""
                            }

                            words.forEachIndexed { index, _ ->
                                if (!foundWordsIndex.contains(index)) {
                                    colors[index] = cellBackgroundColor
                                } else {
                                    colors[index] = Color.Blue// TODO find better colours
                                }
                            }

                            var areAllFound = true
                            isFound.forEach { isFound ->
                                if (!isFound) {
                                    areAllFound = false
                                }
                            }

                            if (areAllFound) {
                                setOpenDialog()
                            }
                        }
                ) {
                    Text(
                        text = words[index],
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )
}

fun doSelectedWordsMatch(selectedWord: String, otherWord: String, entries: List<Entry>): Boolean {
    var doWordsMatch = false

    entries.forEach{ entry ->
        if (selectedWord == entry.word && otherWord == entry.translatedWord||
                selectedWord == entry.translatedWord && otherWord == entry.word) {
            doWordsMatch = true
        }
    }

    return doWordsMatch
}