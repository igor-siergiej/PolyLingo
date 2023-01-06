package com.app.polylingo.ui.games

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.GameScaffold
import com.app.polylingo.ui.navigation.Screen

@Composable
fun MixAndMatchScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    val timer = remember{ Timer()}
    GameScaffold(
        navController = navController,
        titleText = "$numOfWords $time",
        tipText = stringResource(id = R.string.mix_and_match_tip),
        timer = timer
        //TODO Remember undo this
        //titleText = stringArrayResource(id = R.array.game_names_list).toList()[0]
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MixAndMatchScreenContent(
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
private fun MixAndMatchScreenContent(
    modifier: Modifier,
    entryViewModel: EntryViewModel,
    numOfWords: Int,
    time: Int,
    navController: NavHostController,
    timer: Timer,
) {
    var wordsMatchedCorrectly by remember{ mutableStateOf(0) }
    var wordsMatchedIncorrectly by remember{mutableStateOf(0)}
    var numOfColumns = 0
    when (numOfWords) {
        3 -> {
            numOfColumns = 3
        }
        6,9, 12 -> {
            numOfColumns = 4
        }
    }

    val entryList = entryViewModel.entryList.value

    val entries =
        remember { entryList!!.asSequence().shuffled().take(numOfWords).toList().distinct() }

    val isFound = remember { mutableListOf<Boolean>() }
    if (isFound.isEmpty()) {
        for (i in 0 until numOfWords * 2) {
            isFound.add(false)
        }
    }

    val words = remember { mutableListOf<String>() }

    if (words.isEmpty()) {
        entries.forEach { entry ->
            words.add(entry.word)
            words.add(entry.translatedWord)
        }
        val temp = words.shuffled()
        words.clear()
        words.addAll(temp)
    }

    val audioManager = LocalContext.current.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)

    var openOutOfTimeDialog by remember { mutableStateOf(false) }
    var openCompletedDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {

        CreateWordGrid(numOfColumns, entries, isFound, words,
            setOpenDialog = {
                openCompletedDialog = true
            },
            increaseCorrectCounter = {
                wordsMatchedCorrectly += 1
                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, volumeLevel.toFloat())
            },
            increaseIncorrectCounter = {
                wordsMatchedIncorrectly += 1
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_INVALID, volumeLevel.toFloat())
            }
        )

      CreateTimer(time = time,
            setOpenDialog = {
                openOutOfTimeDialog = true
            },timer)

        if (openCompletedDialog) {
            timer.pauseTimer()
            CreateCompletedDialog(
                timer.timeLeft(),
                wordsMatchedCorrectly,
                wordsMatchedIncorrectly,
                navController,
            )
        }

        if (openOutOfTimeDialog) {
            CreateErrorDialog(
                Screen.MixAndMatch,
                navController,
                numOfWords,
                time,
                timer.timeLeft(),
                wordsMatchedCorrectly,
                wordsMatchedIncorrectly
            )
        }
    }
}

@Composable
fun CreateWordGrid(
    numOfColumns: Int,
    entries: List<Entry>,
    isFound: MutableList<Boolean>,
    words: MutableList<String>,
    setOpenDialog: () -> Unit = {},
    increaseCorrectCounter: () -> Unit = {},
    increaseIncorrectCounter: () -> Unit = {}
) {
    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val colors = remember { mutableStateMapOf<Int, Color>() }

    words.forEachIndexed { index, _ ->
        if (colors[index] == null) {
            colors[index] = cellBackgroundColor
        }
    }

    val foundWordsIndex = remember { mutableListOf<Int>() }

    var currentlySelected by remember { mutableStateOf("") }

    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        columns = GridCells.Fixed(numOfColumns),
        content = {
            items(words.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colors[index]!!
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(size = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (currentlySelected.isEmpty()) {
                                if (colors[words.indexOf(currentlySelected)] == Color.Blue) {
                                    return@clickable
                                }
                                currentlySelected = words[index]
                                colors[index] = Color.Green
                                return@clickable
                            } else {
                                if (doSelectedWordsMatch(
                                        currentlySelected,
                                        words[index],
                                        entries
                                    )
                                ) {
                                    increaseCorrectCounter()
                                    foundWordsIndex.add(index)
                                    foundWordsIndex.add(words.indexOf(currentlySelected))
                                    isFound[index] = true
                                    isFound[words.indexOf(currentlySelected)] = true
                                } else {
                                    increaseIncorrectCounter()
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
                        },
                    content = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AutoSizeText(
                                text = words[index],
                                textStyle = TextStyle(fontSize = 16.sp),
                                modifier = Modifier
                                    .padding(vertical = 25.dp)
                            )
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

fun doSelectedWordsMatch(selectedWord: String, otherWord: String, entries: List<Entry>): Boolean {

    var doWordsMatch = false

    entries.forEach { entry ->
        if (selectedWord == entry.word && otherWord == entry.translatedWord ||
            selectedWord == entry.translatedWord && otherWord == entry.word
        ) {
            doWordsMatch = true
        }
    }
    return doWordsMatch
}