package com.elnico.testmko

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import java.io.File

class MainViewRepository(application: Application) {

    private val database: SQLiteDatabase

    init {
        val databaseFile = File(application.filesDir.absolutePath + File.separator + "Instagram.sqlite")
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null)

        createUsersTable()
    }

    private fun createUsersTable() {
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS USERS(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun appendNewName(name: String) = database.execSQL("INSERT INTO USERS(NAME) VALUES(?)", arrayOf(name))

    suspend fun fetchPreviousName(): String? {
        database.rawQuery("SELECT NAME FROM USERS ORDER BY ID DESC LIMIT 1", emptyArray()).use {
            if (it.moveToNext()) {
                return it.getString(0)
            }
        }

        return null
    }
}