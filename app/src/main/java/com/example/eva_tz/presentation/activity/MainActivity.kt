package com.example.eva_tz.presentation.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.eva_tz.R
import com.example.eva_tz.databinding.ActivityMainBinding
import com.example.eva_tz.presentation.camera.CameraFragment
import com.example.eva_tz.utils.doubleClickExit
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentNavContainer)
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

    fun getStatusBarHeight(): MutableLiveData<Int> = viewModel.statusBarHeight

    fun getNavigationBottomHeight(): MutableLiveData<Int> = viewModel.navigationBottomHeight

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTopAndBottomHeight(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    fun getCurrentFragment(): Fragment? {
        navHostFragment?.childFragmentManager?.backStackEntryCount
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    private fun setTopAndBottomHeight(container: View) {
        ViewCompat.setOnApplyWindowInsetsListener(container) { _, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                viewModel.statusBarHeight.value =
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val navigationBarsBottom =
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                viewModel.navigationBottomHeight.value = if (imeBottom > navigationBarsBottom) {
                    imeBottom
                } else {
                    navigationBarsBottom
                }
            } else {
                @Suppress("DEPRECATION")
                viewModel.statusBarHeight.value = insets.systemWindowInsetTop
                @Suppress("DEPRECATION")
                viewModel.navigationBottomHeight.value = insets.systemWindowInsetBottom
            }
            insets
        }
    }
}