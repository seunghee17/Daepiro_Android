package com.daepiro.numberoneproject.data.repositoryimpl
import com.daepiro.numberoneproject.data.model.ShelterUrlResponse
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.data.network.ApiService
import com.daepiro.numberoneproject.domain.repository.GetShelterRepository
import javax.inject.Inject

class GetShelterRepositoryImpl @Inject constructor(
    private val service: ApiService,
): GetShelterRepository {
    override suspend fun getShelterUrl(token: String): ApiResult<ShelterUrlResponse> {
        return service.getShelterFromUrl(token)
    }

}