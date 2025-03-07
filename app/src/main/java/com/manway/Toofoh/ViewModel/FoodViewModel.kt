package com.manway.Toofoh.ViewModel


import Ui.enums.Availability
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.android.showErrorDialog
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FoodViewModel : ViewModel() {
    var list by mutableStateOf(listOf<FoodInfo>())
    private var FoodInfo: FoodInfo?=null
     var context: Context?=null


    var errorList by mutableStateOf((0..15).map { "none$it" })
    private val _list= flow{
        while (true) {
            try {
                emit(supabase.from(Table.FoodInfo.name).select {}.decodeList<FoodInfo>())
            } catch (e: HttpRequestTimeoutException) {
                try {
                    context?.let {
                        showErrorDialog(
                            "Internet",
                            "Check Your Internet Connection",
                            it
                        )
                    }
                }catch (e:Exception){

                }
            }
            catch (e: Exception) {
                try {
                    context?.let {
                        showErrorDialog(
                            "CustomerFoodViewModel",
                            e.message.toString(),
                            it
                        )
                    }
                } catch (e:Exception) {

                }
            }

            delay(1000L)
        }
    }


    fun feedContext(context: Context): FoodViewModel {
        this.context=context
        return this
    }



    fun feed(FoodInfo: FoodInfo): FoodViewModel {
        this.FoodInfo=FoodInfo
        return this
    }



      


    private var avaiable=false
    fun enableAvailable(boolean: Boolean){
        avaiable=boolean
    }
   
    


    init {

        viewModelScope.launch(Dispatchers.IO) {
            _list.collect {
                list=it
            }
        }
    }

}