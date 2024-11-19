package com.manway.Toofoh.ViewModel

import Ui.data.Filter
import Ui.enums.Availability
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.RestaurantInfo
import io.github.jan.supabase.auth.PostgrestFilterDSL
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.request.SelectRequestBuilder
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch



class RestaurantViewModel: ViewModel() {
    //Common
    var list by mutableStateOf(listOf<RestaurantInfo>())
    @SuppressLint("SuspiciousIndentation")
    private val _list= flow{
        val columns=  Columns.raw(""" *, ${Table.FoodInfo.name}!inner ( "name","price","updated_at","isAvailable",foodCategory ) """.trimIndent())
        while (true) {
            emit(supabase.from(Table.RestaurantInfo.name)
                .select(columns,filter).decodeList<RestaurantInfo>())
            delay(250L)
        }
    }
    var unchangedList by mutableStateOf(listOf<RestaurantInfo>())
    private val _unchangedList= flow{
        while (true) {
            emit(supabase.from(Table.RestaurantInfo.name).select().decodeList<RestaurantInfo>())
            delay(1000)
        }
    }
    private var RestaurantInfo: RestaurantInfo?=null
    var errorList by mutableStateOf((0..15).map { "none$it" })
    private  val _errorList= flow<List<String>>{
        while (true) {
            RestaurantInfo?.let {
                emit(
                    supabase.postgrest.rpc(
                        CouldFunction.RestaurantInfoValidate.first, mapOf(
                            CouldFunction.RestaurantInfoValidate.second[0] to RestaurantInfo)).decodeList()
                )
            }
        }
        delay(1000L)


    }
    private  var filterList by mutableStateOf(listOf<Filter>())
    init {



        viewModelScope.launch() {
            _unchangedList.collect {

                unchangedList=it
            }
        }


        viewModelScope.launch(Dispatchers.IO) {
            _list.collect {

                list=it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _errorList.collect{
                errorList=it

            }
        }

    }

    var filter:@PostgrestFilterDSL() (SelectRequestBuilder.() -> Unit) = {
        //Order
        order("id", Order.ASCENDING)

        if(newestEnable)  order("created_at", Order.DESCENDING)

        if(quickDelivery)  order("estimatedDeliveryTime", Order.ASCENDING)


        filter {
            //above below
            when(aboveTheStar){
                3.0->gte("rating",3)
                4.0->gte("rating",4)
                4.5->gte("rating",4.5)
            }

            when(belowPrice){
                30.0->lte("${Table.FoodInfo.name}.price",30.0)
                100.0->lte("${Table.FoodInfo.name}.price",100)
                300.0->lte("${Table.FoodInfo.name}.price",300)
            }


            //Equal  Filter
            if (avaiable) {
                eq("isAvailable", Availability.Available)
            }

            eq("FoodInfo.foodCategory", FoodCategory.VEG)
            pincode?.let {
                eq("address->>pincode", it)
            }
            like("FoodInfo.name", "%$search%")




        }



    }




    var pincode:String?=null
    var aboveTheStar=-1.0
    var belowPrice=Double.MAX_VALUE
    private var search=""
    fun search(string: String){ search=string }


    private  var quickDelivery=false
    private var avaiable=false
    private  var newestEnable=false







    fun enableQuickDelivery(boolean:Boolean){
        quickDelivery=boolean
    }

    fun enableAvailable(boolean: Boolean){
        avaiable=boolean
    }

    fun enableNewest(boolean: Boolean){
        newestEnable=boolean
    }

    fun feed(RestaurantInfo: RestaurantInfo): RestaurantViewModel {
        this.RestaurantInfo=RestaurantInfo
        return this
    }






}


