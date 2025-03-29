package com.example.eva_tz.presentation.image

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.eva_tz.utils.base.BaseFragment
import com.example.eva_tz.databinding.FragmentImageBinding
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageChromaKeyBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorInvertFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageFragment : BaseFragment<FragmentImageBinding, ImageViewModel>() {

    override val viewModel: ImageViewModel by viewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentImageBinding.inflate(inflater, container, false)

    private val args: ImageFragmentArgs by navArgs()

    private var useFilter: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.run {
            Glide.with(requireContext())
                .load(args.uri.toUri())
                .into(ivPhoto)
            shareMediaBtn.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("image/*")
                shareIntent.putExtra(Intent.EXTRA_STREAM, args.uri.toUri())
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            addFilter.setOnClickListener {
                addFilter()
            }

            savePhotoFilter.setOnClickListener {
                saveImage()
            }

            removeFilter.setOnClickListener {
                Glide.with(requireContext())
                    .load(args.uri.toUri())
                    .into(ivPhoto)
                removeFilter.isGone = true
                savePhotoFilter.isGone = true
            }
        }
    }

    private fun addFilter() {
        val imageSepiaFilter = GPUImageSepiaToneFilter()
        val imageRGBFilter = GPUImageRGBFilter()
        val imageChromaKeyBlendFilter = GPUImageChromaKeyBlendFilter()
        val imageColorMatrixFilter = GPUImageColorMatrixFilter()
        val imageColorInvertFilter = GPUImageColorInvertFilter()
        val filters = listOf(
            imageColorInvertFilter, imageColorMatrixFilter, imageSepiaFilter, imageRGBFilter, imageChromaKeyBlendFilter
        )
        val gpuImage = GPUImage(requireContext())
        gpuImage.setFilter(filters.random())
        val filteredBitmap = gpuImage.getBitmapWithFilterApplied(binding.ivPhoto.drawable.toBitmap())
        binding.ivPhoto.setImageBitmap(filteredBitmap)
        binding.removeFilter.isVisible = true
        binding.savePhotoFilter.isVisible = true
    }

    private fun saveImage() {
        val folderName = "evaTZ"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "filtered_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/$folderName"
            )
        }
        val imageUri: Uri? = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(it)
            outputStream?.use { stream ->
                binding.ivPhoto.drawable.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }

            requireContext().contentResolver.notifyChange(imageUri, null)
        }
    }

    override fun initInsets() {
        observeNullable(viewModel.statusBarHeight) {
            if (it != null) { // sets android status bar height
                binding.statusBar.updateLayoutParams { height = it }
            }
        }
        observeNullable(viewModel.navigationBottomHeight) {
            if (it != null) { // sets android bottom navigation buttons height
                binding.navigationBar.updateLayoutParams { height = it }
            }
        }
    }
}