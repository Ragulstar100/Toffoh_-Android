package com.manway.Toofoh.data

import Ui.data.*
import Ui.enums.Availability
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manway.Toofoh.ViewModel.CustomerViewModel
import com.manway.Toofoh.ViewModel.SharedViewModel
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.data.InternetListener
import com.manway.toffoh.admin.data.ServiceArea

import com.manway.toffoh.admin.ui.MyOutlinedTextField
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

enum class FoodCategory{ VEG, NON_VEG }

@Serializable
data class  CustomerInfo   (val id:Int?=null, val created_at:LocalDateTime?=null, val channelId:String?=null, val profileUrl: ImageUrl?=null, val email:String?, val phoneNumber: PhoneNumber, val name:String, val foodCategory: FoodCategory, val address:List<Address>, val others:HashMap<String,String>?=null ){

    companion object{
        // val (id_error,created_at_error,channelId_error,profileUrl_error,email_error,phoneNumber_error,name_error,foodCategory_error,address_error,others_error) = listOf(0 to "id",1 to "created_at",2 to "channelId",3 to "profileUrl",4 to "email",5 to "phoneNumber",6 to "name",7 to "foodCategory",8 to "address",9 to "others")
        val initialCustomerInfo = CustomerInfo(
            null,
            null,
            null,
            null,
            "",
            PhoneNumber("+91", ""),
            "",
            FoodCategory.VEG,
            listOf(Address.intial),
            hashMapOf("none" to "none")
        )
        var addressPickable = true
        var pickedAddress: Address? = null
    }

