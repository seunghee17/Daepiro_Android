package com.daepiro.numberoneproject.presentation.view.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daepiro.numberoneproject.databinding.ItemDisasterCheckListBinding
import com.daepiro.numberoneproject.presentation.view.funding.main.FundingListAdapter

class DisasterCheckListAdapter: RecyclerView.Adapter<DisasterCheckListAdapter.CustomViewHolder>() {
    private val checkStateList1 = MutableList(5) {false}
    private val checkStateList2 = MutableList(4) {false}
    private val checkStateList3 = MutableList(2) {false}
    private var selectedChip = 0

    private var checkList = listOf<String>()

    inner class CustomViewHolder(val binding: ItemDisasterCheckListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvCheckList.text = item

            binding.cbCheckList.setOnCheckedChangeListener(null)

            when(selectedChip) {
                1 -> {
                    binding.cbCheckList.isChecked = checkStateList1[position]
                }
                2 -> {
                    binding.cbCheckList.isChecked = checkStateList2[position]
                }
                3 -> {
                    binding.cbCheckList.isChecked = checkStateList3[position]
                }
            }

            binding.clTop.setOnClickListener {
                when(selectedChip) {
                    1 -> {
                        checkStateList1[position] = !checkStateList1[position]
                    }
                    2 -> {
                        checkStateList2[position] = !checkStateList2[position]
                    }
                    3 -> {
                        checkStateList3[position] = !checkStateList3[position]
                    }
                }
                binding.cbCheckList.isChecked = !binding.cbCheckList.isChecked

                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisasterCheckListAdapter.CustomViewHolder {
        val view = ItemDisasterCheckListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisasterCheckListAdapter.CustomViewHolder, position: Int) {
        holder.bind(checkList[position])

    }

    fun setData(newData: List<String>, selectedPosition: Int) {
        checkList = newData
        selectedChip = selectedPosition
        notifyDataSetChanged()
    }

    override fun getItemCount() = checkList.size
}