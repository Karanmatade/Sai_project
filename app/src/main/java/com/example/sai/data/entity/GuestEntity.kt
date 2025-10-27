package com.example.sai.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "guests",
    foreignKeys = [ForeignKey(
        entity = BookingEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("bookingId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class GuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    // Guest Information
    val folioNumber: String,
    val lastName: String,
    val firstName: String,
    val address: String,
    val company: String,
    // Identification Information
    val idType: String, // Aadhar Card, Passport, Driving License, Voter ID
    val idNumber: String,
    // Vehicle Information
    val vehicle: String,
    val vehicleModel: String,
    val plateNumber: String
)