    @Composable
    fun itemView(refIndex:Int){
            Row(
                Modifier
                    .padding(end = 10.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(15))
                    .padding(15.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Text(refIndex.toString(), modifier = Modifier.width(40.dp), style = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Text(name, modifier = Modifier.width(250.dp), style = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Text(phoneNumber.toString(), modifier = Modifier.width(250.dp), style = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Text(email ?: "Not Mentioned", modifier = Modifier.width(250.dp), style = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Button({}, modifier = Modifier.scale(1.25f), colors = ButtonDefaults.buttonColors(contentColor = Color.Red.copy(0.50f))) {
                    Text("Delete")
                }
                Spacer(Modifier.width(10.dp))
            }
            Spacer(Modifier.height(10.dp))
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun profileInfo(sharedViewModel: SharedViewModel, colseAction: (Boolean) -> Unit) {

        var customer = viewModel<CustomerViewModel>()


        var open by remember {
            mutableStateOf(false)
        }
        var scope = rememberCoroutineScope()


        var customerInfo by remember {
            mutableStateOf(this)
        }


        var listServiceArea by remember {
            mutableStateOf(listOf<ServiceArea>())
        }
        var _listServiceArea = flow {
            while (true) {
                emit(
                    supabase.postgrest.from(Table.ServiceArea.name).select()
                        .decodeList<ServiceArea>()
                )
                delay(1000L)
            }
        }

        scope.launch {
            _listServiceArea.collect {
                listServiceArea = it
            }
        }

        var connection by remember {
            mutableStateOf(true)
        }

        if (!connection) {
            Dialog({}) {
                Text("check internet connection")
            }
        }



        CustomerInfoScope(customerInfo) {

            customerInfo.let {
                val errorList = customer.feed(it).errorList


                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Spacer(Modifier.height(50.dp))

                    //  AsyncImage(profileUrl,"",Modifier.clip(CircleShape).size(100.dp))


                    Text(
                        email ?: phoneNumber.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    var phoneNumberBackup by remember {
                        mutableStateOf(phoneNumber)
                    }

                    Spacer(Modifier.height(10.dp))

                    if (email != null) PhoneNumberField(
                        phoneNumberBackup,
                        listOf(if (!phoneNumberBackup.checkPhoneNumber()) "Enter Valid Phone Number" else ""),
                        0
                    ) {
                        phoneNumberBackup = it
                        customerInfo = customerInfo.copy(phoneNumber = it)
                    }


                    Spacer(Modifier.height(10.dp))
                    MyOutlinedTextField(
                        name,
                        { customerInfo = customerInfo.copy(name = it) },
                        "Name",
                        errorList,
                        6
                    )

                    Spacer(Modifier.height(10.dp))
                    val chips = remember {
                        mutableStateOf(
                            listOf(
                                ChipData(FoodCategory.VEG.name, true),
                                ChipData(FoodCategory.NON_VEG.name, false)
                            )
                        )
                    }



                    Row(
                        Modifier
                            .width(280.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(50.dp)
                        ) {
                            RadioButton(foodCategory == FoodCategory.VEG, {
                                customerInfo = customerInfo.copy(foodCategory = FoodCategory.VEG)
                            })
                            Text(
                                "VEG",
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .padding(10.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(50.dp)
                        ) {
                            RadioButton(foodCategory == FoodCategory.NON_VEG, {
                                customerInfo =
                                    customerInfo.copy(foodCategory = FoodCategory.NON_VEG)
                            })
                            Text("NON VEG", modifier = Modifier.background(Color.White))
                        }
                    }

//                    errorList.filterIndexed { i, it -> !listOf(4,8).contains(i) }
//                        .forEachIndexed { i, it ->
//                            Text("$i $it")
//                        }


                    //Address

                    val addresses = remember {
                        mutableStateOf(arrayListOf<Address>().apply {
                            addAll(address)
                        })
                    }

                    val pagerState = rememberPagerState(0) {
                        addresses.value.size
                    }
                    Spacer(Modifier.height(20.dp))

//                    HorizontalPager(pagerState, Modifier.width(300.dp)) {
//                        var address = remember { mutableStateOf(addresses.value[it]) }
//                        Column(Modifier.width(400.dp).background(Color.Unspecified,MaterialTheme.shapes.medium).padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//                            Row(Modifier.width(200.dp),horizontalArrangement = Arrangement.SpaceBetween) {
//                                IconButton({
//                                    scope.launch {
//                                        pagerState.scrollToPage(it - 1)
//                                    }
//                                }, enabled = (it > 0)) {
//                                    Icon(Icons.Default.KeyboardArrowLeft, "")
//                                }
//                                Text("Address${it + 1}")
//                                IconButton({
//                                    scope.launch {
//                                        pagerState.scrollToPage(it + 1)
//                                    }
//                                }, enabled = (it < addresses.value.size - 1)) {
//                                    Icon(Icons.Default.KeyboardArrowRight, "")
//                                }
//                            }
////
////                            AddressField(address, trailIconAddress = {
////                                IconButton({
////                                    addresses.add(Address("", "", ""))
////                                }) {
////                                    Icon(Icons.Default.Add, "")
////                                }
////                            }, trailIconGeolocation = {
////                                IconButton({
////                                    addresses.delete(it)
////                                }, enabled = (addresses.value.size > 1)) {
////                                    Icon(Icons.Default.Delete, "")
////                                }
////                            }
////                            )
//
//
//                        }
//                        addresses.update(it, address.value)
//                        customerInfo = customerInfo.copy(address = addresses.value)
//                    }


                    if (!errorList.filterIndexed { i, it -> !listOf(4, 5, 8).contains(i) }
                            .map { it.isEmpty() }
                            .contains(false) && phoneNumberBackup.checkPhoneNumber()) Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton({
                            scope.launch {
                                sharedViewModel.customerInfo = customerInfo
                                supabase.from(Table.CustomerInfo.name).upsert(customerInfo)
                                colseAction(false)
                            }
                        }) {
                            Text("Ok")
                        }
                        Spacer(
                            Modifier
                                .width(100.dp)
                                .height(50.dp))
                    }
                }
            }
        }
    }

}


@Composable
fun CustomerInfoScope(customerInfo: CustomerInfo, scope: @Composable CustomerInfo.()->Unit){
    scope(customerInfo)
}


@Serializable
data class FoodDetails(
    val id: String,
    val created_at: String?=null,
    val updated_at: String?=null,
    val restaurantChannelId: String,//Foregin Key Action On Restaurant Table
    val imageUrl: String?=null,
    val name: String,
    val price: Double,
    val foodCategory: String,//Veg or Non Veg
    val foodType: List<String>,//Desert or Main Course,Dairy product
    val isAvailable: Availability,
    val rating: Double,
    val numberOfRatings: Int,
    val description: String="",
    val others: HashMap<String, String> =hashMapOf()
)

@Serializable
data class RestaurantDetails(
    val id: String,
    val created_at: String?=null,
    val updated_at: String?=null,
    val channelId: String,
    val ownerId: String,
    val fssaiNumber: String,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val imageUrl: String,
    val cuisine: List<String>,
    val upiId: String,
    val minOrderAmount: Double,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,//minutes
    val isAvailable: Availability,
    val rating: Double,
    val numberOfRatings: Int,
    val others: HashMap<String, String> =hashMapOf()
)
