package com.example.eva_tz.presentation.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eva_tz.utils.base.BaseFragment
import com.example.eva_tz.databinding.FragmentGalleryBinding
import com.example.eva_tz.presentation.gallery.adapter.GalleryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryFragment : BaseFragment<FragmentGalleryBinding, GalleryViewModel>() {

    override val viewModel: GalleryViewModel by viewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGalleryBinding.inflate(inflater, container, false)

    private val galleryAdapter: GalleryAdapter by lazy {
        GalleryAdapter(
            onClick = {
                val uriString = it.toString()
                findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToImageFragment(uriString))
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSavedPhotos(requireContext())
        initUi()
        initObservers()
    }

    private fun initUi() {
        binding.run {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            rvMedia.apply {
                adapter = galleryAdapter
            }
        }
    }

    private fun initObservers() {
        viewModel.mediaItems.observe(viewLifecycleOwner) { mediaList ->
            binding.run {
                if (mediaList.isEmpty()) {
                    emptyGalleryLayout.isVisible = true
                    rvMedia.isGone = true
                } else {
                    emptyGalleryLayout.isGone = true
                    rvMedia.isVisible = true
                }
            }
            galleryAdapter.submitList(mediaList)
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