package com.example.sai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomNumber: Int,
    val checkInDate: String,
    val checkOutDate: String,
    val status: String, // Active or Cancelled
    val totalPrice: Int = 0,
    val paymentMethod: String = "Cash" // Cash or Online
)
