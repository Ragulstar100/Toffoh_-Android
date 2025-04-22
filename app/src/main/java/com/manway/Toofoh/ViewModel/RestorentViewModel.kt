package com.manway.Toofoh.ViewModel

import Ui.data.Filter
import Ui.data.GeoLocation
import Ui.enums.Availability
import android.annotation.SuppressLint
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.data.OrderStatus
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.android.distance
import com.manway.Toofoh.ui.channel.RDialog
import com.manway.Toofoh.ui.channel.dialogChannel
import com.manway.toffoh.admin.data.RestaurantInfo
import io.github.jan.supabase.auth.PostgrestFilterDSL
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.request.SelectRequestBuilder
import io.github.jan.supabase.postgrest.rpc
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch



class RestaurantViewModel: ViewModel() {
    //Common
    var list by mutableStateOf(listOf<RestaurantInfo>())
    @SuppressLint("StaticFieldLeak")
    private val _list= flow{
        val columns =
            Columns.raw(""" *, ${Table.FoodInfo.name}!inner ( "name","price","updated_at","isAvailable","foodCategory","foodType") """.trimIndent())
        while (true) {
            try {
            emit(supabase.from(Table.RestaurantInfo.name).select(columns, filter).decodeList<RestaurantInfo>())
            } catch (e: HttpRequestTimeoutException) {
                dialogChannel.send(RDialog("Internet", "Check Your Internet Connection"))
            }
            catch (e: Exception) {
                dialogChannel.send(RDialog("RestaurantViewModel", e.message.toString()))
            }
            delay(250L)
        }
    }

    var unchangedList by mutableStateOf(listOf<RestaurantInfo>())
    private val _unchangedList= flow{
        while (true) {
            try {
                emit(supabase.from(Table.RestaurantInfo.name).select().decodeList<RestaurantInfo>())
            } catch (e: Exception) {
                dialogChannel.send(RDialog("RestaurantViewModel", e.message.toString()))
            }

            delay(1000)
        }
    }

    suspend fun getRestaurant(channelId: String): RestaurantInfo {
        return supabase.from(Table.RestaurantInfo.name).select {
            filter {
                eq("channel_id", channelId)
            }
        }.decodeSingle<RestaurantInfo>()
    }


    private var RestaurantInfo: RestaurantInfo?=null
    var errorList by mutableStateOf((0..15).map { "none$it" })
    private  val _errorList= flow<List<String>>{
        while (true) {
            RestaurantInfo?.let {
                try {
                    emit(supabase.postgrest.rpc(CouldFunction.RestaurantInfoValidate.first, mapOf(CouldFunction.RestaurantInfoValidate.second[0] to RestaurantInfo)).decodeList())
                } catch (e: Exception) {
                    dialogChannel.send(RDialog("RestaurantViewModel", e.message.toString()))
                }
            }
        }
        delay(1000L)


    }
    var filterList by mutableStateOf(listOf<Filter>())
    init {



        viewModelScope.launch {
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
      //  order("id", Order.ASCENDING)


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

            eq("FoodInfo.foodCategory", foodCategory)
            pincode?.let {
                eq("address->>pincode", it)
            }
            if (isRestaurentTab) ilike("name", "%$search%") else ilike("FoodInfo.name", "%$search%")



        }



    }


    companion object {
        var vegOnly = false
    }

    var recommendlist by mutableStateOf(listOf<RestaurantInfo>())

    suspend fun recommended( range: LongRange){
        val _recommendlist=  flow{
            while (true) {
                try {
                    emit(supabase.from(Table.RestaurantInfo.name).select {
                        filter {
                            pincode?.let {
                                eq("address->>pincode", it)
                            }
                        }
                        limit(30)
                       // range(range)
                    }.decodeList<RestaurantInfo>())
                }catch (e:Exception){

                }

                delay(1000L)
            }
        }
        _recommendlist.collect{
            viewModelScope.launch(Dispatchers.IO) {
                recommendlist=it
            }
        }
    }


    var pincode:String?=null
    var aboveTheStar=-1.0
    var belowPrice=Double.MAX_VALUE
    private var isRestaurentTab = false

    private var search=""
    var location: GeoLocation? = null


    var foodCategory = FoodCategory.VEG
    private  var quickDelivery=false
    private var avaiable=false
    private  var newestEnable=false


    fun search(restaurentTab: Boolean, string: String) {
        search = string
        isRestaurentTab = restaurentTab
    }

    fun filterFoodCategory(foodCategory: FoodCategory) {
        this.foodCategory = foodCategory
    }





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


