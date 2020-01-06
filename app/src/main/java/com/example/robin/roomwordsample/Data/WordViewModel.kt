package com.example.robin.roomwordsample.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.robin.roomwordsample.localdb.LocalDB
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class WordViewModel(application: Application) : AndroidViewModel(Application()) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: WordRepository
    val allWords: LiveData<List<Word>>

    val prefs = LocalDB(application)

    init {
        val wordsDao = WordRoomDatabase.getDatabase(application, scope).wordDao()
        repository = WordRepository(wordsDao)
        allWords = repository.allWords
    }

    fun insert(word: Word) = scope.launch(Dispatchers.IO) {
        repository.insert(word)
    }

    fun delete(word: Word) = scope.launch(Dispatchers.IO) {
        repository.delete(word)
    }

    fun markAsComplete(id: Int, mark: Boolean) = scope.launch(Dispatchers.IO) {
        repository.toggleCompletion(id, mark)
    }

    fun getTask(): String {
        return prefs.getTask()
    }

    fun addTask(value: String) {
        prefs.addTask(value)
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun update(id: Int, word: String, description: String) = scope.launch(Dispatchers.IO) {
        repository.update(id, word, description)
    }

}