package com.daepiro.numberoneproject.domain.usecase
import com.daepiro.numberoneproject.data.model.ShelterUrlResponse
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.domain.repository.GetShelterRepository
import javax.inject.Inject

class GetShelterUseCase @Inject constructor(
    private val getShelterRepository: GetShelterRepository,

) {
    suspend operator fun invoke(token:String): ApiResult<ShelterUrlResponse> {
        return getShelterRepository.getShelterUrl(token)
    }
}