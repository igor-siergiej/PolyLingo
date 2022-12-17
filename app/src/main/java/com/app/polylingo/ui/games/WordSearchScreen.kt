package com.app.polylingo.ui.games

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.app.polylingo.ui.components.scaffolds.MainScaffold
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

//TODO remove the add entry FAB and remove cog
// add back button that takes the user to games screen but first show a dialog to confirm
@Composable
fun WordSearchScreen(navController: NavHostController) {

    MainScaffold(
        navController = navController,
        titleText = stringArrayResource(id = R.array.game_names_list).toList()[0]
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GamesScreenContent(
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun GamesScreenContent(
    modifier: Modifier = Modifier,
) {
    createGrid()
}

@Composable
fun createGrid() {
    val numOfCells = 36
    val numbers =
        List(numOfCells) { Random.nextInt(97, 122) } // ascii values for upper case alphabet
    val letters = ArrayList<Char>()
    for (number in numbers) {
        letters.add(number.toChar())
    }

    var colorStack = ArrayDeque<Int>()

    var width = 0
    var height = 0

    var colors = remember { mutableStateMapOf<Int, Color>() }
    var coords = remember { mutableListOf<Pair<Int, Int>>() }

    var isHorizontal = false
    var isVertical = false
    var firstBox = Pair<Int, Int>(0, 0)

    val numOfColumns = 6
    val numOfRows = numOfCells / numOfColumns

    LaunchedEffect(Unit) {
        for (j in 1..numOfRows) {
            for (i in 1..numOfColumns) {
                coords.add(width * i to height * j)
            }
        }
    }

    val state = rememberDraggableState(
        onDelta = { delta -> Log.d(ContentValues.TAG, "Dragged $delta") }
    )

    println(coords)
    println(coords.size)

    // map of coordinates

    numbers.forEachIndexed { index, value ->
        colors[index] = Color.Gray;
    }

    val lazyListState = rememberLazyGridState()

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
                        numbers.forEachIndexed { index, value ->
                            colors[index] = Color.Gray;
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
                                    colors[colorStack.pop()] = Color.Gray
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
            }
        ,
        content = {
            items(letters.size) { index ->
                Box(
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Black))
                        .onGloballyPositioned { coordinates ->
                            width = coordinates.size.width
                            height = coordinates.size.height
                        }
                        .background(colors[index]!!)
                ) {
                    Text(
                        text = letters[index].toString().uppercase(Locale.ROOT),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    )
}