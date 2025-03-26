package com.example.eva_tz.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.example.eva_tz.utils.base.BaseFragment
import com.example.stacklab_tz.databinding.FragmentImageBinding
import com.example.stacklab_tz.databinding.FragmentSplashBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override val viewModel: SplashViewModel by viewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSplashBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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