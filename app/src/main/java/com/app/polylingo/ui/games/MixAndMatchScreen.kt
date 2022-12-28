package com.app.polylingo.ui.games

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryViewModel
import com.app.polylingo.ui.components.scaffolds.MainScaffoldWithoutFAB

// add back button that takes the user to games screen but first show a dialog to confirm
@Composable
fun MixAndMatchScreen(
    entryViewModel: EntryViewModel,
    navController: NavHostController,
    numOfWords: Int,
    time: Int
) {
    var words: List<Entry> = listOf()

    val test by entryViewModel.entryList.observeAsState(listOf())

    var entries: MutableList<Entry>

    if (test.isNotEmpty()) {
        words = test.asSequence().shuffled().take(numOfWords).toList().distinct()
    }

    /*
    val randomInts = generateSequence { Random.nextInt(1..69) }
            .distinct()
            .take(6)
    */

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
            MixAndMatchScreenContent(
                modifier = Modifier.padding(8.dp),
                words = words
            )
        }
    }
}

@Composable
private fun MixAndMatchScreenContent(
    modifier: Modifier = Modifier,
    words: List<Entry>
) {

}