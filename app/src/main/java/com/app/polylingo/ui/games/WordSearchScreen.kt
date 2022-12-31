package com.app.polylingo.ui.games

import android.os.CountDownTimer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFABAndOptions
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
    MainScaffoldWithoutFABAndOptions(// TODO create a game scaffold with a dialog for back button and help button and a help text parameter
        navController = navController,
        titleText = "$numOfWords $time"
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
                time = time
            )
        }
    }
}

@Composable
fun WordSearchContent(
    modifier: Modifier,
    entryViewModel: EntryViewModel,
    numOfWords: Int,
    time: Int
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

    var entries = remember {getSortedEntries(entryList!!,numOfWords,numOfColumns,numOfRows)}

    val words = mutableListOf<String>()
    var isFound = mutableListOf<Boolean>()
    entries.forEach { entry ->
        words.add(entry.word)
        isFound.add(false)
    }


    var foundWordsIndex = remember { mutableListOf<Int>() }

    val cellBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    val colorStack = ArrayDeque<Pair<Int, Char>>()

    var width = 0
    var height = 0

    val colors = remember { mutableStateMapOf<Int, Color>() }
    val coords = remember { mutableListOf<Pair<Int, Int>>() }

    var isHorizontal = false
    var isVertical = false
    var firstBox = Pair(0, 0)


    val cells = remember { List(numOfRows) { CharArray(numOfColumns) } }


    LaunchedEffect(Unit) {
        var numAttempts = 0
        var retries = 0

        while (++retries < 100) {
            var gridWords: MutableList<String> = ArrayList()
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
    LaunchedEffect(Unit) {
        for (j in 1..numOfRows) {
            for (i in 1..numOfColumns) {
                coords.add(width * i to height * j)
            }
        }
    }

    letters.forEachIndexed { index, _ ->
        colors[index] = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
    }

    val lazyListState = rememberLazyGridState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
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
                            var selectionIndex = mutableListOf<Int>()
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
                            println(selection)

                            // reset grid
                            isHorizontal = false
                            isVertical = false
                            colorStack.clear()
                            letters.forEachIndexed { index, _ ->
                                if (!foundWordsIndex.contains(index)) {
                                    colors[index] = cellBackgroundColor
                                } else {
                                    colors[index] = Color.Blue// TODO find better colours
                                }
                            }
                        },
                        onDrag = { change: PointerInputChange, _: Offset ->
                            val touchX = change.position.x
                            val touchY = change.position.y

                            for (index in 0 until coords.size) {
                                val cellSizeX = coords[index].first
                                val cellSizeY = coords[index].second
                                val cellSizeBeforeX = cellSizeX - width
                                val cellSizeBeforeY = cellSizeY - height
                                if (touchX < cellSizeX && touchY < cellSizeY
                                    && touchX > cellSizeBeforeX && touchY > cellSizeBeforeY   // found the box the finger is on
                                ) {
                                    var peek = colorStack.peek()
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
                                width = coordinates.size.width
                                height = coordinates.size.height
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
        var openDialog by remember { mutableStateOf(false)  }

        CreateWordGrid(words = words)

        CreateTimer(time = time,
        setOpenDialog = {
            openDialog = true
        })

        if (openDialog) {
            CreateDialog()
        }
    }
}

fun getSortedEntries(entryList: List<Entry>, numOfWords: Int, numOfColumns: Int, numOfRows: Int): List<Entry> {
    var iterations = 0
    var returnList = listOf<Entry>()
    while (++iterations < 100) {
        var words =
            entryList!!.asSequence().shuffled().take(numOfWords).toList()
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
fun CreateDialog() {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = {
            Text(text = "Dialog Title")
        },
        text = {
            Text("Here is a text ")
        },
        confirmButton = {
            Button(

                onClick = {
                }) {
                Text("This is the Confirm Button")
            }
        },
    )
}

@Composable
fun CreateWordGrid(words: List<String>) {
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
                    items(words) { word ->
                        Text(
                            text = word,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 2.dp)
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
    val errorColor = MaterialTheme.colorScheme.errorContainer

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
            indicatorColor.value = errorColor
            setOpenDialog()
        }
    }
    LaunchedEffect(Unit) {
        timer.start()
    }
}