package com.manway.Toofoh.ViewModel


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.data.OrderInfo
import com.manway.Toofoh.ui.android.showErrorDialog

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")
class OrderViewModel(): ViewModel() {
    var list by mutableStateOf(listOf<OrderInfo>())
    private var orderInfo: OrderInfo?=null
    var customerInfo: CustomerInfo?=null
    var context: Context?=null

    var errorList by mutableStateOf((0..15).map { "none$it" })
   private val _list= flow{
        while (true) {
                try {
                    emit(supabase.from(Table.OrderInfo.name).select {
                        customerInfo?.let {
                            filter {
                                eq("customer_channel_id", it.channelId ?: "")
                            }
                        }

                    }.decodeList<OrderInfo>())
                }catch (e: HttpRequestTimeoutException) {
                    context?.let { showErrorDialog("Internet" ,"Check Your Internet Connection",it) }
                }
                catch (e: Exception) {
                    context?.let { showErrorDialog("CustomerFoodViewModel" ,e.message.toString(),it) }
                }
            Log.e("adder",list.toString())
            delay(250L)
        }
    }


    fun feed(customerInfo: CustomerInfo): OrderViewModel {
        this.customerInfo=customerInfo
        return this
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
