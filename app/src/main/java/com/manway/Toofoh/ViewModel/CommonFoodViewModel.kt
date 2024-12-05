package com.manway.Toofoh.ViewModel


import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.data.CommonFoodInfo
import com.manway.Toofoh.ui.android.showErrorDialog
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CommonFoodViewModel : ViewModel() {
    var list by mutableStateOf(listOf<CommonFoodInfo>())
    private var CommonFoodInfo: CommonFoodInfo? = null
    var context: Context?=null

    //   var errorList by mutableStateOf((0..15).map { "none$it" })
    private val _list = flow {
        while (true) {
            try {
                emit(supabase.from(Table.CommonFoodInfo.name).select().decodeList<CommonFoodInfo>())
            }  catch (e: HttpRequestTimeoutException) {
                context?.let { showErrorDialog("Internet" ,"Check Your Internet Connection",it) }
             }
            catch (e: Exception) {
                context?.let { showErrorDialog("CustomerFoodViewModel" ,e.message.toString(),it) }
            }
            delay(250L)
        }
    }

    fun feedContext(context: Context): CommonFoodViewModel{
        this.context=context
        return this
    }

    fun feed(CommonFoodInfo: CommonFoodInfo): CommonFoodViewModel {
        this.CommonFoodInfo = CommonFoodInfo
        return this
    }


    init {

//        viewModelScope.launch(Dispatchers.IO) {
//            _errorList.collect{
//                errorList=it
//            }
//        }
        viewModelScope.launch {
            _list.collect {

                list = it
            }
        }
    }

}