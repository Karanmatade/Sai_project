package com.example.sai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sai.data.db.AppDatabase
import com.example.sai.repository.BookingRepository
import com.example.sai.repository.RoomRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val db = AppDatabase.getInstance(context)
    private val roomRepo = RoomRepository(db.roomDao())
    private val bookingRepo = BookingRepository(db.bookingDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            RoomViewModel::class.java -> RoomViewModel(roomRepo) as T
            BookingViewModel::class.java -> BookingViewModel(bookingRepo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}
