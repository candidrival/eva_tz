package com.example.eva_tz.utils.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {

    var statusBarHeight = MutableLiveData<Int>()

    var navigationBottomHeight = MutableLiveData<Int>()
}