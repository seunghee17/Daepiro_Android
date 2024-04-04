package com.daepiro.numberoneproject.presentation.view.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daepiro.numberoneproject.R

class CommunityWritingImageAdapter(private var images: MutableList<String>)
    : RecyclerView.Adapter<CommunityWritingImageAdapter.ImageViewHolder>() {
    var onImageRemoved: ((String) -> Unit)? = null
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView = view.findViewById(R.id.image)
        val deleteBtn : ImageButton = view.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_img, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.imageView.context).load(imageUrl).into(holder.imageView)
        holder.deleteBtn.setOnClickListener{
            val removedUri = images.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            onImageRemoved?.invoke(removedUri)
        }
    }

    fun addImage(imageUri: String){
        images.add(imageUri)
        notifyItemInserted(images.size-1)
    }

    fun removeImage(position: Int){
        images.removeAt(position)
        notifyItemRemoved(position)
    }

}