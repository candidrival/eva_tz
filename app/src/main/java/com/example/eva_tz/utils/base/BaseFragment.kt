package com.example.eva_tz.utils.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding, V : BaseViewModel> : Fragment() {
    protected abstract val viewModel: V

    private var _binding: B? = null
    protected val binding: B
        get() = requireNotNull(_binding) { "viewBinding not set in $this" }

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): B

    protected abstract fun initInsets()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //sets top insets from activity
        viewModel.statusBarHeight = (activity as BaseActivity<*, *>).getStatusBarHeight()
        //sets bottom insets from activity
        viewModel.navigationBottomHeight =
            (activity as BaseActivity<*, *>).getNavigationBottomHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInsets()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun <T, LD : LiveData<T>> observeNullable(liveData: LD, onChanged: (T?) -> Unit) {
        liveData.observe(viewLifecycleOwner) {
            onChanged(it)
        }
    }
}