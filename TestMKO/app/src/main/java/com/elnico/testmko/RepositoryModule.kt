package com.elnico.testmko

import org.koin.dsl.module

fun getRepositoriesModule() = module {
    single<MainViewRepository> { MainViewRepository(get()) }
}