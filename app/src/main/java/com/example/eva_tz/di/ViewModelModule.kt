package com.example.eva_tz.di

import com.example.eva_tz.presentation.activity.MainViewModel
import com.example.eva_tz.presentation.camera.CameraViewModel
import com.example.eva_tz.presentation.gallery.GalleryViewModel
import com.example.eva_tz.presentation.image.ImageViewModel
import com.example.eva_tz.presentation.splash.SplashViewModel
import com.example.eva_tz.utils.base.BaseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * ViewModels module
 */
val viewModel: Module = module {
    viewModel { BaseViewModel() }
    viewModel { MainViewModel() }
    viewModel { SplashViewModel() }
    viewModel { CameraViewModel() }
    viewModel { GalleryViewModel() }
    viewModel { ImageViewModel() }
}
