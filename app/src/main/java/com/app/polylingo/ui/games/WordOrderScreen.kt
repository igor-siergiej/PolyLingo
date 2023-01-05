package com.app.polylingo.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.GameScaffold
import com.app.polylingo.ui.navigation.Screen
import java.util.*

@Composable
fun WordOrderScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    val timer = remember { Timer() }
    var openCompletedDialog by remember { mutableStateOf(false) }
    if (openCompletedDialog) {
        timer.pauseTimer()
        CreateCompletedDialog(
            stringResource(R.string.congratulations),
            stringResource(R.string.completed_game),
            Screen.Home,
            navController,
        )
    }

    var entryList by remember {
        mutableStateOf(
            entryViewModel.entryList.value!!.asSequence().shuffled().take(numOfWords).distinct()
                .toList()
        )
    }
    //TODO recomp happens here dunno why

    val word = entryList.first().word.lowercase(Locale.ROOT)

    val scrambledWord = word.toList().shuffled().joinToString(separator = "")

    GameScaffold(
        navController = navController,
        titleText = "$numOfWords $time",
        tipText = stringResource(id = R.string.word_order_tip),
        timer = timer
        //TODO Remember undo this
        //titleText = stringArrayResource(id = R.array.game_names_list).toList()[0]
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            WordOrderScreenContent(
                modifier = Modifier.padding(5.dp),
                word = word,
                scrambledWord = scrambledWord,
                numOfWords = numOfWords,
                time = time,
                navController = navController,
                timer = timer,
                removeEntry = {
                    if (entryList.size == 1) {
                        openCompletedDialog = true
                    } else {
                        entryList = entryList.drop(1)
                    }
                }
            )
        }
    }
}

@Composable
private fun WordOrderScreenContent(
    modifier: Modifier,
    word: String,
    scrambledWord: String,
    numOfWords: Int,
    time: Int,
    navController: NavHostController,
    timer: Timer,
    removeEntry: () -> Unit = {},
) {
    var currentlySelected by remember { mutableStateOf("_".repeat(word.length)) }

    var openOutOfTimeDialog by remember { mutableStateOf(false) }

    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val selectionColors = remember { mutableStateMapOf<Int, Color>() }
    val clickableColors = remember { mutableStateMapOf<Int, Color>() }

    println(Integer.toHexString(cellBackgroundColor.toArgb()))
    //TODO get colors in colors.xml or something

    if (!currentlySelected.contains('_')) {
        if (currentlySelected == word) {
            word.forEachIndexed { index, _ ->
                selectionColors[index] = cellBackgroundColor
                clickableColors[index] = cellBackgroundColor
            }
            currentlySelected = ""
            removeEntry()
        } else {
            currentlySelected.forEachIndexed { index, _ ->
                selectionColors[index] = Color.Red
            }
            currentlySelected = "_".repeat(word.length)
            word.forEachIndexed { index, _ ->
                clickableColors[index] = cellBackgroundColor
            }
        }
    }

     word.forEachIndexed { index, _ ->
        if (selectionColors[index] == null) {
            selectionColors[index] = cellBackgroundColor
        }
        if (clickableColors[index] == null) {
            clickableColors[index] = cellBackgroundColor
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        CreateLetterGrid(
            scrambledWord,
            clickableColors
        ) { char ->
            currentlySelected = currentlySelected.replaceFirst('_', char)
            currentlySelected.forEachIndexed { index, _ ->
                selectionColors[index] = cellBackgroundColor
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalIconButton(onClick = {
                currentlySelected = "_".repeat(word.length)
                word.forEachIndexed { index, _ ->
                    clickableColors[index] = cellBackgroundColor
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(id = R.string.time_left_description)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CreateSelectionGrid(
            currentlySelected,
            selectionColors,
        )

        Spacer(modifier = Modifier.height(10.dp))

        CreateTimer(
            time = time,
            setOpenDialog = {
                openOutOfTimeDialog = true
            }, timer
        )

        if (openOutOfTimeDialog) {
            CreateErrorDialog(
                stringResource(R.string.out_of_time),
                stringResource(R.string.better_luck),
                Screen.WordOrder,
                navController,
                numOfWords, time
            )
        }
    }
}

@Composable
fun CreateLetterGrid(
    word: String,
    colors: MutableMap<Int, Color>,
    updateSelection: (Char) -> Unit = {},
) {
    val numOfColumns = 4

    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        columns = GridCells.Fixed(numOfColumns)
    ) {
        items(word.length) { index ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colors[index]!!
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { //TODO add sound effects
                        updateSelection(word[index])
                        colors[index] = Color.Green
                    },
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = word[index].toString().uppercase(Locale.ROOT),
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(vertical = 15.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CreateSelectionGrid(
    currentlySelected: String,
    colors: MutableMap<Int, Color>,
) {
    val numOfColumns = 4

    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        columns = GridCells.Fixed(numOfColumns)
    ) {
        items(currentlySelected.length) { index ->
            Box(
                modifier = Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                    .background(colors[index]!!)
                    .fillMaxSize()
            ) {
                Text(
                    text = currentlySelected[index].uppercase(Locale.ROOT),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 10.dp)
                )
            }
        }
    }
}