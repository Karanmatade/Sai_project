package com.example.sai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sai.data.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(room: RoomEntity)

    @Update
    suspend fun update(room: RoomEntity)

    @Delete
    suspend fun delete(room: RoomEntity)

    @Query("SELECT * FROM rooms ORDER BY roomNumber ASC")
    fun getAllRoomsFlow(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE isBooked = 0 ORDER BY roomNumber ASC")
    fun getAvailableRoomsFlow(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE roomNumber = :roomNumber LIMIT 1")
    suspend fun getRoom(roomNumber: Int): RoomEntity?

    @Query("UPDATE rooms SET isBooked = :booked WHERE roomNumber = :roomNumber")
    suspend fun setBooked(roomNumber: Int, booked: Boolean)
}
