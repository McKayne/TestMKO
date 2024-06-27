package com.elnico.testmko

import kotlinx.coroutines.CoroutineExceptionHandler

object StringValueHolder {

    var loggedInUserName: String? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    }
}