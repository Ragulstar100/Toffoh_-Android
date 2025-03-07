package com.manway.Toofoh.Screen

import Ui.data.Address
import android.annotation.SuppressLint
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manway.Toofoh.ViewModel.OrderViewModel
import com.manway.Toofoh.ViewModel.SharedViewModel
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.OrderInfo
import com.manway.Toofoh.data.OrderStatus
import com.manway.Toofoh.data.PaymentMethod
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.data.rupee
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.toffoh.admin.ui.formatDate
import com.manway.toffoh.admin.ui.getOnlineTimeNow
import data.enums.Role
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons

import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import java.util.Locale


@SuppressLint("NewApi")
@Composable
fun OrderScreen(customerInfo: CustomerInfo,onDismissListener:()->Unit) {

    var orders by remember {
        mutableStateOf(listOf<OrderInfo>())
    }




    LaunchedEffect(Unit) {
        orders = supabase.from(Table.OrderInfo.name).select {
            filter {
                eq("customer_channel_id", customerInfo.channelId ?: "")
            }
          //  order("order_time",OrderDirection.Desc)
        }.decodeList()
    }
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Spacer(Modifier.height(50.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.End) {
                IconButton({
                    onDismissListener()
                    Log.e("bug","next")
                }) {
                    Icon(Icons.Default.Close, "")
                }
            }
        }

        orders.forEach {


            item {
                var openOrder by remember {
                    mutableStateOf(false)
                }
                Row( horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                    .fillMaxWidth()) {
                    // Text(it.order_id.toString(), Modifier.width(200.dp))
                    Text(it.order_time.toString(), style = MaterialTheme.typography.bodyMedium)
                    Text(it.order_status.toString(),style = MaterialTheme.typography.bodyMedium)
                    IconButton({
                        openOrder = !openOrder
                    }) {
                        Icon(if (openOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            "")
                    }
                }

               if(openOrder) OrderPage(customerInfo,it)

            }
        }
}

}

