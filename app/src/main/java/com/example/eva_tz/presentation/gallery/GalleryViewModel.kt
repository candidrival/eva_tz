package com.example.eva_tz.presentation.gallery

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eva_tz.utils.base.BaseViewModel

class GalleryViewModel: BaseViewModel() {

    private val _mediaItems = MutableLiveData<List<Uri>>()
    val mediaItems: LiveData<List<Uri>> get() = _mediaItems

    fun getSavedPhotos(context: Context) {
        val imageList = mutableListOf<Uri>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%${Environment.DIRECTORY_PICTURES}/evaTZ%")

        context.contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(collection, id)
                imageList.add(uri)
            }
        }
       _mediaItems.postValue(imageList)
    }
}