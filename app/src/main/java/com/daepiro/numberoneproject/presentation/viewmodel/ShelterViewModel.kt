package com.daepiro.numberoneproject.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daepiro.numberoneproject.data.model.ShelterListResponse
import com.daepiro.numberoneproject.data.model.ShelterRequestBody
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.data.network.onFailure
import com.daepiro.numberoneproject.data.network.onSuccess
import com.daepiro.numberoneproject.domain.usecase.AroundShelterUseCase
import com.daepiro.numberoneproject.domain.usecase.GetShelterUseCase
import com.daepiro.numberoneproject.presentation.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ShelterViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val aroundShelterUseCase: AroundShelterUseCase,
    private val getShelterUseCase: GetShelterUseCase,
    private val filesDir: File,
): ViewModel() {
    private val _sheltersList = MutableStateFlow(ShelterListResponse())
    val sheltersList = _sheltersList.asStateFlow()

    val shelterLoadingState = MutableStateFlow(true)
    
    private val _url = MutableStateFlow<ApiResult.Failure?>(null)
    val url = _url.asStateFlow()

    private val _shelterDataState = MutableLiveData<ApiResult<Unit>>()
    val shelterDataState: LiveData<ApiResult<Unit>> = _shelterDataState

    val shelterList1 = MutableStateFlow(ShelterListResponse())
    val shelterList2 = MutableStateFlow(ShelterListResponse())
    val shelterList3 = MutableStateFlow(ShelterListResponse())
    val shelterList4 = MutableStateFlow(ShelterListResponse())

    fun getAroundSheltersList(shelterRequestBody: ShelterRequestBody) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"

            aroundShelterUseCase(token, shelterRequestBody)
                .onSuccess {
                    shelterLoadingState.emit(false)
                    _sheltersList.value = it
                }
                .onFailure {

                }
        }
    }

    fun getAroundSheltersInDetailList(shelterRequestBody: ShelterRequestBody) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"

            aroundShelterUseCase(token, shelterRequestBody)
                .onSuccess {
                    when(shelterRequestBody.shelterType) {
                        null -> shelterList1.value = it
                        "지진" -> shelterList2.value = it
                        "수해" -> shelterList3.value = it
                        "민방위" -> shelterList4.value = it
                    }
                }
                .onFailure {

                }
        }
    }

    fun getSheltersetLocal(){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            val fileName = "shelter_data.json"
            val file = File(filesDir, fileName)
            getShelterUseCase(token,file)
                .onSuccess {
                    Log.d("ShelterViewModel", "성공")
                }
                .onFailure {
                    _url.value = it
                    Log.e("ShelterViewModel", "$it")
                }
        }
    }


}