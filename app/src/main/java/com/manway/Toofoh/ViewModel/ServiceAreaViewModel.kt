package com.manway.Toofoh.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.android.showErrorDialog
import com.manway.toffoh.admin.data.ServiceArea

import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ServiceAreaViewModel(): ViewModel() {
    var list by mutableStateOf(listOf<ServiceArea>())
    private var ServiceArea: ServiceArea?=null
    @SuppressLint("StaticFieldLeak")
    private var context: Context?=null

    var errorList by mutableStateOf((0..15).map { "none$it" })
    private val _list= flow{
        while (true) {
            try {
                emit(supabase.from(Table.ServiceArea.name).select().decodeList<ServiceArea>())
            }catch (e: HttpRequestTimeoutException) {
                context?.let { showErrorDialog("Internet" ,"Check Your Internet Connection",it) }
            }
            catch (e: Exception) {
                context?.let { showErrorDialog("CustomerFoodViewModel" ,e.message.toString(),it) }
            }
            delay(250L)
        }
    }

    fun feed(ServiceArea: ServiceArea): ServiceAreaViewModel {
        this.ServiceArea=ServiceArea
        return this
    }

    fun feed(context: Context): ServiceAreaViewModel {
        this.context=context
        return this
    }

//    private val _errorList= flow<List<String>>{
//        while (true) {
//            ServiceArea?.let {
//                emit(
//                    supabase.postgrest.rpc(CouldFunction.ServiceAreaValidate.first, mapOf(CouldFunction.ServiceAreaValidate.second[0] to ServiceArea)).decodeList()
//                )
//            }
//        }
//        delay(1000L)
//
//
//    }

    init {

//        viewModelScope.launch(Dispatchers.IO) {
//            _errorList.collect{
//                errorList=it
//            }
//        }
        viewModelScope.launch(Dispatchers.IO) {
            _list.collect {

                list=it
            }
        }
    }

}