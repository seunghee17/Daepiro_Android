package com.daepiro.numberoneproject.presentation.view.networkerror

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.DisastertypeDataModel
import com.daepiro.numberoneproject.databinding.FragmentBehaviorTabCBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.view.login.onboarding.GridviewAdapter

class BehaviorTabCFragment : BaseFragment<FragmentBehaviorTabCBinding>(R.layout.fragment_behavior_tab_c) {
    private lateinit var adapter: GridviewAdapter
    companion object{
        private var instance: BehaviorTabCFragment? = null
        fun newInstance(): BehaviorTabCFragment{
            if(instance == null){
                instance = BehaviorTabCFragment()
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
        adapter.updateList(data)
    }

    private fun setData():List<DisastertypeDataModel> = listOf(
        DisastertypeDataModel("비상대비","비상사태",R.drawable.ic_emergency),
        DisastertypeDataModel("비상대비","테러",R.drawable.ic_terror),
        DisastertypeDataModel("비상대비","화생방사고",R.drawable.ic_cbr),

        )
}