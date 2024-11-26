package com.manway.Toofoh.ViewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.data.CommonFoodInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CommonFoodViewModel(): ViewModel() {
    var list by mutableStateOf(listOf<CommonFoodInfo>())
    private var CommonFoodInfo: CommonFoodInfo?=null

 //   var errorList by mutableStateOf((0..15).map { "none$it" })
    private val _list= flow{
        while (true) {
            emit(supabase.from(Table.CommonFoodInfo.name).select().decodeList<CommonFoodInfo>())
            delay(1000L)
        }
    }

    fun feed(CommonFoodInfo: CommonFoodInfo): CommonFoodViewModel {
        this.CommonFoodInfo=CommonFoodInfo
        return this
    }


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