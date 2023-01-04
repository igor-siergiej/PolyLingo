package com.app.polylingo.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.Entry
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
                entryViewModel = entryViewModel,
                numOfWords = numOfWords,
                time = time,
                navController = navController,
                timer = timer
            )
        }
    }
}

@Composable
private fun WordOrderScreenContent(
    modifier: Modifier,
    entryViewModel: EntryViewModel,
    numOfWords: Int,
    time: Int,
    navController: NavHostController,
    timer: Timer,
) {
    val entryList = entryViewModel.entryList.value

    val entries =
        remember { entryList!!.asSequence().shuffled().take(numOfWords).toList().distinct() }



    val word = remember { entries.first().word.toList().shuffled().joinToString(separator = "") }

    var currentlySelected by remember { mutableStateOf("_".repeat(word.length)) }

    var openOutOfTimeDialog by remember { mutableStateOf(false) }
    var openCompletedDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        CreateLetterGrid(
            word,
            currentlySelected,
            setOpenDialog = {
                openCompletedDialog = true
            },
            updateSelection = {
                var string = currentlySelected.toCharArray()
                string[it.second] = it.first
                currentlySelected = String(string)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalIconButton(onClick = {
                //reset selection
            }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(id = R.string.time_left_description)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CreateSelectionGrid(
            word,
            currentlySelected,
            setOpenDialog = {
                openCompletedDialog = true
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        CreateTimer(
            time = time,
            setOpenDialog = {
                openOutOfTimeDialog = true
            }, timer
        )

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
                Screen.MixAndMatch,
                navController,
                numOfWords, time
            )
        }
    }
}

@Composable
fun CreateLetterGrid(
    entry: String,
    currentlySelected: String,
    updateSelection: (Pair<Char,Int>) -> Unit = {},
    setOpenDialog: () -> Unit = {}
) {
    val numOfColumns = 4
    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val wordArray = entry.toCharArray()

    val colors = remember { mutableStateMapOf<Int, Color>() }

    wordArray.forEachIndexed { index, _ ->
        if (colors[index] == null) {
            colors[index] = cellBackgroundColor
        }
    }

    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        columns = GridCells.Fixed(numOfColumns)
    ) {
        items(wordArray.size) { index ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colors[index]!!
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(size = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        updateSelection(wordArray[index] to index)
                        colors[index] = Color.Green
                    },
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = wordArray[index].toString().uppercase(Locale.ROOT),
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
    word: String,
    currentlySelected: String,
    setOpenDialog: () -> Unit = {}
) {
    val numOfColumns = 4

    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val colors = remember { mutableStateMapOf<Int, Color>() }

    currentlySelected.forEachIndexed { index, _ ->
        if (colors[index] == null) {
            colors[index] = cellBackgroundColor
        }
    }
    println(currentlySelected)

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






