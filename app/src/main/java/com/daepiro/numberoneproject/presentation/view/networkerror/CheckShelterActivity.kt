package com.daepiro.numberoneproject.presentation.view.networkerror

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.ShelterRecyclerList
import com.daepiro.numberoneproject.databinding.ActivityCheckShelterBinding
import com.daepiro.numberoneproject.presentation.base.BaseActivity
import com.daepiro.numberoneproject.presentation.viewmodel.CheckShelterViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckShelterActivity : BaseActivity<ActivityCheckShelterBinding>(R.layout.activity_check_shelter) {
    private val locationSettingDialogFragment = LocationSettingDialogFragment()
    val viewModel by viewModels<CheckShelterViewModel>()
    private lateinit var adapter : ShelterListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        adapter = ShelterListAdapter()
        binding.recyclerList.adapter = adapter
        binding.includeAppBar.appBarText.text = "대피소 조회"

        binding.includeAppBar.backBtn.setOnClickListener{
            onBackPressed()
        }

        setupTabLayout()

        viewModel.currentList.asLiveData().observe(this, Observer { list->
//            val shelterToList = list.map{data ->
//                ShelterRecyclerList(
//                    fullAddress = data ,
//                    facilityFullName = data
//                )
//            }
            adapter.updateShelters(list)
        })

        viewModel.shelterListUpdate.observe(this, Observer { shouldUpdate ->
            if (shouldUpdate) {
                updateShelterListBasedOnTab(binding.tabLayout.selectedTabPosition)
            }
        })


        binding.touchContainer.setOnClickListener{
            setLocationSelect()
        }


    }

    override fun onStart() {
        super.onStart()
        locationSettingDialogFragment.setOnItemSelectedListener(object : LocationSettingDialogFragment.OnItemSelectedListener{
            override fun onItemSelected(sendItems: String) {
                Log.d("CheckShelterActivity", "$sendItems")
            }

        })
    }
    private var previousIndex = 0
    private fun setupTabLayout(){
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateShelterListBasedOnTab(tab?.position)
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }
    private fun getShelterType(tabPosition:Int?):String{
        return when(tabPosition){
            0 -> ""
            1-> "EARTHQUAKE"
            2->"CIVIL_DEFENCE"
            else-> "FLOOD"
        }
    }
    fun setLocationSelect(){
        val dialog = locationSettingDialogFragment
        dialog.show(supportFragmentManager, "LocationSelect")
    }

    private fun updateShelterListBasedOnTab(tabPosition: Int?) {
//        viewModel.selectaddress.value?.let{address->
//            val shelterType = getShelterType(tabPosition)
//            viewModel.updateShelterList(viewModel.city.value ?: "", viewModel.district.value ?: "", viewModel.dong.value ?: "")
//        }
        viewModel.updateShelterList(viewModel.city.value ?: "", viewModel.district.value ?: "", viewModel.dong.value ?: "")
    }


}