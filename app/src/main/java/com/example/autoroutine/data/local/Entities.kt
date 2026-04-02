package com.example.autoroutine.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val timeOfDay: String, // e.g., "Night", "Morning"
    val isHeadsetConnected: Boolean,
    val batteryLevel: Int,
    val foregroundApp: String?,
    val locationRegion: String? // e.g., "Home", "Work"
)

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ruleName: String,
    val condition: String,  // e.g., "time=Night, app=YouTube"
    val action: String,     // e.g., "brightness=10%, dnd=on"
    val isUserAccepted: Boolean = false,
    val isActive: Boolean = false
)
