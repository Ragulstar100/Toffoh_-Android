package com.manway.Toofoh.ViewModel

import Ui.data.Address
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.data.OrderItem
import com.manway.toffoh.admin.data.FavInfo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    var orders by mutableStateOf(listOf<OrderItem>())
    var customerInfo: CustomerInfo? = null
    var activity: Activity? = null
    var onLunch: Boolean = true
    var internetAvailable = false
    var favList by mutableStateOf(listOf<FavInfo>())
    val _liveValue = flow<SharedViewModel> {
        while (true) {
            emit(this@SharedViewModel)
            delay(250L)
        }
    }
    var foodCategory = FoodCategory.VEG
    var liveValue by mutableStateOf(this)
    var currentAddress: Address? = null

    init {
        viewModelScope.launch {
            _liveValue.collect {
                liveValue = it
            }
        }
    }

}