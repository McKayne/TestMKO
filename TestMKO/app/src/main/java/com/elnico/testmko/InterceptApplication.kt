package com.elnico.testmko

import android.app.Application
import com.elnico.testmko.StringValueHolder.exceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class InterceptApplication: Application(), KoinComponent {

    private val repository: MainViewRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    getViewModelModule(),
                    getRepositoriesModule()
                )
            )
        }
    }

    fun appendNewName(name: String) {
        CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                repository.appendNewName(name)
            }
        }
    }
}