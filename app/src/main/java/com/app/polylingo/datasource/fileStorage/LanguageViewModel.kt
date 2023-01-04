package com.app.polylingo.datasource.fileStorage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

const val FILENAME = "languages.txt"

// TODO does this have to extendViewModel class?
class LanguageViewModel(application: Application) : AndroidViewModel(application) {

    private var textFile = File(application.filesDir, FILENAME)

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

    fun deleteLanguages() {
        textFile.delete()
    }
}