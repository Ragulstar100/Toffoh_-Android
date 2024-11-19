package com.manway.Toofoh.ViewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.data.OrderInfo

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class OrderViewModel(): ViewModel() {
    var list by mutableStateOf(listOf<OrderInfo>())
    private var orderInfo: OrderInfo?=null

    var errorList by mutableStateOf((0..15).map { "none$it" })
   private val _list= flow{
        while (true) {
                emit(supabase.from(Table.OrderInfo.name).select().decodeList<OrderInfo>())
            delay(1000L)
        }
    }

    fun feed(orderInfo: OrderInfo): OrderViewModel {
        this.orderInfo=orderInfo
        return this
    }
    private val _errorList= flow<List<String>>{
        while (true) {
                orderInfo?.let {
                    emit(
                        supabase.postgrest.rpc(
                            CouldFunction.orderinfovalidate.first, mapOf(
                                CouldFunction.orderinfovalidate.second[0] to orderInfo)).decodeList())
                }
           }
            delay(1000L)


    }

    init {

        viewModelScope.launch(Dispatchers.IO) {
            _errorList.collect{
                errorList=it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _list.collect {

                list=it
            }
        }
    }


}
