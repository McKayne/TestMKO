package com.elnico.testmko

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun getViewModelModule() = module {
    viewModel<MainViewModel> { MainViewModel(get()) }
}