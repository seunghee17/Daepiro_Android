package com.daepiro.numberoneproject.presentation.view.networkerror

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.DisastertypeDataModel
import com.daepiro.numberoneproject.databinding.FragmentBehaviorTabDBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.view.login.onboarding.GridviewAdapter

class BehaviorTabDFragment : BaseFragment<FragmentBehaviorTabDBinding>(R.layout.fragment_behavior_tab_d) {
    private lateinit var adapter: GridviewAdapter
    companion object{
        private var instance: BehaviorTabDFragment? = null
        fun newInstance(): BehaviorTabDFragment{
            if(instance == null){
                instance = BehaviorTabDFragment()
            }
            return instance!!
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = setData()
        binding.recycler.layoutManager = GridLayoutManager(requireContext(),3)
        adapter = GridviewAdapter(
            emptyList(),
            object : GridviewAdapter.onItemClickListener{
                override fun onItemClickListener(disasterType: String, isSelected: Boolean) {
                }
            }
        )
        binding.recycler.adapter = adapter
        //adapter.updateList(data)
    }

    private fun setData():List<DisastertypeDataModel> = listOf(
        DisastertypeDataModel("기타","실종",R.drawable.ic_missing),
        DisastertypeDataModel("기타","기타",R.drawable.ic_add),

        )
}