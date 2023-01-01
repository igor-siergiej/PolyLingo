package com.app.polylingo.ui.games

import android.os.CountDownTimer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
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
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFABAndOptions
import com.app.polylingo.ui.navigation.Screen
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Composable
fun WordSearchScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    GameScaffold(
        navController = navController,
        titleText = "$numOfWords $time",
        tipText = stringResource(id = R.string.word_search_tip)
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
                navController = navController
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
    navController: NavHostController
) {
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
    var isFound = remember { mutableStateListOf<Boolean>() }
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
        var numAttempts = 0
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
                    //cells[i][j] = Random.nextInt(97, 122).toChar()
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

    // map of coordinates



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        CreateGrid(numOfColumns, numOfRows, words, letters, isFound,
            setOpenDialog = {
                openCompletedDialog = true
            }
        )

        CreateWordGrid(words = words, isFound = isFound)

        CreateTimer(time = time,
            setOpenDialog = {
                openOutOfTimeDialog = true
            })

        if (openCompletedDialog) {
            CreateDialog(
                stringResource(R.string.congratulations),
                stringResource(R.string.completed_game),
                Screen.Home,
                navController,
                false
            )
        }

        if (openOutOfTimeDialog) {
            CreateDialog(
                stringResource(R.string.out_of_time),
                stringResource(R.string.better_luck),
                Screen.Games,
                navController,
                true
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
    setOpenDialog: () -> Unit = {}
) {
    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val colors = remember { mutableStateMapOf<Int, Color>() }
    letters.forEachIndexed { index, _ ->
        if (colors[index] == null) {
            colors[index] = cellBackgroundColor
        }
    }

    val foundWordsIndex = remember { mutableListOf<Int>() }

    var width = remember { mutableListOf(0) }
    var height = remember { mutableListOf(0) }

    val coords = remember { mutableListOf<Pair<Int, Int>>() }


    coords.clear()
    for (j in 1..numOfRows) {
        for (i in 1..numOfColumns) {
            coords.add(width[0] * i to height[0] * j)
        }
    }


    val colorStack = ArrayDeque<Pair<Int, Char>>()

    val lazyListState = rememberLazyGridState()
    var isHorizontal = false
    var isVertical = false
    var firstBox = Pair(0, 0)


    LazyVerticalGrid(
        columns = GridCells.Fixed(numOfColumns),
        state = lazyListState,
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
                            selectionIndex.add(colorStack.peek().first)
                            selection += colorStack.pop().second
                        }
                        selection = selection.reversed()

                        words.forEachIndexed { index, word ->
                            if (selection == word) {
                                isFound[index] = true
                                foundWordsIndex.addAll(selectionIndex)
                            }
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
                                    if (colorStack.peek().first == index) {
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

//TODO create a "Try again" button here so that user can restart game instead of having to get to the game again
//TODO create two methods instead
@Composable
fun CreateDialog(
    title: String,
    text: String,
    screen: Screen,
    navController: NavHostController,
    isErrorDialog: Boolean
) {
    AlertDialog(
        onDismissRequest = {
        },
        title = {
            if (isErrorDialog) {
                Text(text = title, color = Color.Red)
            } else {
                Text(text = title)
            }

        },
        text = {
            if (isErrorDialog) {
                Text(text = text, color = Color.Red)
            } else {
                Text(text = text)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    navController.navigate(screen.route) {
                        // this should be navigating without being able to go back
                        popUpTo(Screen.Home.route)
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
) {
    val color = ProgressIndicatorDefaults.linearColor
    val indicatorColor = remember { mutableStateOf(color) }

    var progress by remember { mutableStateOf(1f) }

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        progress = animatedProgress,
        color = indicatorColor.value
    )

    val timer = object : CountDownTimer((time * 1000).toLong(), 100) {
        override fun onTick(millisUntilFinished: Long) {
            progress = millisUntilFinished / 1000f / time
        }

        override fun onFinish() {
            progress = 1f
            indicatorColor.value = Color.Red
            setOpenDialog()
        }
    }
    LaunchedEffect(Unit) {
        timer.start()
    }
}