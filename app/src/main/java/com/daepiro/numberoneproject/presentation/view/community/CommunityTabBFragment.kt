package com.daepiro.numberoneproject.presentation.view.community

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.os.Build
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.databinding.FragmentCommunityTabBBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.util.Extensions.repeatOnStarted
import com.daepiro.numberoneproject.presentation.viewmodel.CommunityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@AndroidEntryPoint
class CommunityTabBFragment : BaseFragment<FragmentCommunityTabBBinding>(R.layout.fragment_community_tab_b) {
    val viewModel by activityViewModels<CommunityViewModel>()
    private lateinit var adapter : TownCommentListAdapter
    private var region : String = ""
    private var latitude:Double=0.0
    private var longitude:Double=0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentTag=""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        binding.viewModel = viewModel
        binding.all.isSelected = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()


        val tags = listOf(binding.all, binding.life, binding.traffic, binding.safety,binding.other)
        tags.forEach{textview->
            textview.setOnClickListener{
                selectTags(textview,tags)
            }
        }

        binding.writeBtn.setOnClickListener{
            findNavController().navigate(R.id.action_communityFragment_to_communityTownWritingFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.setLongitude(longitude)
        viewModel.setLatitude(latitude)
        viewModel.setTag(currentTag)
        viewModel.setRegion(region)
        collectTownCommentList()
    }

    override fun setupInit() {
        super.setupInit()
    }

    override fun subscribeUi() {
        super.subscribeUi()
        viewModel.setLongitude(longitude)
        viewModel.setLatitude(latitude)
        viewModel.setTag(currentTag)
        viewModel.setRegion(region)
        collectTownCommentList()

        repeatOnStarted {
            viewModel.selectRegion.collectLatest { response->
                region = response
            }
        }

    }

    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    lifecycleScope.launch {
                        if(isAdded){
                            viewModel.selectRegion.collect{
                                viewModel.setRegion(viewModel.selectRegion.value)
                                viewModel.setLatitude(latitude)
                                viewModel.setLongitude(longitude)
                                collectTownCommentList()
                            }
                        }
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
           1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {

                }
            }
        }
    }

    private fun collectTownCommentList(){
        repeatOnStarted {
            viewModel.combinedData.collectLatest { data->
                adapter.submitData(viewLifecycleOwner.lifecycle,data)
            }
        }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecyclerView(){
        adapter = TownCommentListAdapter(object :TownCommentListAdapter.onItemClickListener{
            override fun onItemClick(id: Int) {
                viewModel.getTownDetail(id)
                viewModel.setReply(id)
                findNavController().navigate(R.id.action_communityFragment_to_communityTownDetailFragment)
            }
        },viewModel::getTimeDifference
        )
        binding.recycler.adapter = adapter
    }

    private fun UpdateTagData(tag:String){
        viewModel.setLongitude(longitude)
        viewModel.setLatitude(latitude)
        viewModel.setTag(tag)
        viewModel.setRegion(region)
        collectTownCommentList()
    }


    private fun selectTags(selectedTag: TextView, textviews:List<TextView>){
        textviews.forEach{
            it.isSelected = it == selectedTag
        }
        currentTag = when (selectedTag) {
            binding.all -> ""
            binding.life -> "LIFE"
            binding.safety -> "SAFETY"
            binding.traffic -> "TRAFFIC"
            binding.other -> "NONE"
            else -> ""
        }
        UpdateTagData(currentTag)
    }

}

