package com.app.polylingo.ui.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.R
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.GameScaffold
import com.app.polylingo.ui.navigation.Screen

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

    val isFound = remember { mutableListOf<Boolean>() }
    if (isFound.isEmpty()) {
        for (i in 0 until numOfWords * 2) {
            isFound.add(false)
        }
    }

    val words = remember { mutableListOf<String>() }

    if (words.isEmpty()) {
        entries.forEach { entry ->
            words.add(entry.translatedWord)
        }
        val temp = words.shuffled()
        words.clear()
        words.addAll(temp)
    }

    var openOutOfTimeDialog by remember { mutableStateOf(false) }
    var openCompletedDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {

        //TODO create word order grid?

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




