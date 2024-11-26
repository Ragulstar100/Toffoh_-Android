package com.manway.Toofoh.ViewModel

import Ui.enums.Availability
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.ui.data.InternetListener
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CustomerViewModel(): ViewModel() {
    var list by mutableStateOf(listOf<CustomerInfo>())
    private var customerInfo: CustomerInfo?=null

    var errorList by mutableStateOf((0..15).map { "none$it" })
    var connectionCheck=object :InternetListener{
        override fun internet(availability: Boolean) {

        }

        override fun other(e: Exception) {

        }

    }
    private val _list= flow{
        while (true) {
            emit(supabase.from(Table.CustomerInfo.name).select().decodeList<CustomerInfo>())
            delay(1000L)
        }
    }


    fun feed(CustomerInfo: CustomerInfo): CustomerViewModel {
        this.customerInfo=CustomerInfo
        return this
    }
    private val _errorList= flow<List<String>>{
        while (true) {

           customerInfo?.let {
               try {
                   emit(
                       supabase.postgrest.rpc(
                           CouldFunction.customerInfoValidate.first, mapOf(
                               CouldFunction.customerInfoValidate.second[0] to customerInfo
                           )
                       ).decodeList()
                   )
                   connectionCheck.internet(true)
               }catch (e: HttpRequestTimeoutException){
                   delay(3000)
                   connectionCheck.internet(false)
               }catch (e:Exception){
                   connectionCheck.other(e)
               }
            }
        }
        delay(250L)


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