package com.daepiro.numberoneproject.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daepiro.numberoneproject.data.datasources.ShelterDatabase
import com.daepiro.numberoneproject.data.model.ShelterEntity
import com.daepiro.numberoneproject.data.network.onFailure
import com.daepiro.numberoneproject.data.network.onSuccess
import com.daepiro.numberoneproject.domain.usecase.GetShelterUseCase
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
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getShelterUseCase: GetShelterUseCase,
    private val db: ShelterDatabase
): ViewModel() {
    private val list = mutableListOf<ShelterEntity>()
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
                    Log.d("storeToDB", "${data.size}")
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
}