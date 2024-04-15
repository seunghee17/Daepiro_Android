package com.daepiro.numberoneproject.presentation.view.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.CommunityTownReplyResponseItem

class CommunityTownDetailReplyAdapter(
    private val context: Context,
    private var items:List<CommunityTownReplyResponseItem> = listOf(),
    private val listener : onItemClickListener,
    private val getTimeDifference: (String) -> String
):RecyclerView.Adapter<CommunityTownDetailReplyAdapter.ViewHolder>() {

    interface onItemClickListener{
        fun onAdditionalItemClick(commentid:Int)
        fun onReplyClick(commentid:Int)

        fun onLikedClick(commentid:Int)
        fun onUnLikedClick(commentid:Int)
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val userInfo: TextView = itemView.findViewById(R.id.user_info)
        val content : TextView = itemView.findViewById(R.id.content)
        val likenum:TextView = itemView.findViewById(R.id.like_num)
        val likebtn: ImageView = itemView.findViewById(R.id.like_btn)
        val additional:ImageView = itemView.findViewById(R.id.additional)
        val rereply:ImageView = itemView.findViewById(R.id.rereply_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_commentlist,parent,false)
        return CommunityTownDetailReplyAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = ContextCompat.getColor(holder.itemView.context, R.color.orange_500)
        if(position< items.size){
            val item = items[position]
            val time = getTimeDifference(item.createdAt)
            holder.userInfo.text = "${item.authorNickName} ∙ ${time}"
            holder.content.text = item.content

            holder.likenum.text = if(item.likeCount >0) {
                holder.likenum.visibility = View.VISIBLE
                item.likeCount.toString()
            } else {
                holder.likenum.visibility = View.GONE
                ""
            }
            //현재상태 감지용
            if(item.liked) {
                holder.likebtn.setColorFilter(ContextCompat.getColor(context, R.color.orange_500))
            } else {
                holder.likebtn.setColorFilter(ContextCompat.getColor(context, R.color.secondary_300))
            }
            holder.likebtn.setOnClickListener {
                val newIsLiked = !item.liked
                item.liked = newIsLiked
                holder.likebtn.setColorFilter(
                    ContextCompat.getColor(context, if (newIsLiked) R.color.orange_500 else R.color.secondary_300)
                )
                holder.likenum.text = item.likeCount.toString()
                holder.likenum.visibility = if(item.likeCount > 0) View.VISIBLE else View.GONE
                if(newIsLiked) {
                    listener.onLikedClick(item.commentId)
                } else {
                    listener.onUnLikedClick(item.commentId)
                }
                notifyItemChanged(position)
            }
            holder.additional.setOnClickListener{
                //listener.onAdditionalItemClick(item.commentId)
            }
            holder.rereply.setOnClickListener{
                listener.onReplyClick(item.commentId)
            }
        }


    }
    fun updateList(newData:List<CommunityTownReplyResponseItem>){
        items=newData
        notifyDataSetChanged()
    }
}