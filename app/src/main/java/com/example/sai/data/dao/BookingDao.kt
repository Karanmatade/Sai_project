package com.example.sai.data.dao

import androidx.room.*
import com.example.sai.data.entity.BookingEntity
import com.example.sai.data.entity.GuestEntity
import com.example.sai.data.entity.BookingWithGuest
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert
    suspend fun insert(booking: BookingEntity): Long

    @Update
    suspend fun update(booking: BookingEntity)

    @Delete
    suspend fun delete(booking: BookingEntity)

    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookingsFlow(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: Int): BookingEntity?

    @Insert
    suspend fun insertGuest(guest: GuestEntity): Long

    @Transaction
    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingWithGuest(bookingId: Int): BookingWithGuest?

    @Transaction
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookingsWithGuests(): Flow<List<BookingWithGuest>>
}
