package com.daepiro.numberoneproject.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.daepiro.numberoneproject.data.datasources.ShelterDatabase
import com.daepiro.numberoneproject.data.model.ShelterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CheckShelterViewModel @Inject constructor(
    private val db: ShelterDatabase
) : ViewModel() {
    val _isactive = MutableLiveData<Boolean>()
    val isactive:LiveData<Boolean> = _isactive

    val _setUpdate = MutableLiveData<Boolean>()
    val setUpdate:LiveData<Boolean> = _setUpdate

        //주소 담는
//    val _selectaddress = MutableLiveData<String?>()
//    val selectaddress : LiveData<String?> = _selectaddress
    val _city = MutableLiveData<String?>()
    val city: LiveData<String?> = _city
    val _district = MutableLiveData<String?>()
    val district: LiveData<String?> = _district
    val _dong = MutableLiveData<String?>()
    val dong: LiveData<String?> = _dong

    //보여지는 주소
    val _onScreenAddress = MutableLiveData<String?>()
    val onScreenAddress:LiveData<String?> = _onScreenAddress


    //데이터 리스트를 담는 flow
    private val _currentList = MutableStateFlow<List<String>>(emptyList())
    val currentList : StateFlow<List<String>> = _currentList

    val currentLiveList: LiveData<List<String>> = _currentList.asLiveData()

    val isEmptyVisible : LiveData<Boolean> = MediatorLiveData<Boolean>().apply{
        //고른 지역에 대피소가 없을경우
        addSource(city){value=updateVisibility()}
        addSource(currentLiveList){value=updateVisibility()}

    }
    private fun updateVisibility():Boolean{
        return city.value != null && currentList.value.isNullOrEmpty()
    }
//    fun setSelectedAddress(address:String){
//        _selectaddress.value=address
//    }
    val shelterListUpdate = MediatorLiveData<Boolean>().apply{
        addSource(city){value = checkUpdateAvail()}
        addSource(setUpdate){value = checkUpdateAvail()}
    }
    fun checkUpdateAvail():Boolean{
        return city != null && setUpdate.value==true
    }
    suspend fun getShelterData() {
       withContext(Dispatchers.IO) {
           val allShelters = db.shelterDao().getAllShelters()
           if(allShelters.isEmpty()) {
           } else {
           }
       }
    }

    init {
        _isactive.value = false
        _city.value = null
        _district.value = null
        _dong.value = null
        viewModelScope.launch {
            getShelterData()
        }
    }

//    fun extractShelterFromLocal(context:Context, fileName:String, selectAddress:String, shelterType : String):List<JSONObject>{
//        val file = File(context.filesDir, fileName)
//        val jsonString = file.readText()
//        val jsonArray = JSONArray(jsonString)
//        val filteredList = mutableListOf<JSONObject>()
//        for(i in 0 until jsonArray.length()){
//            val jsonObject = jsonArray.getJSONObject(i)
//            val currentType = jsonObject.getString("shelterType")
//            val matchesType = shelterType.isEmpty() || currentType == shelterType
//            if(jsonObject.getString("fullAddress").contains(selectAddress) && matchesType){
//                filteredList.add(jsonObject)
//            }
//        }
//        return filteredList
//    }
    fun updateShelterList(city: String, district: String, dong:String){
        viewModelScope.launch {
            val newList = db.shelterDao().getShelters(city, district, dong)
            _currentList.value = newList
        }
    }

}