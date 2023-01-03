package com.app.polylingo.datasource.fileStorage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

const val FILENAME = "languages.txt"


class LanguageViewModel(application: Application) : AndroidViewModel(application) {

    private var textFile = File(application.filesDir, FILENAME)
    var languages: Pair<String,String> = readLanguages()

    fun doesFileExist(): Boolean {
        return textFile.exists()
    }

    fun readLanguages(): Pair<String, String> {
        return if (doesFileExist()) {
            var readString = textFile.readText()
            var list = ArrayList<String>()
            list.addAll(readString.split(","))
            Pair(list[0],list[1])
        } else {
            Pair("","")
        }
    }

    fun saveLanguages(currentLanguage: String, learningLanguage: String) {
        CoroutineScope(Dispatchers.IO).launch {
            textFile.writeText("$currentLanguage,$learningLanguage")
        }
    }
    //TODO create delete file function
}