package com.example.eva_tz.presentation.gallery.adapter

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil

class MediaDiffCallback : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean =
        oldItem == newItem
}