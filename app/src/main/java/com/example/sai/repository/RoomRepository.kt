package com.example.sai.repository

import com.example.sai.data.dao.RoomDao
import com.example.sai.data.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val roomDao: RoomDao) {
    fun getAllRooms(): Flow<List<RoomEntity>> = roomDao.getAllRoomsFlow()
    fun getAvailableRooms(): Flow<List<RoomEntity>> = roomDao.getAvailableRoomsFlow()

    suspend fun insert(room: RoomEntity) = roomDao.insert(room)
    suspend fun update(room: RoomEntity) = roomDao.update(room)
    suspend fun delete(room: RoomEntity) = roomDao.delete(room)
    suspend fun getRoom(number: Int) = roomDao.getRoom(number)
    suspend fun setBooked(number: Int, booked: Boolean) = roomDao.setBooked(number, booked)
}
