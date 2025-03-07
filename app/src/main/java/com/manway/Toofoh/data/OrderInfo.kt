package com.manway.Toofoh.data

import Ui.data.*
import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.manway.Toofoh.R

import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.toffoh.admin.data.orderItemView
import com.manway.toffoh.admin.ui.MyOutlinedTextField
import data.enums.Role
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class  OrderInfo(
    val id: Int? = null,
    val customer_channel_id: String?,
    val restaurant_id: Int,
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
        val initialOrderInfo = OrderInfo(
            null, null, 0,
            Role.Customer, "", Address.intial,
            PhoneNumber("+91","000000000000"), listOf(),0.0,
            OrderStatus.PENDING,null,null,
            PaymentMethod.CASH_ON_DELIVERY,"demo" )
    }

    @Composable
    fun itemView(refIndex:Int,deleteAction:()->Unit){
            //Min Width 800dp
            Row(Modifier
                .padding(end = 50.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(15))
                .padding(15.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
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

}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderReceiptScreen(
    customerInfo: CustomerInfo,
    restaurantInfo: RestaurantInfo?,
    foodList: List<FoodInfo>,
    orderInfo: OrderInfo,
    onOrderChanged: (OrderInfo) -> Unit
) {
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
                    it.second,
                    FoodCategory.VEG,
                    0
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
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(0.75f),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
            bottomBar = {
                Row(Modifier
                    .fillMaxWidth()
                    .height(150.dp), horizontalArrangement = Arrangement.End) {
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
                            Text("Order", Modifier
                                .width(100.dp)
                                .padding(5.dp))
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
                        modifier = Modifier
                            .width(150.dp)
                            .padding(5.dp)
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
fun OrderReceiptScreen(
    restaurantInfo: RestaurantInfo,
    orderInfo: OrderInfo,
    onOrderChanged: (OrderInfo) -> Unit
) {
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

@Serializable
data class OrderItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val foodCategory: FoodCategory,
    val restaurantId: Int
) {
    fun total():Double{ return price*quantity }

    @Composable
    fun OrderItemDisplay(onItemClick:(OrderItem)->Unit){

        var orderCount by remember {
            mutableStateOf(quantity)
        }

        LaunchedEffect(key1 =orderCount){
            onItemClick(copy(quantity =orderCount ))
        }


            ConstraintLayout(Modifier.fillMaxWidth()) {
                val (vegIcon,_name,addOrder,cost) = createRefs()
                Icon(if(foodCategory==FoodCategory.VEG) painterResource(R.drawable.veg) else painterResource(R.drawable.nonveg), "Veg/Nom Veg", Modifier
                    .constrainAs(vegIcon) {
                        start.linkTo(parent.start, 10.dp)
                        top.linkTo(parent.top, 10.dp)
                    }
                    .size(40.dp),
                    tint = if (foodCategory == FoodCategory.VEG) Color(0xFF1B5E20) else Color.Red
                )
                Text(name,Modifier.constrainAs(_name){
                    start.linkTo(vegIcon.end,15.dp)
                    top.linkTo(parent.top,10.dp)
                })
                val color=MaterialTheme.colorScheme.primary

                Row(
                    Modifier
                        .constrainAs(addOrder) {
                            top.linkTo(
                                parent.top,
                                10.dp
                            );end.linkTo(parent.end, 10.dp)
                        }
                        .width(120.dp)
                        .height(35.dp)
                        .background(
                            if (orderCount == 0) color else Color.White,
                            MaterialTheme.shapes.small
                        )
                        .border(
                            1.dp,
                            if (orderCount != 0) color else Color.White,
                            MaterialTheme.shapes.small
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if(orderCount>0)     TextButton({ orderCount--;}) { Text("-") }
                    Text(if(orderCount==0) "Add" else orderCount.toString(), style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center), color =if(orderCount==0) Color.Unspecified else color , modifier = Modifier.clickable { if(orderCount==0){ orderCount=1; }})
                    if(orderCount!=0) TextButton({ orderCount++; }) { Text("+") }
                }
                Text("₹${total()}", Modifier.constrainAs(cost) {
                    end.linkTo(addOrder.end,10.dp)
                    top.linkTo(addOrder.bottom, 5.dp)
                }, style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.End))

            }

    }
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