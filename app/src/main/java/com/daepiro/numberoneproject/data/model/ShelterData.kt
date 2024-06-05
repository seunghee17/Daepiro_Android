package com.daepiro.numberoneproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelters")
data class ShelterEntity(
    val fullAddress: String = "",
    val city: String = "",
    val district: String = "",
    val dong: String="",
) {
    @PrimaryKey(autoGenerate = true) var id: Int=0
}
