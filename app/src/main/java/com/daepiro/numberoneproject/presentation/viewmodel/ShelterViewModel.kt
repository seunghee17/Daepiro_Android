package com.daepiro.numberoneproject.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daepiro.numberoneproject.data.datasources.ShelterDatabase
import com.daepiro.numberoneproject.data.model.ShelterListResponse
import com.daepiro.numberoneproject.data.model.ShelterRequestBody
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.data.network.onFailure
import com.daepiro.numberoneproject.data.network.onSuccess
import com.daepiro.numberoneproject.domain.usecase.AroundShelterUseCase
import com.daepiro.numberoneproject.presentation.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelterViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val aroundShelterUseCase: AroundShelterUseCase,
): ViewModel() {
    private val _sheltersList = MutableStateFlow(ShelterListResponse())
    val sheltersList = _sheltersList.asStateFlow()

    val shelterLoadingState = MutableStateFlow(true)

    private lateinit var db: ShelterDatabase

    private val _shelterDataState = MutableLiveData<ApiResult<Unit>>()
    val shelterDataState: LiveData<ApiResult<Unit>> = _shelterDataState

    fun getAroundSheltersList(shelterRequestBody: ShelterRequestBody) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"

            aroundShelterUseCase(token, shelterRequestBody)
                .onSuccess {
                    _sheltersList.value = it
                    shelterLoadingState.emit(false)
                }
                .onFailure {

                }
        }
    }


}