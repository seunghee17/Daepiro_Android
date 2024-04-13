package com.daepiro.numberoneproject.presentation.view.login.onboarding

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.DisastertypeDataModel

class GridviewAdapter(
    private var items:List<DisastertypeDataModel>,
    private val listener: onItemClickListener,
): RecyclerView.Adapter<GridviewAdapter.ViewHolder>() {
    interface onItemClickListener {
        fun onItemClickListener(disasterType: String, isSelected: Boolean)
    }
    var original : List<DisastertypeDataModel> = items.toList()
    private var currentCategory: String = ""

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.image)
        val disasterType : TextView = itemView.findViewById(R.id.disastertype)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_disastertype , parent,false)
        return GridviewAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItemList():List<DisastertypeDataModel>{
        return items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.isSelected = item.isSelected
        holder.image.setImageResource(item.imageResId)
        holder.disasterType.text = item.disasterType
        holder.itemView.setOnClickListener{
            item.isSelected = !item.isSelected
            //holder.itemView.isSelected = item.isSelected
            notifyItemChanged(position)
            listener.onItemClickListener(item.disasterType, item.isSelected)
        }

    }
    fun filterByCategory(category:String, selectedItems: List<DisastertypeDataModel>){
        currentCategory = category
        items = if(category == "") {
            original.toList()
        } else {
            original.filter { it.category == category }
        }
        items.forEach { item ->
            item.isSelected = selectedItems.any { it.disasterType == item.disasterType }
        }
        notifyDataSetChanged()
    }
    fun updateList(newData:List<DisastertypeDataModel>, selectedItems: List<DisastertypeDataModel>){
        items = newData.map {newItem->
            val isSelected = selectedItems.any {it.disasterType == newItem.disasterType}
            newItem.copy(isSelected = isSelected)
        }
        notifyDataSetChanged()
    }

    fun selectAllItems() {
        original.forEach { it.isSelected = true }
        items = original.filter { it.category == currentCategory || currentCategory.isEmpty() }
        notifyDataSetChanged()
    }

    // 모든 아이템 선택 해제
    fun deselectAllItems() {
        original.forEach { it.isSelected = false }
        items = original.filter { it.category == currentCategory || currentCategory.isEmpty() }
        notifyDataSetChanged()
    }
}