package com.daepiro.numberoneproject.domain.repository

import com.daepiro.numberoneproject.data.model.ShelterUrlResponse
import com.daepiro.numberoneproject.data.network.ApiResult

interface GetShelterRepository {
    suspend fun getShelterUrl(token: String): ApiResult<ShelterUrlResponse>
}