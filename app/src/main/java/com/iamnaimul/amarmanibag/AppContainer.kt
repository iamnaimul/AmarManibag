package com.iamnaimul.amarmanibag

import android.content.Context
import androidx.room.Room
import com.iamnaimul.amarmanibag.data.AppDatabase
import com.iamnaimul.amarmanibag.data.AppRepository

class AppContainer(context: Context) {
    val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "amar_manibag.db"
    ).build()

    val repository = AppRepository(database)
}