@SuppressLint("NewApi")
@Composable
fun OrderScreen(customerInfo: CustomerInfo,tab:MutableState<Int>) {

    var orders by remember {
        mutableStateOf(listOf<OrderInfo>())
    }




    LaunchedEffect(Unit) {
        orders = supabase.from(Table.OrderInfo.name).select {
            filter {
                eq("customer_channel_id", customerInfo.channelId ?: "")
            }
            //  order("order_time",OrderDirection.Desc)
        }.decodeList()
    }
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Spacer(Modifier.height(50.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.End) {
                IconButton({
                    tab.value=0
                }) {
                    Icon(Icons.Default.Close, "")
                }
            }
        }

        orders.sortedByDescending { it.order_time }.forEach {
            item {
                Box(Modifier
                    .fillMaxWidth()
                    .padding(10.dp), contentAlignment = Alignment.Center) {
                    HorizontalDivider(Modifier.fillMaxWidth(0.85f))
                    Text(
                        "none",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Color(0xFFF8F8F8))
                            .padding(horizontal = 10.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun OrderListScreen(sharedViewModel: SharedViewModel) {
    sharedViewModel.customerInfo?.let { cus ->
        val orders = viewModel<OrderViewModel>().feedCustomerInfo(cus)

        LaunchedEffect(orders.list) {

        }

        Column(Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            orders.list.sortedByDescending { it.order_time }.forEach {
                Spacer(Modifier.height(10.dp))
                Box(Modifier
                    .fillMaxWidth()
                    .padding(10.dp), contentAlignment = Alignment.Center) {
                    HorizontalDivider(Modifier.fillMaxWidth(0.85f))
                    Text(
                        it.order_time?.toInstant(UtcOffset(6, 30)).toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Color(0xFFF8F8F8))
                            .padding(horizontal = 10.dp)
                    )
                }
                it.OrderCard()

            }
            Spacer(Modifier.height(75.dp))
        }
    }

}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun OrderInfo.OrderCard() {
    val displayWhite = Color(0xFFFDFDFD)


    var restaurantInfo by remember {
        mutableStateOf<RestaurantInfo?>(null)
    }
    val scope = rememberCoroutineScope()

    scope.launch {
        restaurantInfo = supabase.from(Table.RestaurantInfo.name).select {
            filter {
                RestaurantInfo::id eq id
            }
        }.decodeSingle()
    }


    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        ConstraintLayout(Modifier
            .fillMaxWidth()
            .background(displayWhite)
            .padding(10.dp)) {
            val (_name, _locationPincode, _role, _dishes, _address, _total, _status, _deliveryTime, _rating) = createRefs()
            Text(restaurantInfo?.name ?: "", Modifier.constrainAs(_name) {
                start.linkTo(parent.start, 20.dp)
                top.linkTo(parent.top, 10.dp)
            }, style = MaterialTheme.typography.titleLarge)
            Row(Modifier
                .fillMaxWidth()
                .constrainAs(_locationPincode) {
                    start.linkTo(parent.start, 20.dp)
                    top.linkTo(_name.bottom, 10.dp)
                    end.linkTo(parent.end, 20.dp)
                }, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${customer_address.address.location} ${customer_address.pincode}",
                    Modifier
                        .weight(1.0f)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(Modifier.width(10.dp))
                TextButton({

                }, Modifier) {
                    Text("More")
                }
            }

            Text("( Updated By ${role.name} )", modifier = Modifier.constrainAs(_role) {
                top.linkTo(_locationPincode.bottom, 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, style = MaterialTheme.typography.bodySmall)

            Column(
                Modifier
                    .constrainAs(_dishes) {
                        top.linkTo(_role.bottom, 10.dp)
                    }
                    .fillMaxWidth()
                    .heightIn(max = 150.dp)
                    .padding(horizontal = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(20.dp))
                order_items.forEach {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            it.quantity.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right),
                            modifier = Modifier
                                .width(35.dp)
                                .padding(2.dp)
                        )
                        Text(
                            "x ${it.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth()
                                .padding(2.dp)
                        )
                        Spacer(Modifier.width(210.dp))
                        Text(
                            "${it.foodCategory}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .width(70.dp)
                                .padding(2.dp)
                        )
                        Spacer(Modifier.width(25.dp))
                        Text(
                            "${it.total()}",
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right),
                            modifier = Modifier
                                .width(100.dp)
                                .padding(2.dp)
                        )
                    }
                }
            }

            Text(
                "Total  ${order_items.map { it.total() }.sum()}",
                modifier = Modifier
                    .constrainAs(_total) {
                        top.linkTo(_dishes.bottom, 10.dp)
                    }
                    .fillMaxWidth()
                    .padding(10.dp),
                style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Right)
            )

            Text(
                "To  $customer_address",
                softWrap = false,
                modifier = Modifier
                    .constrainAs(_address) {
                        top.linkTo(_total.bottom, 10.dp)
                    }
                    .padding(10.dp)
            )
            Text(order_status.name.lowercase(Locale.getDefault()), Modifier.constrainAs(_status) {
                top.linkTo(_address.bottom, 10.dp)
                start.linkTo(parent.start, 25.dp)
            })

            if (delivery_time != null) Text(
                delivery_time.toString(),
                Modifier.constrainAs(_deliveryTime) {
                    start.linkTo(_status.end, 10.dp)
                    top.linkTo(_status.top)
                })
            else {
//                    OutlinedTextField(delivery_instructions?:"",{
//                        orderInfo=orderInfo.copy(delivery_instructions=it)
//                        scope.launch {
//                            supabase.postgrest.from(Table.OrderInfo.name).upsert(orderInfo)
//                        }
//                    }, label = { Text("Delivery Instructions") }, modifier = Modifier.constrainAs(_deliveryTime){
//                        start.linkTo(_status.start,10.dp)
//                        top.linkTo(_status.bottom,10.dp)
//                    }, shape = MaterialTheme.shapes.small)
            }
            if (order_status == OrderStatus.DELIVERED) {
//                                StarRating(Modifier.constrainAs(_rating){
//                                    start.linkTo(parent.start)
//                                    end.linkTo(parent.end)
//                                    top.linkTo(_status.bottom,10.dp)
//                                }) {
//
//                                }
            }


        }
    }

}



