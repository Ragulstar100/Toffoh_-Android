package com.manway.Toofoh.data

import Ui.data.*
import Ui.enums.Availability
import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight

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
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.ServiceArea

import com.manway.toffoh.admin.ui.MyOutlinedTextField
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

enum class FoodCategory{ VEG, NON_VEG }

@Serializable
public data class  CustomerInfo   (val id:Int?=null, val created_at:LocalDateTime?=null, val channelId:String?=null, val profileUrl: ImageUrl?=null, val email:String?, val phoneNumber: PhoneNumber, val name:String, val foodCategory: FoodCategory, val address:List<Address>, val others:HashMap<String,String>?=null ){

    companion object{
        // val (id_error,created_at_error,channelId_error,profileUrl_error,email_error,phoneNumber_error,name_error,foodCategory_error,address_error,others_error) = listOf(0 to "id",1 to "created_at",2 to "channelId",3 to "profileUrl",4 to "email",5 to "phoneNumber",6 to "name",7 to "foodCategory",8 to "address",9 to "others")
        val initialCustomerInfo= CustomerInfo(null, null,null,null,"", PhoneNumber("+91","000000000" ), "", FoodCategory.VEG, listOf(
            Address("","","")
        ), hashMapOf("none" to "none"))
    }

    @Composable
    fun itemView(refIndex:Int){
            Row(Modifier.padding(end = 10.dp).border(1.dp,Color.LightGray, RoundedCornerShape(15)).padding(15.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
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
    fun profileInfo(colseAction:(Boolean)->Unit){
        var errorList by remember {
            mutableStateOf((0..9).map{
                "none$it"
            })
        }
        var open by remember {
            mutableStateOf(false)
        }
        var scope= rememberCoroutineScope()


        var customerInfo by remember {
            mutableStateOf(this)
        }

        var listServiceArea by remember {
            mutableStateOf(listOf<ServiceArea>())
        }
        var _listServiceArea= flow {
            while (true){
                emit( supabase.postgrest.from(Table.ServiceArea.name).select().decodeList<ServiceArea>())
                delay(1000L)
            }
        }

        scope.launch {
            _listServiceArea.collect {
                listServiceArea =it
            }
        }


        CustomerInfoScope(customerInfo) {

            customerInfo.let {

                scope.launch {
                    errorList = supabase.postgrest.rpc(
                        CouldFunction.customerInfoValidate.first,
                        mapOf(CouldFunction.customerInfoValidate.second[0] to customerInfo)
                    ).decodeList<String>()

                }
            }




                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(Modifier.height(50.dp))

                      //  AsyncImage(profileUrl,"",Modifier.clip(CircleShape).size(100.dp))

                    val emaichips = remember { mutableStateOf(listOf(ChipData("Email", true), ChipData("Phone Number", false))) }
                    var emailChip by remember { mutableStateOf(ChipData("Email", true)) }
                    var emailBackup by remember {
                        mutableStateOf(email?:"")
                    }
//                    ChipGroup(Modifier.width(300.dp), emaichips, {
//                        emailChip = it
//                        if (it.label == "Email") customerInfo = customerInfo.copy(email = emailBackup) else customerInfo = customerInfo.copy(email = null)
//                    }) {
//                        Text(it.label, Modifier.background(if (it.isChecked) Color.LightGray.copy(0.75f) else Color.Unspecified, RoundedCornerShape(0)).border(1.dp, Color.LightGray.copy(0.75f), RoundedCornerShape(0)).padding(8.dp).width(100.dp))
//                    }

                    email?.let {
                        MyOutlinedTextField(emailBackup, {
                            emailBackup = it
                            customerInfo = customerInfo.copy(email = emailBackup)
                        }, "Email", errorList, 4, interact = false, readOnly =true)
                    }

                    var phoneNumberBackup by remember {
                        mutableStateOf(phoneNumber)
                    }

                    Spacer(Modifier.height(10.dp))


                    Text(errorList.toString(), Modifier.width(300.dp))

                    Text(errorList[5])

                    PhoneNumberField(phoneNumberBackup.phoneNumber,email==null,errorList[5].isEmpty(),{
                        customerInfo=customerInfo.copy(phoneNumber =it.toPhoneNumber())
                    },{
                        customerInfo=customerInfo.copy(phoneNumber = "".toPhoneNumber())
                    })

                    Spacer(Modifier.height(10.dp))
                    MyOutlinedTextField(name, { customerInfo = customerInfo.copy(name = it) }, "Name", errorList, 6)

                    Spacer(Modifier.height(10.dp))
                    val chips = remember {
                        mutableStateOf(
                            listOf(
                                ChipData(FoodCategory.VEG.name, true),
                                ChipData(FoodCategory.NON_VEG.name, false)
                            )
                        )
                    }
                    var chip by remember { mutableStateOf(ChipData(FoodCategory.VEG.name, true)) }
                    ChipGroup(Modifier.width(300.dp), chips, onCheckedChange = { _chip ->
                        chip = _chip;
                        if (_chip.isChecked) customerInfo.copy(foodCategory = if(FoodCategory.VEG.name==_chip.label) FoodCategory.VEG else FoodCategory.NON_VEG)
                    }) {
                        Text(
                            it.label,
                            Modifier.background(
                                if (it.isChecked) Color.LightGray.copy(0.75f) else Color.Unspecified,
                                RoundedCornerShape(0)
                            ).border(1.dp, Color.LightGray.copy(0.75f), RoundedCornerShape(0)).padding(8.dp)
                                .width(100.dp)
                        )
                    }

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

                    HorizontalPager(pagerState, Modifier.width(300.dp)) {
                        var address = remember { mutableStateOf(addresses.value[it]) }
                        Column(Modifier.width(400.dp).background(Color.Unspecified,MaterialTheme.shapes.medium).padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.width(200.dp),horizontalArrangement = Arrangement.SpaceBetween) {
                                IconButton({
                                    scope.launch {
                                        pagerState.scrollToPage(it - 1)
                                    }
                                }, enabled = (it > 0)) {
                                    Icon(Icons.Default.KeyboardArrowLeft, "")
                                }
                                Text("Address${it + 1}")
                                IconButton({
                                    scope.launch {
                                        pagerState.scrollToPage(it + 1)
                                    }
                                }, enabled = (it < addresses.value.size - 1)) {
                                    Icon(Icons.Default.KeyboardArrowRight, "")
                                }
                            }
//
//                            AddressField(address, trailIconAddress = {
//                                IconButton({
//                                    addresses.add(Address("", "", ""))
//                                }) {
//                                    Icon(Icons.Default.Add, "")
//                                }
//                            }, trailIconGeolocation = {
//                                IconButton({
//                                    addresses.delete(it)
//                                }, enabled = (addresses.value.size > 1)) {
//                                    Icon(Icons.Default.Delete, "")
//                                }
//                            }
//                            )


                        }
                        addresses.update(it, address.value)
                        customerInfo = customerInfo.copy(address = addresses.value)
                    }
                    if(addresses.value.map { listServiceArea.map { it.pincode.toString() }.contains(it.pincode) }.contains(false)) Text("Enter Pin code Not Available")

                    else  if (!errorList.mapIndexed { i,it-> if(i!=4) it.isEmpty() else true ; }.contains(false)) Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton({
                            scope.launch {
                                supabase.from(Table.CustomerInfo.name).upsert(customerInfo)
                                colseAction(false)
                            }
                        }) {
                            Text("Ok")
                        }
                        Spacer(Modifier.width(100.dp).height(50.dp))
                    }
                }
        }
    }
}

//@OptIn(SupabaseExperimental::class)
//@Composable
//fun CustomerInfoItemView(){
//    val scope= rememberCoroutineScope()
//    var dlg by remember {
//        mutableStateOf(false)
//    }
//    Scaffold(floatingActionButton = {
//        FloatingActionButton({
//            dlg=true
//        },Modifier.size(100.dp)){
//            Icon(Icons.Default.Add,"")
//        }
//    }) {
//        Column(Modifier.fillMaxWidth()) {
//            var customerInfoList by remember {
//                mutableStateOf(listOf<CustomerInfo>())
//            }
//            scope.launch {
//                customerInfoList = supabase.postgrest.from(Table.CustomerInfo.name).select().decodeList<CustomerInfo>()
//            }
//            customerInfoList.forEach {
//                it.itemView()
//            }
//            var open by remember {
//                mutableStateOf(true)
//            }
//            Dialog("Add CustomerInfo",{
//                CustomerInfo.initialCustomerInfo.profileInfo{
//                    open=it
//                }
//            },open)
//        }
//
//    }
//
//}

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
