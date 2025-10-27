package com.example.sai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sai.data.entity.BookingEntity
import com.example.sai.data.entity.GuestEntity
import com.example.sai.data.entity.BookingWithGuest
import com.example.sai.repository.BookingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel(private val repo: BookingRepository) : ViewModel() {
    val bookings: StateFlow<List<BookingEntity>> = repo.getAllBookings()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val bookingsWithGuests: StateFlow<List<BookingWithGuest>> = repo.getAllBookingsWithGuests()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addBooking(b: BookingEntity, onInserted: (Long) -> Unit = {}) {
        viewModelScope.launch { onInserted(repo.insert(b)) }
    }
    
    fun addGuest(guest: GuestEntity, onInserted: (Long) -> Unit = {}) {
        viewModelScope.launch { onInserted(repo.insertGuest(guest)) }
    }
    
    fun updateBooking(b: BookingEntity) { viewModelScope.launch { repo.update(b) } }
    fun deleteBooking(b: BookingEntity) { viewModelScope.launch { repo.delete(b) } }
    suspend fun getBookingById(id: Int) = repo.getBookingById(id)
    suspend fun getBookingWithGuest(bookingId: Int) = repo.getBookingWithGuest(bookingId)
    
    fun updateBookingPaymentMethod(bookingId: Int, paymentMethod: String, onUpdated: () -> Unit = {}) {
        viewModelScope.launch { 
            repo.updateBookingPaymentMethod(bookingId, paymentMethod)
            onUpdated()
        }
    }
}
