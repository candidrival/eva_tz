package com.example.eva_tz.presentation.gallery.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eva_tz.databinding.ItemGalleryMediaBinding

class GalleryAdapter(private val onClick: (Uri) -> Unit) : ListAdapter<Uri, GalleryAdapter.GalleryViewHolder>(MediaDiffCallback()) {

    inner class GalleryViewHolder(private val binding: ItemGalleryMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaUri: Uri) {
            binding.run {
                Glide.with(root.context)
                    .load(mediaUri)
                    .into(ivMediaPreview)
                binding.root.setOnClickListener { onClick(mediaUri) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size
}