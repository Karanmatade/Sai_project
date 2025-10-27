package com.example.sai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sai.data.entity.RoomEntity
import com.example.sai.repository.RoomRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomViewModel(private val repo: RoomRepository) : ViewModel() {
    val rooms: StateFlow<List<RoomEntity>> = repo.getAllRooms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val availableRooms: StateFlow<List<RoomEntity>> = repo.getAvailableRooms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addRoom(room: RoomEntity, onError: (Throwable) -> Unit = {}) {
        viewModelScope.launch {
            try { repo.insert(room) } catch (t: Throwable) { onError(t) }
        }
    }

    fun updateRoom(room: RoomEntity) {
        viewModelScope.launch { repo.update(room) }
    }

    fun deleteRoom(room: RoomEntity) {
        viewModelScope.launch { repo.delete(room) }
    }

    suspend fun getRoom(number: Int) = repo.getRoom(number)

    fun setBooked(number: Int, booked: Boolean) {
        viewModelScope.launch { repo.setBooked(number, booked) }
    }
}
