package com.app.polylingo.ui.games

import android.content.Context
import android.media.AudioManager
import android.os.CountDownTimer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
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
import com.app.polylingo.ui.navigation.Screen
import java.util.*
import kotlin.random.Random

@Composable
fun WordSearchScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    val timer = remember { Timer() }

    GameScaffold(
        navController = navController,
        titleText = "$numOfWords $time",
        tipText = stringResource(id = R.string.word_search_tip),
        timer = timer
        //titleText = stringArrayResource(id = R.array.game_names_list).toList()[0]
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            WordSearchContent(
                modifier = Modifier.padding(8.dp),
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
fun WordSearchContent(
    modifier: Modifier,
    entryViewModel: EntryViewModel,
    numOfWords: Int,
    time: Int,
    navController: NavHostController,
    timer: Timer,
) {
    var wordsMatchedCorrectly by remember { mutableStateOf(0) }
    var wordsMatchedIncorrectly by remember { mutableStateOf(0) }
    val entryList = entryViewModel.entryList.value

    var numOfCells = 0
    var numOfColumns = 0
    when (numOfWords) {
        3 -> {
            numOfCells = 35
            numOfColumns = 6 //5
        }
        6 -> {
            numOfCells = 49
            numOfColumns = 7 //7
        }
        9 -> {
            numOfCells = 72
            numOfColumns = 8 //9
        }
        12 -> {
            numOfCells = 99
            numOfColumns = 9 //11
        }
    }
    val numOfRows = numOfCells / numOfColumns

    val letters = remember { ArrayList<Char>() }

    val entries = remember { getSortedEntries(entryList!!, numOfWords, numOfColumns, numOfRows) }

    val words = mutableListOf<String>()
    val isFound = remember { mutableStateListOf<Boolean>() }
    if (isFound.isEmpty()) {
        for (i in 0 until numOfWords) {
            isFound.add(false)
        }
    }

    entries.forEach { entry ->
        words.add(entry.word)
    }

    var openOutOfTimeDialog by remember { mutableStateOf(false) }
    var openCompletedDialog by remember { mutableStateOf(false) }

    val cells = remember { List(numOfRows) { CharArray(numOfColumns) } }

    LaunchedEffect(Unit) {
        var numAttempts: Int
        var retries = 0

        while (++retries < 100) {
            val gridWords: MutableList<String> = ArrayList()
            gridWords.addAll(words)
            numAttempts = 0
            println("retryNumber = $retries")
            println("gridwords = $gridWords")
            while (++numAttempts < 100 && gridWords.isNotEmpty()) {

                val word = gridWords.last()
                val row = Random.nextInt(0, numOfRows)
                val column = Random.nextInt(0, numOfColumns)

                val columnLengthUntilBound = numOfColumns - column
                var columnCellsAreEmpty = true
                for (i in column until numOfColumns) {
                    if (cells[row][i] != '\u0000') {
                        columnCellsAreEmpty = false
                        break
                    }
                }
                if (columnLengthUntilBound >= word.length && columnCellsAreEmpty) { // if the word will fit
                    //put the words in the grid
                    for (i in word.indices) {
                        cells[row][column + i] = word[i]
                    }
                    gridWords.removeLast()
                }
                val rowLengthUntilBound = numOfRows - row
                var rowCellsAreEmpty = true
                for (i in row until numOfRows) {
                    if (cells[i][column] != '\u0000') {
                        rowCellsAreEmpty = false
                        break
                    }
                }
                if (rowLengthUntilBound >= word.length && rowCellsAreEmpty) { // if the word will fit
                    //put the words in the grid
                    for (i in word.indices) {
                        cells[row + i][column] = word[i]
                    }
                    gridWords.removeLast()
                }
            }
            if (gridWords.isEmpty()) {
                println("Break out")
                break
            }

            if (numAttempts == 100) {
                println("NumberAttempts Reached!")
                println(gridWords)
            }

            //Clear Grid
            for (i in 0 until numOfRows) {
                for (j in 0 until numOfColumns) {
                    if (cells[i][j] != '\u0000') {
                        cells[i][j] = '\u0000'
                    }
                }
            }
        }
        println("Retries failed")


        for (i in 0 until numOfRows) {
            for (j in 0 until numOfColumns) {
                if (cells[i][j] == '\u0000') {
                    cells[i][j] = Random.nextInt(97, 122).toChar()
                }
            }
        }
    }

    // need to create an empty grid first to get the size of the grid boxes
    // but then need to clear to enter generated grid
    letters.clear()
    for (i in 0 until numOfRows) {
        for (j in 0 until numOfColumns) {
            letters.add(cells[i][j])
        }
    }

    val audioManager = LocalContext.current.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        CreateGrid(numOfColumns, numOfRows, words, letters, isFound,
            setOpenDialog = {
                openCompletedDialog = true
            },
            increaseCorrectCounter = {
                wordsMatchedCorrectly += 1
                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, volumeLevel.toFloat())
            },
            increaseIncorrectCounter = {
                wordsMatchedIncorrectly += 1
                audioManager.playSoundEffect(
                    AudioManager.FX_KEYPRESS_INVALID,
                    volumeLevel.toFloat()
                )
            }
        )

        CreateWordGrid(words = words, isFound = isFound)

        CreateTimer(
            time = time,
            setOpenDialog = {
                openOutOfTimeDialog = true
            },
            timer = timer
        )

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
                Screen.WordSearch,
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
fun CreateGrid(
    numOfColumns: Int,
    numOfRows: Int,
    words: List<String>,
    letters: List<Char>,
    isFound: MutableList<Boolean>,
    setOpenDialog: () -> Unit = {},
    increaseCorrectCounter: () -> Unit = {},
    increaseIncorrectCounter: () -> Unit = {}
) {
    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val colors = remember { mutableStateMapOf<Int, Color>() }
    letters.forEachIndexed { index, _ ->
        if (colors[index] == null) {
            colors[index] = cellBackgroundColor
        }
    }

    val foundWordsIndex = remember { mutableListOf<Int>() }

    val width = remember { mutableListOf(0) }
    val height = remember { mutableListOf(0) }

    val coords = remember { mutableListOf<Pair<Int, Int>>() }


    // map of coordinates
    coords.clear()
    for (j in 1..numOfRows) {
        for (i in 1..numOfColumns) {
            coords.add(width[0] * i to height[0] * j)
        }
    }


    val colorStack = ArrayDeque<Pair<Int, Char>>()

    var isHorizontal = false
    var isVertical = false
    var firstBox = Pair(0, 0)


    LazyVerticalGrid(
        columns = GridCells.Fixed(numOfColumns),
        modifier = Modifier
            /*.draggable(
                state = state,
                orientation = Orientation.Vertical,
                onDragStarted = { Log.d(TAG, "Vertical Drag started") },
                onDragStopped = { Log.d(TAG, "Vertical Drag ended") }
            )
            .draggable(
            state = state,
            orientation = Orientation.Horizontal,
            onDragStarted = { Log.d(TAG, "Horizontal Drag started") },
            onDragStopped = { Log.d(TAG, "Horizontal Drag ended") }
        )*/

            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { println("DragStarted") },
                    onDragEnd = {

                        val selectionIndex = mutableListOf<Int>()
                        var selection = ""
                        while (colorStack.peek() != null) {
                            selectionIndex.add(colorStack.peek()!!.first)
                            selection += colorStack.pop().second
                        }
                        selection = selection.reversed()

                        var wordFound = false

                        words.forEachIndexed { index, word ->
                            if (selection == word) {
                                increaseCorrectCounter()
                                wordFound = true
                                isFound[index] = true
                                foundWordsIndex.addAll(selectionIndex)
                            }
                        }

                        if (!wordFound) {
                            increaseIncorrectCounter()
                        }

                        letters.forEachIndexed { index, _ ->
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

                        // reset grid
                        isHorizontal = false
                        isVertical = false
                        colorStack.clear()

                    },
                    onDrag = { change: PointerInputChange, _: Offset ->
                        val touchX = change.position.x
                        val touchY = change.position.y

                        for (index in 0 until coords.size) {
                            val cellSizeX = coords[index].first
                            val cellSizeY = coords[index].second
                            val cellSizeBeforeX = cellSizeX - width[0]
                            val cellSizeBeforeY = cellSizeY - height[0]
                            if (touchX < cellSizeX && touchY < cellSizeY
                                && touchX > cellSizeBeforeX && touchY > cellSizeBeforeY   // found the box the finger is on
                            ) {
                                val peek = colorStack.peek()
                                if (peek != null) {
                                    if (colorStack.peek()!!.first == index) {
                                        break
                                    }
                                }
                                if (colorStack.size == 1) { // second box is pressed, this will determine if it's going to be horizontal or vertical
                                    if (firstBox.first == coords[index].first) {
                                        isVertical = true
                                    } else {
                                        isHorizontal = true
                                    }
                                }
                                if (colors[index] == Color.Green) { // if the box that the finger is on was already green make the previous box gray
                                    colors[colorStack.pop().first] = cellBackgroundColor
                                    break
                                } else {
                                    if (isHorizontal) { // once the drag is horizontal ignore all of the vertical drag events
                                        if (firstBox.second != coords[index].second) {
                                            break
                                        } else {
                                            colors[index] = Color.Green
                                            colorStack.push(index to letters[index])
                                        }
                                    } else if (isVertical) { // same as above just for vertical
                                        if (firstBox.first != coords[index].first) {
                                            break
                                        } else {
                                            colors[index] = Color.Green
                                            colorStack.push(index to letters[index])
                                        }
                                    } else { // first box will be highlighted before the drag has direction
                                        colors[index] = Color.Green
                                        colorStack.push(index to letters[index])
                                        if (colorStack.size == 1) {// the first box that is pressed
                                            firstBox = coords[index]
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
                )
            },
        content = {
            items(letters.size) { index ->
                Box(
                    modifier = Modifier
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                        .onGloballyPositioned { coordinates ->
                            width[0] = coordinates.size.width
                            height[0] = coordinates.size.height
                        }
                        .background(colors[index]!!)
                        .fillMaxSize()
                ) {
                    Text(
                        text = letters[index].toString().uppercase(Locale.ROOT),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )
}

fun getSortedEntries(
    entryList: List<Entry>,
    numOfWords: Int,
    numOfColumns: Int,
    numOfRows: Int
): List<Entry> {
    var iterations = 0
    var returnList = listOf<Entry>()
    while (++iterations < 100) {
        val words =
            entryList.asSequence().shuffled().take(numOfWords).toList()
                .distinct()
        var isLonger = false
        words.forEach { word ->
            if (word.word.length > numOfColumns || word.word.length > numOfRows) {
                isLonger = true
            }
        }
        if (!isLonger) {
            returnList = words
        }
    }
    return returnList
}

@Composable
fun CreateCompletedDialog(
    timeLeft: Int,
    wordsMatchedCorrectly: Int,
    wordsMatchedIncorrectly: Int,
    navController: NavHostController,
) {
    AlertDialog(
        onDismissRequest = {
        },
        title = {
            Text(text = stringResource(R.string.congratulations))
        },
        text = {
            Text(text = stringResource(R.string.completed_game))
        },
        confirmButton = {
            Button(
                onClick = {
                    navController.navigate("${Screen.GameReview.route}/${timeLeft}/${wordsMatchedCorrectly}/${wordsMatchedIncorrectly}") {
                        // this should be navigating without being able to go back
                        popUpTo(0)
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }) {
                Text(stringResource(id = R.string.completed))
            }
        },
    )
}

@Composable
fun CreateErrorDialog(
    retryScreen: Screen,
    navController: NavHostController,
    numOfWords: Int,
    time: Int,
    timeLeft: Int,
    wordsMatchedCorrectly: Int,
    wordsMatchedIncorrectly: Int
) {
    AlertDialog(
        onDismissRequest = {
        },
        title = {
            Text(text = stringResource(R.string.out_of_time), color = Color.Red)
        },
        text = {
            Text(text = stringResource(R.string.better_luck), color = Color.Red)
        },
        confirmButton = {
            Button(
                onClick = {
                    navController.navigate("${retryScreen.route}/${numOfWords}/${time}") {
                        // this should be navigating without being able to go back
                        popUpTo(Screen.Games.route)
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }) {
                Text(stringResource(id = R.string.try_again))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    navController.navigate("${Screen.GameReview.route}/${timeLeft}/${wordsMatchedCorrectly}/${wordsMatchedIncorrectly}") {
                        // this should be navigating without being able to go back
                        popUpTo(0)
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }) {
                Text(stringResource(id = R.string.completed))
            }
        }
    )
}


@Composable
fun CreateWordGrid(words: List<String>, isFound: List<Boolean>) {
    Spacer(modifier = Modifier.height(10.dp))
    if (words.isNotEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                columns = GridCells.Fixed(3),
                content = {
                    itemsIndexed(words) { index, word ->
                        var alpha = ContentAlpha.high
                        if (isFound[index]) {
                            alpha = ContentAlpha.disabled
                        }
                        Text(
                            text = word,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .alpha(alpha),
                        )
                    }
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun CreateTimer(
    time: Int,
    setOpenDialog: () -> Unit = {},
    timer: Timer
) {
    val color = ProgressIndicatorDefaults.linearColor

    val indicatorColor = remember { mutableStateOf(color) }

    var progress by remember { mutableStateOf(1f) }

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(5.dp),
                imageVector = Icons.Filled.Timer,
                contentDescription = stringResource(id = R.string.time_left_description)
            )
            Text(text = stringResource(id = R.string.time_left))
            Text(text = (" " + timer.timeInMilliSeconds / 1000 + " Seconds"))
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = animatedProgress,
            color = indicatorColor.value
        )
    }

    LaunchedEffect(Unit) {
        timer.createTimer(
            time,
            onTick = { timeLeft ->
                progress = timeLeft / 1000f / time
            },
            onFinish = {
                progress = 1f
                indicatorColor.value = Color.Red
                setOpenDialog()
            })
        timer.startTimer()
    }
}

//TODO create file for this
class Timer {
    private lateinit var timer: CountDownTimer
    var timeInMilliSeconds = 0L
    var tick = { _: Long -> }
    var finish = {}

    fun createTimer(
        timeInSeconds: Int,
        onTick: (timeLeft: Long) -> Unit = {},
        onFinish: () -> Unit = {}
    ) {
        timeInMilliSeconds = (timeInSeconds * 1000).toLong()
        tick = onTick
        finish = onFinish

        timer = object : CountDownTimer((timeInMilliSeconds), 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMilliSeconds = millisUntilFinished
                tick(timeInMilliSeconds)
            }

            override fun onFinish() {
                finish()
            }
        }
    }

    fun pauseTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
    }

    fun startTimer() {
        if (this::timer.isInitialized) {
            timer.start()
        }
    }

    fun resumeTimer() {
        timer = object : CountDownTimer((timeInMilliSeconds), 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMilliSeconds = millisUntilFinished
                tick(timeInMilliSeconds)
            }

            override fun onFinish() {
                finish()
            }
        }
        timer.start()
    }

    fun timeLeft(): Int {
        return (timeInMilliSeconds / 1000).toInt()
    }
}