package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val response: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "en",
    val isSpoken: Boolean = false
)
