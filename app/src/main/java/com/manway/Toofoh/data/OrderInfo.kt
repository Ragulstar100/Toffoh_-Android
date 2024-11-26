package com.manway.Toofoh.data

import Ui.data.*
import android.annotation.SuppressLint
import com.manway.Toofoh.ViewModel.CustomerViewModel
import com.manway.Toofoh.ViewModel.RestaurantViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.manway.Toofoh.ViewModel.OrderViewModel
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.toffoh.admin.data.orderItemView
import com.manway.toffoh.admin.ui.MyOutlinedTextField
import data.enums.Role
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class  OrderInfo(
    val order_id: String? = null,
    val customer_channel_id: String?,
    val role: Role,
    val customer_name: String,
    val customer_address: Address,
    val customer_phone_number: PhoneNumber,
    val order_items: List<OrderItem>,
    val order_total: Double,
    val order_status: OrderStatus,
    val order_time: LocalDateTime? = null,
    val delivery_time: LocalDateTime? = null,
    val payment_method: PaymentMethod,
    val delivery_instructions: String? = null,
){

    companion object{
        val initialOrderInfo= OrderInfo(null,null,
            Role.Customer,"", Address("hello","641020","geo location"),
            PhoneNumber("+91","000000000000"), listOf(),0.0,
            OrderStatus.PENDING,null,null,
            PaymentMethod.CASH_ON_DELIVERY,"demo" )
    }

    @Composable
    fun itemView(refIndex:Int,deleteAction:()->Unit){
            //Min Width 800dp
            Row(Modifier.padding(end = 50.dp).border(1.dp, Color.LightGray, RoundedCornerShape(15)).padding(15.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Text(refIndex.toString(), modifier = Modifier.width(40.dp), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Text(customer_name, modifier = Modifier.width(150.dp), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Text(customer_phone_number.toString(), modifier = Modifier.width(150.dp), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Text(customer_address.pincode, modifier = Modifier.width(150.dp), maxLines = 1, style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Start, fontWeight = FontWeight.W300))
                Text(order_items.map { it.name  }.toString(), maxLines = 1, modifier = Modifier.width(150.dp), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.W300))
                Spacer(Modifier.width(10.dp))
                Button(deleteAction, modifier = Modifier, colors = ButtonDefaults.buttonColors(contentColor = Color.Red.copy(0.50f))) {
                    Text("Delete")
                }
                Spacer(Modifier.width(10.dp))
            }
            Spacer(Modifier.height(10.dp))
    }

    @Composable
    fun orderAdd(){
        val (main,customerPicker,restorentPicker,foodPicker)= listOf(0,1,2,3)

        var subScreen by remember {
            mutableStateOf(main)
        }
        var orderInfo by remember {
            mutableStateOf(this)
        }
        var order= viewModel<OrderViewModel>()
        var customer= viewModel<CustomerViewModel>()
        var restaurant= viewModel<RestaurantViewModel>()
        val errorList=order.feed(orderInfo).errorList


        OrderInfoScope(orderInfo) {
            var restaurantInfo by remember {
                mutableStateOf<RestaurantInfo?>(null)
            }

            AnimatedVisibility(subScreen == main) {


                    Column(Modifier.verticalScroll(rememberScrollState()).clip(RoundedCornerShape(2)).padding(vertical = 5.dp).background(Color.White, RoundedCornerShape(2)).width(450.dp).height(1500.dp).padding(vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {


                        MyOutlinedTextField(customer_name, { orderInfo = orderInfo.copy(customer_name = it) }, "Name", errorList, 6)

                        MyOutlinedTextField(customer_channel_id ?: "none", { orderInfo = orderInfo.copy(customer_channel_id = customer_channel_id) }, "Customer_Channel_id", errorList, 1, readOnly = true, trailingIcon = {
                                IconButton({
                                    subScreen = customerPicker
                                }) {
                                    Icon(Icons.Default.Menu, "Add Customer Info")
                                }
                            })

                        //Address
                        var _address = remember { mutableStateOf(customer_address) }
                        AddressField(_address, errorList, 3)
                        orderInfo = orderInfo.copy(customer_address = _address.value)

                        //Order Status
                        Row(verticalAlignment = Alignment.CenterVertically) { MyDropdownMenu(
                            OrderStatus.PENDING.name,listOf(OrderStatus.PENDING.name, OrderStatus.ACCEPTED.name, OrderStatus.PREPARING.name, OrderStatus.OUT_FOR_DELIVERY.name, OrderStatus.DELIVERED.name, OrderStatus.CANCELED.name), Modifier.width(300.dp)) {
                                orderInfo = orderInfo.copy(
                                    order_status = when (it) {
                                        OrderStatus.ACCEPTED.name -> OrderStatus.ACCEPTED
                                        OrderStatus.PREPARING.name -> OrderStatus.PREPARING
                                        OrderStatus.OUT_FOR_DELIVERY.name -> OrderStatus.OUT_FOR_DELIVERY
                                        OrderStatus.DELIVERED.name -> OrderStatus.DELIVERED
                                        OrderStatus.CANCELED.name -> OrderStatus.CANCELED
                                        else -> OrderStatus.PENDING
                                    }
                                )
                            };Icon(Icons.Default.KeyboardArrowDown, "Down") }

                        TextButton({
                            subScreen=restorentPicker
                        }){
                            Text("Pick Restorent")
                        }

                        OrderReciptScreen(CustomerInfo.initialCustomerInfo,restaurantInfo, listOf(
                            FoodInfo.initialFoodInfo.copy(id = 33), FoodInfo.initialFoodInfo.copy(id = 38), FoodInfo.initialFoodInfo.copy(id = 33), FoodInfo.initialFoodInfo.copy(id = 34), FoodInfo.initialFoodInfo.copy(id = 33), FoodInfo.initialFoodInfo.copy(id = 38)), orderInfo) { orderInfo = it }
                    }
            }


            AnimatedVisibility(subScreen == customerPicker) {
                Column(Modifier.clip(RoundedCornerShape(2)).padding(vertical = 5.dp).background(Color.White, RoundedCornerShape(2)).width(450.dp).fillMaxHeight().padding(vertical = 5.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text(order_id ?: "None")
                        IconButton({
                            subScreen=main
                        }){
                            Icon(Icons.Default.Close,"Close")
                        }

                    }
                    customer.list.forEach { customerinfo->
                        var show by remember {
                            mutableStateOf(false)
                        }
                        Column {
                            Row(Modifier.padding(10.dp).border(1.dp, Color.LightGray, RoundedCornerShape(10)).fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Text(customerinfo.name)
                                Spacer(Modifier.width(10.dp))
                                Text(customerinfo.phoneNumber.toString())
                                IconButton({
                                    show=!show
                                }, modifier = Modifier.size(25.dp)) {
                                    Icon(if(show) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "")
                                }
                            }
                            if(show){
                                customerinfo.address.forEach {
                                    TextButton({
                                        orderInfo=orderInfo.copy(customer_address = it, customer_name =customerinfo.name, customer_channel_id = customerinfo.channelId!!, role = Role.Admin, customer_phone_number =customerinfo.phoneNumber )
                                        subScreen=main
                                    }) {
                                        Text(it.toString(), modifier = Modifier.padding(10.dp))
                                    }
                                    Spacer(Modifier.height(15.dp))
                                }

                            }
                        }
                    }
                }
            }

        }

    }

}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderReciptScreen(customerInfo: CustomerInfo, restaurantInfo: RestaurantInfo?, foodList: List<FoodInfo>, orderInfo: OrderInfo, onOrderChanged:(OrderInfo)->Unit){
    val scope= rememberCoroutineScope()

    restaurantInfo?.let {
        var foodItemList = foodList.map { it to foodList.filter { it1 -> it1.id == it.id }.size }.toSet()

        //This list used to reduce repeativity in foodItemView
        var foodItemSynchronizedList by remember {
            mutableStateOf(foodItemList.map {
                OrderItem(
                    it.first.id.toString(),
                    it.first.name,
                    it.first.price,
                    it.second
                )
            })
        }

        var total by remember { mutableStateOf(0.0) }


        LaunchedEffect(key1 = true) {
            total = foodItemSynchronizedList.map { it.total() }.sum()
//        restaurantInfo=  supabase.from(Table.RestaurantInfo.name).select() {
//            filter {
//                RestaurantInfo::channel_id eq foodList[0].restaurantChannelId
//            }
//        }.decodeSingle()

        }

        var paymentMethod by remember {
            mutableStateOf(PaymentMethod.CASH_ON_DELIVERY)
        }
        var deliveryInstructions by remember {
            mutableStateOf("")
        }

        Scaffold(Modifier, topBar = {
            Row(Modifier.fillMaxWidth()) {
                //Platform Code
                // AsyncImage(customerInfo.profileUrl ?: "", "", Modifier.padding(15.dp).size(50.dp).clip(CircleShape))
                Text(
                    restaurantInfo.name,
                    Modifier.padding(20.dp).fillMaxWidth(0.75f),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
            bottomBar = {
                Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.End) {
                    Text("Total: $total", Modifier.padding(15.dp))
                    Spacer(Modifier.width(25.dp))
                    Button({
                        onOrderChanged(
                            orderInfo.copy(
                                order_items = foodItemSynchronizedList,
                                order_total = total,
                                order_status = OrderStatus.PENDING,
                                payment_method = paymentMethod,
                                delivery_instructions = deliveryInstructions
                            )
                        )
//                    scope.launch {
//                        supabase.from(Table.OrderInfo.name).insert(OrderInfo(null,customerInfo.channelId?:"",
//                            Role.Admin,"",
//                            Address("","",""),customerInfo.phoneNumber,foodItemSynchronizedList,total,OrderStatus.ACCEPTED,null,null, PaymentMethod.CASH_ON_DELIVERY,""))
//                    }

                    }, shape = MaterialTheme.shapes.small, modifier = Modifier.height(50.dp)) {
                        Box(Modifier.width(200.dp)) {
                            Text("Order", Modifier.width(100.dp).padding(5.dp))
                        }
                    }
                    Spacer(Modifier.width(25.dp))

                }
            }) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(100.dp))

                foodItemList.forEachIndexed { i, it ->
                    var foodItem = remember { mutableStateOf(it) }

                    foodItem.orderItemView(foodList, {
                        foodItemSynchronizedList =
                            foodItemSynchronizedList.mapIndexed { j, item -> if (i == j) item.copy(quantity = foodItem.value.second) else item }

                        total = foodItemSynchronizedList.map { it.total() }.sum()
                    })

                    Spacer(Modifier.height(10.dp))
                }

                //Select Payment Method
                val chips = remember {
                    mutableStateOf(
                        listOf<ChipData>(
                            ChipData(PaymentMethod.CASH_ON_DELIVERY.name, true),
                            ChipData(PaymentMethod.UPI.name)
                        )
                    )
                }
                ChipGroup(
                    Modifier,
                    chips,
                    {
                        if (it.label == PaymentMethod.CASH_ON_DELIVERY.name) paymentMethod =
                            PaymentMethod.CASH_ON_DELIVERY else paymentMethod = PaymentMethod.UPI
                    }) {
                    Text(
                        it.label,
                        color = if (it.isChecked) Color.Blue else Color.Unspecified,
                        modifier = Modifier.width(150.dp).padding(5.dp)
                    )
                }

                MyOutlinedTextField(
                    deliveryInstructions,
                    onValueChange = { s -> deliveryInstructions = s },
                    "Delivery Instructions",
                    ErrorInfo("", ""),
                    ErrorState(false, false)
                )

            }
        }
    }
}

@Composable
fun OrderReciptScreen(restaurantInfo: RestaurantInfo, orderInfo: OrderInfo, onOrderChanged:(OrderInfo)->Unit){
    val scope= rememberCoroutineScope()
    var foodList by remember {
        mutableStateOf<List<FoodInfo>?>(null)
    }
//    LaunchedEffect(Unit){
//        foodList= supabase.postgrest.from(Table.FoodInfo.name).select {
//            filter {
//                eq("restaurantChannelId",restaurantInfo.channel_id?:"")
//            }
//        }.decodeList()
//    }
//
//
//
//    foodList?.let {foodList->
//        var foodItemList = foodList.map { it to foodList.filter { it1 -> it1.id == it.id }.size }.toSet()
//
//        //This list used to reduce repeativity in foodItemView
//        var foodItemSynchronizedList by remember {
//            mutableStateOf(foodItemList.map {
//                OrderItem(
//                    it.first.id.toString(),
//                    it.first.name,
//                    it.first.price,
//                    it.second
//                )
//            })
//        }
//
//        var total by remember { mutableStateOf(0.0) }
//
//
//        LaunchedEffect(key1 = true) {
//            total = foodItemSynchronizedList.map { it.total() }.sum()
////        restaurantInfo=  supabase.from(Table.RestaurantInfo.name).select() {
////            filter {
////                RestaurantInfo::channel_id eq foodList[0].restaurantChannelId
////            }
////        }.decodeSingle()
//
//        }
//
//        var paymentMethod by remember {
//            mutableStateOf(PaymentMethod.CASH_ON_DELIVERY)
//        }
//        var deliveryInstructions by remember {
//            mutableStateOf("")
//        }
//
//        Scaffold(Modifier, topBar = {
//            Row(Modifier.fillMaxWidth()) {
//                //Platform Code
//                // AsyncImage(customerInfo.profileUrl ?: "", "", Modifier.padding(15.dp).size(50.dp).clip(CircleShape))
//                Text(
//                    restaurantInfo?.name ?: "",
//                    Modifier.padding(20.dp).fillMaxWidth(0.75f),
//                    fontSize = 20.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//        },
//            bottomBar = {
//                Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.End) {
//                    Text("Total: $total", Modifier.padding(15.dp))
//                    Spacer(Modifier.width(25.dp))
//                    Button({
//                        onOrderChanged(
//                            orderInfo.copy(
//                                order_items = foodItemSynchronizedList,
//                                order_total = total,
//                                order_status = OrderStatus.PENDING,
//                                payment_method = paymentMethod,
//                                delivery_instructions = deliveryInstructions
//                            )
//                        )
////                    scope.launch {
////                        supabase.from(Table.OrderInfo.name).insert(OrderInfo(null,customerInfo.channelId?:"",
////                            Role.Admin,"",
////                            Address("","",""),customerInfo.phoneNumber,foodItemSynchronizedList,total,OrderStatus.ACCEPTED,null,null, PaymentMethod.CASH_ON_DELIVERY,""))
////                    }
//
//                    }, shape = MaterialTheme.shapes.small, modifier = Modifier.height(50.dp)) {
//                        Box(Modifier.width(200.dp)) {
//                            Text("Order", Modifier.width(100.dp).padding(5.dp))
//                        }
//                    }
//                    Spacer(Modifier.width(25.dp))
//
//                }
//            }) {
//            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
//                Spacer(Modifier.height(100.dp))
//
//                foodItemList.forEachIndexed { i, it ->
//                    var foodItem = remember { mutableStateOf(it) }
//
//                    foodItem.orderItemView(foodList, {
//                        foodItemSynchronizedList =
//                            foodItemSynchronizedList.mapIndexed { j, item -> if (i == j) item.copy(quantity = foodItem.value.second) else item }
//
//                        total = foodItemSynchronizedList.map { it.total() }.sum()
//                    })
//
//                    Spacer(Modifier.height(10.dp))
//                }
//
//                //Select Payment Method
//                val chips = remember {
//                    mutableStateOf(
//                        listOf<ChipData>(
//                            ChipData(PaymentMethod.CASH_ON_DELIVERY.name, true),
//                            ChipData(PaymentMethod.UPI.name)
//                        )
//                    )
//                }
//                ChipGroup(
//                    Modifier,
//                    chips,
//                    {
//                        if (it.label == PaymentMethod.CASH_ON_DELIVERY.name) paymentMethod =
//                            PaymentMethod.CASH_ON_DELIVERY else paymentMethod = PaymentMethod.UPI
//                    }) {
//                    Text(
//                        it.label,
//                        color = if (it.isChecked) Color.Blue else Color.Unspecified,
//                        modifier = Modifier.width(150.dp).padding(5.dp)
//                    )
//                }
//
//                MyOutlinedTextField(
//                    deliveryInstructions,
//                    onValueChange = { s -> deliveryInstructions = s },
//                    "Delivery Instructions",
//                    ErrorInfo("", ""),
//                    ErrorState(false, false)
//                )
//
//            }
//        }
//    }
}




@Composable
fun OrderInfoScope(orderInfo: OrderInfo, scope:@Composable OrderInfo.()->Unit){
    scope(orderInfo)
}

@Serializable
data class OrderItem(val id:String,val name:String,val price:Double,val quantity:Int){
    fun total():Double{ return price*quantity }
}

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELED
}

enum class PaymentMethod {
    CASH_ON_DELIVERY,
  //  CARD_PAYMENT,
    UPI,
  //  DIGITAL_WALLET
}