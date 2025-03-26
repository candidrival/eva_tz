package com.example.eva_tz.utils.base

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding

/**
 * Base Activity
 */
abstract class BaseActivity<B : ViewBinding, V : BaseViewModel> : AppCompatActivity() {
    // This abstract property must be implemented to provide the ViewModel instance of type V.
    protected abstract val viewModel: V

    // This variable holds the view binding instance, which may be null initially.
    protected var binding: B? = null

    /**
     * This abstract function must be implemented to inflate a view binding using
     * the provided LayoutInflater.
     * @return an instance of the view binding type B,which is specific to the implementing class.
     */
    protected abstract fun inflateBinding(inflater: LayoutInflater): B

    // This private variable tracks whether a toast message is currently being displayed.
    // It is used to prevent showing multiple toast messages simultaneously
    // or to manage toast visibility.
    private var isToastShowing = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val newConfiguration = Configuration(newBase?.resources?.configuration)
        //prevent system font-size changing effects
        newConfiguration.fontScale = 1f
        applyOverrideConfiguration(newConfiguration)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //chang status bar color to transparent
        makeStatusBarTransparent()

        binding = inflateBinding(layoutInflater)
        binding?.root?.let { setTopAndBottomHeight(it) }
        setContentView(binding?.root)
    }
    /**
     * This function controls the visibility of bottom buttons.
     *
     * @param isVisible determines whether the buttons should be visible or not
     * @param withAnimation indicates whether the visibility change should be animated
     * (default is true)
     */
    open fun bottomButtonsVisibility(isVisible: Boolean, withAnimation: Boolean = true) {}

    /**
     * @return the top system window inset in pixels
     */
    fun getStatusBarHeight(): MutableLiveData<Int> = viewModel.statusBarHeight

    /**
     * @return MutableLiveData<Int> representing the height of the status bar in pixels
     */
    fun getNavigationBottomHeight(): MutableLiveData<Int> = viewModel.navigationBottomHeight

    /**
     * This function sets up an observer for LiveData that allows the observed value to be nullable.
     * It triggers the provided 'onChanged' callback whenever the LiveData's value changes,
     * including when it's null. The observation is tied to the lifecycle of the fragment's view.
     */
    protected fun <T, LD : LiveData<T>> observeNullable(liveData: LD, onChanged: (T?) -> Unit) {
        liveData.observe(this) { value ->
            onChanged(value)
        }
    }

    /**
     * This function sets up an observer for LiveData, which triggers
     * the provided 'onChanged' callback whenever the LiveData's value changes.
     * It observes the LiveData within the lifecycle of the fragment's view.
     * The callback is only invoked if the LiveData's value is not null.
     */
    protected fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this) { value ->
            value?.let(onChanged)
        }
    }

    /**
     * This function sets up a listener to apply window insets to the provided container view.
     * It updates the ViewModel's statusBarHeight and navigationBottomHeight
     * LiveData based on the current insets.
     */
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

    fun Activity.makeStatusBarTransparent() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                navigationBarColor = Color.TRANSPARENT
                setDecorFitsSystemWindows(false)
            } else {
                @Suppress("DEPRECATION")
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                @Suppress("DEPRECATION")
                addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}