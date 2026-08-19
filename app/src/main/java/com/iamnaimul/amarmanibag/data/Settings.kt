package com.iamnaimul.amarmanibag.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "system",
    val defaultAccountId: Long? = null,
    val backupTreeUri: String? = null
)