@SuppressLint("NewApi", "SuspiciousIndentation")
@Composable
fun OrderPage(customerInfo: CustomerInfo,orderInfo:OrderInfo){



    var order by remember {
        mutableStateOf(orderInfo)
    }

    val scope= rememberCoroutineScope()


    order.apply {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(Modifier.height(50.dp))

            id?.let {
                Text(
                    "Order Id:$id",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(5.dp)
                )
            }
            Text(
                "Updated By $role",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(3.dp)
            )


            //Address
            Row(Modifier
                .fillMaxWidth()
                .padding(8.dp)) {

            }

            //Address
            Text(
                customer_address.toString(),
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Column(Modifier
                .fillMaxWidth()
                .background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(20.dp))
                order_items.forEachIndexed {i,it->
                    Row(Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.extraSmall
                        )
                        .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(it.name, Modifier.width(150.dp),style = MaterialTheme.typography.bodySmall)
                        Text(it.price.toString(), Modifier.width(50.dp), maxLines = 1)
                     if(false)   Text("-", style = MaterialTheme.typography.titleMedium, modifier = Modifier.clickable { var list=order.order_items;list= list.mapIndexed { j, item-> var qty=item.quantity;if(i==j&&qty>0) item.copy(quantity =--qty ) else item };order=order.copy(order_items= list) })
                        Text("x${it.quantity}", Modifier.width(35.dp), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                      if(false)  Text("+", modifier = Modifier.clickable { var list=order.order_items;list= list.mapIndexed { j, item-> var qty=item.quantity;if(i==j) item.copy(quantity =++qty ) else item };order=order.copy(order_items= list) })
                        Text(it.total().toString(), Modifier.width(60.dp), maxLines = 1)

                    }
                    Spacer(Modifier.height(10.dp))

                }
            }

            Spacer(Modifier.height(10.dp))

            if (order_status == OrderStatus.DELIVERED) Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Hotel Rating", Modifier.width(100.dp))
                JetBackRatingBar(0.4f, Modifier.scale(0.60f)) {

                }
            }

            if (order_status == OrderStatus.DELIVERED) Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Employee Rating", Modifier.width(100.dp))
                JetBackRatingBar(0.4f, Modifier.scale(0.60f)) {

                }
            }

            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(0.75f)) {
                    Text("Status: ${order_status.name}", Modifier.padding(5.dp), style = MaterialTheme.typography.bodyMedium)
                    Text("OrderTime: ${order_time}", Modifier.padding(5.dp), style = MaterialTheme.typography.bodyMedium)
                    Text("DeliveryTime: ${order_time}", Modifier.padding(5.dp),style = MaterialTheme.typography.bodyMedium)
                }

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        order_items.map { it.total() }.sum().toString(),
                        modifier = Modifier.padding(end = 20.dp),
                        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Right)
                    )

                    Button(onClick = {
                        scope.launch {
                            supabase.from(Table.OrderInfo.name).update({
                                set("order_status", OrderStatus.CANCELED)
                            }) {
                                filter {
                                    eq("id", id ?: 0)
                                }
                            }
                        }
                    }, shape = MaterialTheme.shapes.small, modifier = Modifier.padding(8.dp)) {
                        Text("Cancel")
                    }
                }

            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(order.delivery_instructions.toString(), {
                order=order.copy(delivery_instructions=it)
            }, label = { Text("Delivery Instructions(Editable)") }, colors =OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.medium, modifier = Modifier
                .scale(0.80f)
                .fillMaxWidth())


            Spacer(Modifier.height(10.dp))

            if (payment_method == PaymentMethod.CASH_ON_DELIVERY) TextButton({

            }) {
                Text("Pay Now")
            }

            Spacer(Modifier.height(50.dp))


        }
    }

}

