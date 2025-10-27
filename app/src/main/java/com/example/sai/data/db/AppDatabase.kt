package com.example.sai.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sai.data.dao.BookingDao
import com.example.sai.data.dao.RoomDao
import com.example.sai.data.entity.BookingEntity
import com.example.sai.data.entity.GuestEntity
import com.example.sai.data.entity.RoomEntity

@Database(entities = [RoomEntity::class, BookingEntity::class, GuestEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hotel_booking_db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
