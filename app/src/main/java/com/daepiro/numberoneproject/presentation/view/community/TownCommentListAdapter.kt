package com.daepiro.numberoneproject.presentation.view.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.Content

class TownCommentListAdapter(
    private val listener: onItemClickListener,
    private val getTimeDifference: (String) -> String
): PagingDataAdapter<Content, TownCommentListAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Content>() {
            override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem == newItem
        }
    }
    interface onItemClickListener{
        fun onItemClick(id:Int)
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tag : TextView = itemView.findViewById(R.id.tag)
        val title:TextView = itemView.findViewById(R.id.title)
        val content:TextView = itemView.findViewById(R.id.content)
        val image : ImageView =itemView.findViewById(R.id.image)
        val writerInfo:TextView = itemView.findViewById(R.id.writer_info)
        val likenum:TextView = itemView.findViewById(R.id.like_num)
        val replynum:TextView = itemView.findViewById(R.id.reply_num)

        fun bind(item: Content, listener: onItemClickListener, getTimeDifference: (String) -> String){
            title.text = item.title
            tag.text = when(item.tag.toString()){
                "LIFE" -> "일상"
                "TRAFFIC" -> "교통"
                "SAFETY" -> "치안"
                "NONE" -> "기타"
                else-> "전체"
            }
            content.text = item.content
            //시간 처리
            val time = getTimeDifference(item.createdAt)
            writerInfo.text = " ${item.address} ∙ ${item.ownerNickName} ∙ ${time}"

            Glide.with(itemView.context)
                .load(item.thumbNailImageUrl)
                .into(image)

            likenum.text = item.articleLikeCount.toString()
            replynum.text = item.commentCount.toString()
            itemView.setOnClickListener{
                listener.onItemClick(item.id)
            }
        }


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_townlist,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, listener, getTimeDifference)
    }
}