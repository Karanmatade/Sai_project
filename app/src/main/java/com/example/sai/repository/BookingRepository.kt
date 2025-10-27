package com.example.sai.repository

import com.example.sai.data.dao.BookingDao
import com.example.sai.data.entity.BookingEntity
import com.example.sai.data.entity.GuestEntity
import com.example.sai.data.entity.BookingWithGuest
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val bookingDao: BookingDao) {
    fun getAllBookings(): Flow<List<BookingEntity>> = bookingDao.getAllBookingsFlow()
    fun getAllBookingsWithGuests(): Flow<List<BookingWithGuest>> = bookingDao.getAllBookingsWithGuests()
    
    suspend fun insert(booking: BookingEntity): Long = bookingDao.insert(booking)
    suspend fun insertGuest(guest: GuestEntity): Long = bookingDao.insertGuest(guest)
    suspend fun update(booking: BookingEntity) = bookingDao.update(booking)
    suspend fun delete(booking: BookingEntity) = bookingDao.delete(booking)
    suspend fun getBookingById(id: Int) = bookingDao.getBookingById(id)
    suspend fun getBookingWithGuest(bookingId: Int) = bookingDao.getBookingWithGuest(bookingId)
    
    suspend fun updateBookingPaymentMethod(bookingId: Int, paymentMethod: String) {
        val booking = bookingDao.getBookingById(bookingId)
        booking?.let {
            val updatedBooking = it.copy(paymentMethod = paymentMethod)
            bookingDao.update(updatedBooking)
        }
    }
}
