package com.daepiro.numberoneproject.data.datasources

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.daepiro.numberoneproject.data.model.ShelterEntity

@Database(entities = [ShelterEntity::class], version = 1)
abstract class ShelterDatabase: RoomDatabase() {
    abstract fun shelterDao(): ShelterDAO
}