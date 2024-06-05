package com.daepiro.numberoneproject.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daepiro.numberoneproject.data.model.ShelterEntity

@Dao
interface ShelterDAO {
    @Query("SELECT * FROM shelters")
    suspend fun getAllShelters(): List<ShelterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShelters(shelters: List<ShelterEntity>)
}