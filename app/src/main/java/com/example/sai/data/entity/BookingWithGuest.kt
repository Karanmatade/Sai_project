package com.example.sai.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BookingWithGuest(
    @Embedded val booking: BookingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookingId"
    )
    val guest: GuestEntity?
)
