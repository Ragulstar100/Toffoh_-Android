package com.manway.Toofoh.android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.UnknownHostException

//
//class InternetViewModel:ViewModel(){
//    var test by mutableStateOf("ping ")
//    private val _test= flow<String> {
//        var v=0;
//        while (true) {
//            delay(1000)
//        }
//
//    }
//
//    init {
//        viewModelScope.launch {
//            _test.collect{
//                test=it
//            }
//        }
//
//    }
//
//
//}



