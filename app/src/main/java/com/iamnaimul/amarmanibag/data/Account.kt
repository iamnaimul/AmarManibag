package com.iamnaimul.amarmanibag.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val openingBalance: Double,
    val iconColor: String = "#008577",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
