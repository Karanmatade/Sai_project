package com.example.sai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val roomNumber: Int,
    val type: String,
    val price: Int,
    val isBooked: Boolean = false
)
