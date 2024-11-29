package com.manway.Toofoh.ViewModel


import Ui.enums.Availability
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FoodViewModel(): ViewModel() {
    var list by mutableStateOf(listOf<FoodInfo>())
    private var FoodInfo: FoodInfo?=null


    var errorList by mutableStateOf((0..15).map { "none$it" })
    private val _list= flow{
        while (true) {
            try {
                emit(supabase.from(Table.FoodInfo.name).select(){

                }.decodeList<FoodInfo>())
            }catch (e:Exception){

            }

            delay(1000L)
        }
    }





    fun feed(FoodInfo: FoodInfo): FoodViewModel {
        this.FoodInfo=FoodInfo
        return this
    }
    private val _errorList= flow<List<String>>{
        while (true) {
            FoodInfo?.let {
             //  emit(supabase.postgrest.rpc(CouldFunction.FoodInfoValidate.first, mapOf(CouldFunction.FoodInfoValidate.second[0] to FoodInfo)).decodeList())
            }
        }
        delay(1000L)


    }


      


    private var avaiable=false
    fun enableAvailable(boolean: Boolean){
        avaiable=boolean
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