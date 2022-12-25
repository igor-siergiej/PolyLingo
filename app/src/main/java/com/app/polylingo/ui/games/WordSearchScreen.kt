package com.app.polylingo.ui.games

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.MainScaffold
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFAB
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextInt

// TODO add back button that takes the user to games screen but first show a dialog to confirm
@Composable
fun WordSearchScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    MainScaffoldWithoutFAB(
        navController = navController,
        titleText = "$numOfWords $time"
        //titleText = stringArrayResource(id = R.array.game_names_list).toList()[0]
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GamesScreenContent(
                modifier = Modifier.padding(8.dp),
                entryViewModel = entryViewModel,
                numOfWords = numOfWords,
                time = time
            )
        }
    }
}

@Composable
fun GamesScreenContent(
    modifier: Modifier,
    entryViewModel: EntryViewModel,
    numOfWords: Int,
    time: Int
) {
    val entries = remember {entryViewModel.entryList.value!!.asSequence().shuffled().take(numOfWords).toList().distinct()}
    val words = mutableListOf<String>()
    entries.forEach { entry ->
        words.add(entry.word)
    }


    val numOfCells = 99
/*    val numbers =
        List(numOfCells) { Random.nextInt(97, 122) } // ascii values for upper case alphabet*/


    // 2d char array which first should be populated with words then filled out with random letters
    // from numbers array and turned to char

    val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)

    var colorStack = ArrayDeque<Int>()

    var width = 0
    var height = 0

    var colors = remember { mutableStateMapOf<Int, Color>() }
    var coords = remember { mutableListOf<Pair<Int, Int>>() }

    var isHorizontal = false
    var isVertical = false
    var firstBox = Pair<Int, Int>(0, 0)

    val numOfColumns = 9
    val numOfRows = numOfCells / numOfColumns

    val cells = remember { List(numOfRows) { CharArray(numOfColumns) } }

    var gridWords = words

    if (gridWords.isNotEmpty()) {
        LaunchedEffect(Unit) {
            var numAttempts = 0
            //TODO if num attempts is reached, reset the grid and start again
            while (++numAttempts < 100 && gridWords.isNotEmpty()) {

                val word = gridWords.last()
                val row = Random.nextInt(0, numOfRows)
                val column = Random.nextInt(0, numOfColumns)


                var columnLengthUntilBound = numOfColumns - column
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

                var rowLengthUntilBound = numOfRows - row
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
        }
    }

    val letters = ArrayList<Char>()
    for (charArray in cells) {
        for (character in charArray)
            letters.add(character)
    }

    LaunchedEffect(Unit) {
        for (j in 1..numOfRows) {
            for (i in 1..numOfColumns) {
                coords.add(width * i to height * j)
            }
        }
    }

// map of coordinates

    letters.forEachIndexed { index, value ->
        colors[index] = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp);
    }

    val lazyListState = rememberLazyGridState()

    Column(
        modifier = Modifier
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
                            isHorizontal = false
                            isVertical = false
                            colorStack.clear()
                            letters.forEachIndexed { index, value ->
                                colors[index] = backgroundColor;
                            }
                        },
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            var touchX = change.position.x
                            var touchY = change.position.y

                            for (index in 0 until coords.size) {
                                var cellSizeX = coords[index].first
                                var cellSizeY = coords[index].second
                                var cellSizeBeforeX = cellSizeX - width
                                var cellSizeBeforeY = cellSizeY - height
                                if (touchX < cellSizeX && touchY < cellSizeY
                                    && touchX > cellSizeBeforeX && touchY > cellSizeBeforeY   // found the box the finger is on
                                ) {
                                    if (colorStack.peek() == index) {
                                        break
                                    }
                                    if (colorStack.size == 1) { // second box is pressed, this will determine if it's going to be horizontal or vertical
                                        if (firstBox.first == coords[index].first) {
                                            isVertical = true
                                        } else {
                                            isHorizontal = true
                                        }
                                    }
                                    if (colors[index] == Color.Green) { // if the box that the finger is on was already green make the previous box gray
                                        colors[colorStack.pop()] = backgroundColor
                                        break
                                    } else {
                                        if (isHorizontal) { // once the drag is horizontal ignore all of the vertical drag events
                                            if (firstBox.second != coords[index].second) {
                                                break
                                            } else {
                                                colors[index] = Color.Green
                                                colorStack.push(index)
                                            }
                                        } else if (isVertical) { // same as above just for vertical
                                            if (firstBox.first != coords[index].first) {
                                                break
                                            } else {
                                                colors[index] = Color.Green
                                                colorStack.push(index)
                                            }
                                        } else { // first box will be highlighted before the drag has direction
                                            colors[index] = Color.Green
                                            colorStack.push(index)
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
        if (words.isNotEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    columns = GridCells.Fixed(4),
                    content = {
                        items(words) { word ->
                            //TODO fix long words getting cut off
                            Text(
                                text = word,
                                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                )
            }
        }
        // SPACER

        // TODO use time variable and every second set the progress to be percentage left
        var progress by remember { mutableStateOf(1f) }
        LinearProgressIndicator(progress = progress)
    }


}