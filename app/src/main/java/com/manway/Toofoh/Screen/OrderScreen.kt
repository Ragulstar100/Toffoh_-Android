package com.manway.Toofoh.Screen

import android.annotation.SuppressLint
import android.widget.RatingBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.OrderInfo
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.data.OrderStatus
import com.manway.Toofoh.data.PaymentMethod
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.ui.toDate
import data.enums.Role
import io.github.jan.supabase.postgrest.from
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.ZoneId


@SuppressLint("NewApi")
@Composable
fun OrderScreen(customerInfo: CustomerInfo) {

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
        }

        orders.forEach {


            item {
                var openOrder by remember {
                    mutableStateOf(false)
                }
                Row( horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp).border(1.dp, MaterialTheme.colorScheme.primary,MaterialTheme.shapes.small).fillMaxWidth()) {
                    // Text(it.order_id.toString(), Modifier.width(200.dp))
                    Text(it.order_time?.toDate().toString(), style = MaterialTheme.typography.bodyMedium)
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

            order_id?.let {
                Text(
                    "Order Id:$it",
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
            Row(Modifier.fillMaxWidth().padding(8.dp)) {

            }

            //Address
            Text(
                customer_address.toString(),
                Modifier.fillMaxWidth().padding(15.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Column(Modifier.fillMaxWidth().background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(20.dp))
                order_items.forEachIndexed {i,it->
                    Row(Modifier.border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraSmall).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
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
                                    eq("order_id", order_id ?: "")
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
            }, label = { Text("Delivery Instructions(Editable)") }, colors =OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.medium, modifier = Modifier.scale(0.80f).fillMaxWidth())


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

@Composable
fun OrderReceiptScreen3(customerInfo: CustomerInfo, orderItems:List<OrderItem>){

    val scope= rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.90f)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(20.dp))
            orderItems.forEach {
                Row(
                    Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.extraSmall
                        )
                        .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(it.name, Modifier.width(150.dp))
                    Text(it.price.toString(), Modifier.width(50.dp), maxLines = 1)
                    Text(
                        "x${it.quantity}",
                        Modifier.width(35.dp),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                    Text((it.total().toString()), Modifier.width(60.dp), maxLines = 1)
                }
                Spacer(Modifier.height(10.dp))

            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text(orderItems.map { it.total() }.sum().toString(),modifier = Modifier.padding(end = 20.dp), style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Right ))
            Button(onClick = {
                scope.launch {

                    supabase.from("public","OrderInfo").insert(
                        OrderInfo(
                            customer_channel_id = customerInfo.channelId,
                            role = Role.Customer,
                            customer_name = customerInfo.name,
                            customer_address = customerInfo.address[0],
                            customer_phone_number = customerInfo.phoneNumber,
                            order_items = orderItems,
                            order_total = orderItems.map { it.total() }.sum(),
                            order_status = OrderStatus.ACCEPTED,
                            payment_method = PaymentMethod.CASH_ON_DELIVERY,
                        )
                    )
                }
            }){
                Text("Update Order")
            }
        }
    }
}

