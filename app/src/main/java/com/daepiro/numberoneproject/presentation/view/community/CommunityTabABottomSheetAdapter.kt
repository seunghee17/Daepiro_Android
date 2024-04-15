package com.daepiro.numberoneproject.presentation.view.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.ConversationModel

class CommunityTabABottomSheetAdapter(
    private val context: Context,
    private var items:List<ConversationModel>,
    private val listener:onItemClickListener,
): RecyclerView.Adapter<CommunityTabABottomSheetAdapter.ViewHolder>() {
    private lateinit var subadapter: CommunityTownDetailRereplyAdapter
    interface onItemClickListener{
        fun onItemClickListener()
        fun onLikeClicked(conversationId: Int)
        fun onUnlikeClicked(conversationId: Int)
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val info : TextView = itemView.findViewById(R.id.user_info)
        val content:TextView = itemView.findViewById(R.id.content)
        val recycler : RecyclerView = itemView.findViewById(R.id.recycler)
        val likeBtn: ImageView = itemView.findViewById(R.id.like_btn)
        val likeNum: TextView = itemView.findViewById(R.id.like_num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_commentlist,parent,false)
        return CommunityTabABottomSheetAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        subadapter = CommunityTownDetailRereplyAdapter(emptyList())
        holder.recycler.adapter = subadapter
        if(position < items.size){
            val item = items[position]
            holder.info.text = item.info
            holder.content.text = item.content
            subadapter.updateList(item.childs)
            holder.itemView.setOnClickListener{
                listener.onItemClickListener()
            }

            if(item.isLiked) {
                holder.likeBtn.setColorFilter(ContextCompat.getColor(context, R.color.orange_500))
            } else {
                holder.likeBtn.setColorFilter(ContextCompat.getColor(context, R.color.secondary_300))
            }
            holder.likeBtn.setOnClickListener{
                val newIsLiked = !item.isLiked
                item.isLiked = newIsLiked
                holder.likeBtn.setColorFilter(
                    ContextCompat.getColor(context, if (newIsLiked) R.color.orange_500 else R.color.secondary_300)
                )
                item.like += if(newIsLiked) 1 else -1
                holder.likeNum.text = item.like.toString()
                holder.likeNum.visibility = if(item.like>0) View.VISIBLE else View.GONE

                if(newIsLiked){
                    listener.onLikeClicked(item.conversationId)
                } else {
                    listener.onUnlikeClicked(item.conversationId)
                }
                notifyItemChanged(position)
            }

            holder.likeNum.text = item.like.toString()
            if(item.like != 0) {
                holder.likeNum.visibility = View.VISIBLE
            } else {
                holder.likeNum.visibility = View.GONE
            }
        }

    }
    fun updateList(newData:List<ConversationModel>){
        items = newData
        notifyDataSetChanged()
    }
}