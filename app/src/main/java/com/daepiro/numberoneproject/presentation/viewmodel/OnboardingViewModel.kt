package com.daepiro.numberoneproject.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daepiro.numberoneproject.data.datasources.ShelterDatabase
import com.daepiro.numberoneproject.data.model.AddresseModel
import com.daepiro.numberoneproject.data.model.DisasterTypeModel
import com.daepiro.numberoneproject.data.model.DisastertypeDataModel
import com.daepiro.numberoneproject.data.model.InitDataOnBoardingRequest
import com.daepiro.numberoneproject.data.model.ShelterEntity
import com.daepiro.numberoneproject.data.network.onFailure
import com.daepiro.numberoneproject.data.network.onSuccess
import com.daepiro.numberoneproject.domain.repository.OnBoardingRepository
import com.daepiro.numberoneproject.domain.usecase.GetShelterUseCase
import com.daepiro.numberoneproject.domain.usecase.OnBoardingUseCase
import com.daepiro.numberoneproject.presentation.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val onBoardingUseCase: OnBoardingUseCase,
    private val getShelterUseCase: GetShelterUseCase,
    private val db: ShelterDatabase
) : ViewModel() {
    val _showSelectAddress = MutableLiveData<MutableList<String>>(mutableListOf())
    val showSelectAddress:LiveData<MutableList<String>> = _showSelectAddress
    private val list = mutableListOf<ShelterEntity>()

    //api요청에 들어갈 데이터항목
    var getAddressForApi = mutableListOf<AddresseModel>()

    var realname:String = ""
    var nickname:String =""
    var fcmToken:String =""
    var disasterType = listOf<DisasterTypeModel>()

    fun updateShowAddress(address: Map<String, String>, index: Int){
        val address = "${address["lv1"]} ${address["lv2"]} ${address["lv3"]}".trim()
        val currentList = _showSelectAddress.value ?: mutableListOf()

        while (currentList.size <= index) {
            currentList.add("")
        }

        currentList.set(index, address)
        _showSelectAddress.value = currentList
    }



    fun updateData(objectData: Map<String, String>) {
        val addressModel = AddresseModel(
            lv1 = objectData["lv1"] ?: "",
            lv2 = objectData["lv2"] ?: "",
            lv3 = objectData["lv3"] ?: ""
        )
        getAddressForApi.add(addressModel)
    }

    fun getSheltersetLocal(){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            getShelterUseCase(token)
                .onSuccess {
                    Log.d("ShelterViewModel", "성공")
                    val data = getJsonData(it.link)
                    //여기부터 안찍힘
                    storeToDB(data)
                    Log.d("ShelterViewModel", "성공")
                }
                .onFailure {
                    //_url.value = it
                    Log.e("ShelterViewModel", "$it")
                }
        }
    }
    suspend fun storeToDB(data: List<ShelterEntity>) {
        withContext(Dispatchers.IO) {
            try {
                val allShelters = db.shelterDao().getAllShelters()
                if(allShelters.isEmpty()) {
                    db.shelterDao().saveShelters(data)
                    Log.d("storeToDB", "디비가 존재하지 않기에${data.size}")
                }
                Log.d("storeToDB", "${allShelters.size}")
            } catch (e: Exception) {
                Log.e("ShelterViewModel", "Error storing data in DB: ${e.message}")
            }

        }
    }
    suspend fun getJsonData(url: String): List<ShelterEntity> {
        withContext(Dispatchers.IO) {
            var url= URL(url)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            var str: String? = null
            val stringBuffer = StringBuffer()
            do {
                str = bufferedReader.readLine()
                if(str != null) {
                    stringBuffer.append(str.toString())
                }
            } while (str != null)
            bufferedReader.close()
            val data = stringBuffer.toString()
            val jsonArray = JSONArray(data)
            for(index in 0 until jsonArray.length()) {
                val jsonObject= jsonArray.getJSONObject(index)
                val fullAddress = jsonObject.getString("fullAddress")
                val city = jsonObject.getString("city")
                val district = jsonObject.getString("district")
                val dong = jsonObject.getString("dong")
                list.add(ShelterEntity(fullAddress, city, district, dong))

            }
            Log.d("aldjladfa", "데이터개수${list.size}")
        }
        return list
    }

    //온보딩시 입력한 데이터 전송할 api호출 함수
    suspend fun postInitData(body: InitDataOnBoardingRequest){
        val token = "Bearer ${tokenManager.accessToken.first()}"
        onBoardingUseCase.invoke(token,body)
            .onSuccess {
                Log.d("postInitData" , "$it")
            }
            .onFailure {
                Log.e("postInitData" , "$it")
            }
    }



}