@Composable
fun JetBackRatingBar(ratingBar: Float,modifier: Modifier=Modifier,ratingBarChangeListener:(Float)->Unit){
    var rating by remember {
        mutableStateOf(ratingBar)
    }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(rating.toString(), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(5.dp))
        AndroidView({
            RatingBar(it).apply {
                this.stepSize = 0.5f
                this.rating=ratingBar
                this.setOnRatingBarChangeListener { ratingBar, fl, b ->
                    rating=fl
                    ratingBarChangeListener(fl)
                }
            }
        }) {}
    }
}


/**New Order Screen**/
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "NewApi")
@Composable
fun OrderScreen(sharedViewModel: SharedViewModel, onOrderPlacedListener: () -> Unit) {

    var orders by remember {
        mutableStateOf(sharedViewModel.orders)
    }

    var addressList by remember {
        mutableStateOf(sharedViewModel.customerInfo?.address)
    }
    var customerInfo by remember {
        mutableStateOf<CustomerInfo?>(sharedViewModel.customerInfo)
    }

    var address by remember {
        mutableStateOf<Address?>(null)
    }
    var paymentMethod by remember {
        mutableStateOf<PaymentMethod?>(null)
    }
    val (none, openAddressSheet, openPaymentSheet) = listOf(0, 1, 2)

    var openBottomSheet by remember {
        mutableStateOf(none)
    }


    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(true, { false })


    var onOrderActionCash = {
        val orderItemGroup = orders.groupBy { it.restaurantId }
        address?.let {
            scope.launch {
                orderItemGroup.forEach { (id, items) ->

                    try {
                        coroutineScope {
                            if (items.size > 0) supabase.from(Table.OrderInfo.name).insert(
                                OrderInfo(
                                    null,
                                    customerInfo?.channelId,
                                    id.toInt(),
                                    Role.Customer,
                                    customerInfo?.name ?: "none",
                                    it,
                                    it.address.phoneNumber,
                                    items.filter { it.quantity > 0 },
                                    items.filter { it.quantity > 0 }.map { it.total() }.sum(),
                                    OrderStatus.PENDING, null,
                                    null,
                                    PaymentMethod.CASH_ON_DELIVERY,
                                    "none"
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("Exception", "OrderScreen", e)
                    }

                }
            }
            sharedViewModel.orders = listOf()
            orders = listOf()
            onOrderPlacedListener()
        }
    }

    Column(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {

        if (address != null) Row(
            Modifier
                .padding(vertical = 60.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Close, "", Modifier.padding(10.dp))
            Column(
                Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Delivery To ${address?.address?.locationType}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    address.toString(),
                    softWrap = false,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )
            }
            IconButton({
                scope.launch {
                    sheetState.expand()
                }
                openBottomSheet = openAddressSheet
            }) {
                Icon(imageVector = Icons.Default.KeyboardArrowDown, "", Modifier.size(35.dp))
            }
        }
        else Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton({
                scope.launch {
                    sheetState.expand()
                }

                openBottomSheet = openAddressSheet

            }, shape = MaterialTheme.shapes.small) {
                Text(
                    "+ Add Your Address",
                    style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }
        }



        Column(Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.90f)
            .verticalScroll(rememberScrollState())) {
            var orderItemsGroup = orders.groupBy { it.restaurantId }



            orderItemsGroup.forEach { (id, items) ->
                var resName by remember {
                    mutableStateOf("")
                }

                scope.launch {
                    resName = supabase.from(Table.RestaurantInfo.name).select {
                        filter {
                            RestaurantInfo::id eq id.toInt()
                        }
                    }.decodeList<RestaurantInfo>().first().name
                }


                if (items.filter { it.quantity > 0 }.size > 0) Card(
                    elevation = CardDefaults.cardElevation(
                        1.dp
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .fillMaxWidth()
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            " $resName",
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        items.filter { it.quantity > 0 }.forEach { food ->
                            food.OrderItemDisplay { food1 ->
                                orders = sharedViewModel.orders.map {
                                    if (it.id == food1.id) food1 else it
                                }
                                sharedViewModel.orders = orders
                            }
                        }
                    }
                }
            }

            customerInfo?.let {
                if (openBottomSheet != none) {
                    ModalBottomSheet(
                        { openBottomSheet = none },
                        modifier = Modifier.fillMaxWidth(0.98f),
                        sheetState = sheetState
                    ) {
                        if (openBottomSheet == openAddressSheet) AddressScreen(sharedViewModel, {
                            scope.launch {
                                supabase.from(Table.CustomerInfo.name).update({
                                    set("address", it)
                                }) {
                                    filter {
                                        eq("id", customerInfo?.id ?: "")
                                    }
                                }
                                sharedViewModel.customerInfo =
                                    sharedViewModel.customerInfo?.copy(address = it)
                            }
                        }, {
                            openBottomSheet = none
                        }) {
                            address = it
                            openBottomSheet = none
                        }

                        if (openBottomSheet == openPaymentSheet) PaymentSheet(OrderInfo.initialOrderInfo) {
                            paymentMethod = it.payment_method
                            openBottomSheet = none
                        }

                    }

                }

            }

        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (paymentMethod) {
                PaymentMethod.CASH_ON_DELIVERY -> Text(
                    "Cash On Delivery",
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .clickable {
                            openBottomSheet = openPaymentSheet
                        })

                else -> TextButton({
                    openBottomSheet = openPaymentSheet
                }, shape = MaterialTheme.shapes.medium) {
                    Text(
                        "Choose Payment Method",
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        modifier = Modifier.fillMaxWidth(0.4f)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))

            Text(
                "$rupee${orders.map { it.total() }.sum()}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(0.4f)
            )

            Button(
                {
                    if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) {
                        onOrderActionCash()
                    }

                },
                shape = MaterialTheme.shapes.small,
                enabled = paymentMethod != null && orders.filter { it.quantity > 0 }
                    .isNotEmpty() && address != null
            ) {
                Text(
                    "Order",
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }
            Spacer(Modifier.width(10.dp))

        }
        Spacer(Modifier.height(50.dp))

    }


}

@Composable
fun PaymentSheet(_orderInfo: OrderInfo, orderInfoChange: (OrderInfo) -> Unit) {
    var orderInfo by remember {
        mutableStateOf(_orderInfo)
    }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(orderInfo.payment_method == PaymentMethod.CASH_ON_DELIVERY, {
                orderInfo = orderInfo.copy(payment_method = PaymentMethod.CASH_ON_DELIVERY)
            })
            Text("Cash On Delivery", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button({
                orderInfoChange(orderInfo)
            }, shape = MaterialTheme.shapes.small) {
                Text("Ok")
            }
        }
    }
}


//var openOrder by remember {
//    mutableStateOf(false)
//}
//Row( horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp).border(1.dp, MaterialTheme.colorScheme.primary,MaterialTheme.shapes.small).fillMaxWidth()) {
//    // Text(it.order_id.toString(), Modifier.width(200.dp))
//    Text(it.order_time.toString(), style = MaterialTheme.typography.bodyMedium)
//    Text(it.order_status.toString(),style = MaterialTheme.typography.bodyMedium)
//    IconButton({
//        openOrder = !openOrder
//    }) {
//        Icon(if (openOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//            "")
//    }
//}
//
//if(openOrder) OrderPage(customerInfo,it)
