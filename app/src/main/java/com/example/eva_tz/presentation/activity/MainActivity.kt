package com.example.eva_tz.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.eva_tz.presentation.camera.CameraFragment
import com.example.eva_tz.utils.base.BaseActivity
import com.example.eva_tz.utils.doubleClickExit
import com.example.stacklab_tz.R
import com.example.stacklab_tz.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (getCurrentFragment()) {
                is CameraFragment -> {
                    doubleClickExit(this@MainActivity)
                }

                else -> navHostFragment?.findNavController()?.popBackStack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    fun getCurrentFragment(): Fragment? {
        navHostFragment?.childFragmentManager?.backStackEntryCount
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }
}