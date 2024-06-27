package com.elnico.testmko

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elnico.testmko.StringValueHolder.exceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel(application: Application): AndroidViewModel(application), KoinComponent {

    val nameFlow = MutableStateFlow<String?>(null)

    private val repository: MainViewRepository by inject()

    fun fetchPreviousName() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                nameFlow.update { repository.fetchPreviousName() }
            }
        }
    }
}