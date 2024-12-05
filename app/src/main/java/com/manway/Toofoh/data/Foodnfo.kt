package com.manway.toffoh.admin.data

import Ui.enums.Availability
import Ui.data.ImageUrl
import android.annotation.SuppressLint
import android.widget.RatingBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.manway.Toofoh.R
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.getImage
import com.manway.Toofoh.dp.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class FoodInfo(
    val id: Int? = null,
    val created_at: LocalDateTime? = null,
    val updated_at: LocalDateTime? = null,
    val restaurantChannelId: String,//Foreign Key Action On Restaurant Table
    val imageUrl: ImageUrl? = null,
    val name: String,
    val price: Double,
    val dishCategory: String? = null,
    val foodCategory: FoodCategory,//Veg or Non Veg
    val foodType: List<String>, //Desert or Main Course,Dairy product
    val available_qty: Int = 0,
    val isAvailable: Availability,
    val rating: Double,
    val numberOfRatings: Int,
    val isBestSeller:Boolean=false,
    val description: String = "",
    val others: HashMap<String, String>? = hashMapOf(),
){


    companion object{
        val initialFoodInfo=FoodInfo(
            null, null, null, "",
            ImageUrl("", "", null), "", 100.00, "",
            FoodCategory.VEG, listOf(), 0, Availability.NotAvailable, 0.0, 0,
        )
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun FoodItemDisplay(customerInfo: CustomerInfo,orderItems:MutableState<List<OrderItem>>,onItemClick:(OrderItem)->Unit){
        val scope= rememberCoroutineScope()

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            Spacer(Modifier.height(20.dp))
            var orderItem by remember {
                mutableStateOf<OrderItem>(orderItems.value.find { it.id==id.toString() }?:OrderItem(id.toString(),name,price,0,foodCategory, restaurantChannelId))
            }

            var orderCount by remember {
                mutableStateOf(orderItem.quantity)
            }



            LaunchedEffect(key1 =orderCount){
              //  orderCount=orderItem.quantity
                onItemClick(orderItem.copy(quantity =orderCount ))
            }

            ConstraintLayout(Modifier.fillMaxWidth().padding(5.dp)) {
                val (vegIcon,bestSeller,_name,ratingBar,_numberOfRatings,cost,addCollection,addOrder,image) = createRefs()

                Icon(if(foodCategory==FoodCategory.VEG) painterResource(R.drawable.veg) else painterResource(R.drawable.nonveg), "Veg/Nom Veg", Modifier.constrainAs(vegIcon) {}.size(40.dp),tint = if(foodCategory==FoodCategory.VEG) Color(0xFF1B5E20) else Color(0xFF8B0000))
              if (isBestSeller)  Text("Bestseller", style =MaterialTheme.typography.bodySmall, modifier = Modifier
                  .constrainAs(bestSeller) {
                      start.linkTo(vegIcon.end, 5.dp)
                      top.linkTo(vegIcon.top)
                      bottom.linkTo(vegIcon.bottom)
                  }
                  .background(Color(0xFFFFD600), MaterialTheme.shapes.small)
                  .padding(3.dp))

                Text(name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.constrainAs(_name){ top.linkTo(vegIcon.bottom,10.dp);start.linkTo(parent.start,10.dp) })

                Text(price.toString(), style = MaterialTheme.typography.titleMedium, modifier = Modifier.constrainAs(cost){
                    top.linkTo(_name.bottom, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                })

                Row(
                    Modifier
                        .constrainAs(ratingBar) {
                            top.linkTo(cost.bottom, 10.dp)
                            start.linkTo(parent.start, 10.dp)
                        }
                        .width(100.dp)
                        .height(15.dp)
                        ) {
                            Spacer(Modifier.width(10.dp))
                    val decimal = rating - Math.floor(rating)
                            for (i in 1..Math.floor(rating).toInt()) {
                                Icon(
                                    Icons.Default.Star,
                                    "",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(10.dp)
                                )
                                if (i == Math.floor(rating).toInt() && decimal != 0.0
                                ) Icon(
                                    painterResource(R.drawable.half_star),
                                    "",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }




                var favorate by remember {
                    mutableStateOf<FavInfo?>(null)
                }

                var isFav by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(Unit) {
                    try {
                        favorate=
                            supabase.from(Table.FavInfo.name).select {
                                filter {
                                    eq("favId",Json.encodeToString(FavInfo.FoodFav(customerInfo.channelId?:"",Table.FoodInfo.name,id)))
                                }
                            }.decodeSingle()
                        isFav=favorate?.isFavorate?:false

                    }catch (e:Exception){

                    }
                }



                FilterChip(isFav,{
                    isFav=!isFav
                    favorate=favorate?.copy(isFavorate =isFav)
                    scope.launch {
                        FavInfo.upsertFood(customerInfo, this@FoodInfo,(favorate?.isFavorate?:false),favorate?.star?:0.0f)
                    }

                },{
                    Text("Add to Collections")
                }, modifier = Modifier.constrainAs(addCollection){
                    top.linkTo(ratingBar.bottom,10.dp)
                }, shape = MaterialTheme.shapes.small, border = BorderStroke(1.dp, Color.LightGray.copy(0.3f)))

                supabase.getImage(imageUrl, Modifier.clip(MaterialTheme.shapes.small)
                        .constrainAs(image) {
                            top.linkTo(parent.top, 10.dp)
                            end.linkTo(parent.end, 20.dp)
                        }
                        .size(125.dp), contentScale = ContentScale.FillBounds, enableBlackAndWhite = this@FoodInfo.isAvailable!=Availability.Available)

                //Add cart
                val color=MaterialTheme.colorScheme.primary


                if(this@FoodInfo.isAvailable==Availability.Available)      Row(Modifier.constrainAs(addOrder) { top.linkTo(image.bottom, 15.dp);end.linkTo(image.end);start.linkTo(image.start) }
                        .width(120.dp)
                        .height(40.dp)
                        .background(
                            if (orderCount == 0) color else Color.White,
                            MaterialTheme.shapes.small
                        )
                        .border(
                            1.dp,
                            if (orderCount != 0) color else Color.White,
                            MaterialTheme.shapes.small
                        ), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                if(orderCount>0)     TextButton({ orderCount--;}) { Text("-") }
                    Text(if(orderCount==0) "Add" else orderCount.toString(), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center), color =if(orderCount==0) Color.Unspecified else color , modifier = Modifier.clickable { if(orderCount==0){ orderCount=1; }})
                    if(orderCount!=0) TextButton({ orderCount++; }) { Text("+") }

                }



            }
            HorizontalDivider(
                Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp))

        }
    }


    
    
}




@Composable
fun MutableState<Pair<FoodInfo,Int>>.orderItemView(list:List<FoodInfo>,orderQuntityChanageListner:(Pair<FoodInfo,Int>)->Unit){
    Row(
        Modifier
            .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
            .fillMaxWidth(0.70f)
            .height(50.dp)
            .padding(5.dp),Arrangement.SpaceBetween,Alignment.CenterVertically){
        Text(value.first.name)
        Row(Modifier.width(100.dp),Arrangement.SpaceBetween) {

            IconButton(onClick = {
                if(value.second>1) value=value.copy(second = value.second-1)
               // else value=value.copy(second = 1)
                orderQuntityChanageListner(value)
            }){
               // Icon(Icons., contentDescription = "Reduce")
            }
            Text(value.second.toString())
            IconButton(onClick = {
                value=value.copy(second = value.second+1)
                orderQuntityChanageListner(value)
            }){
               // Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Add")
            }
        }

        Text(value.first.price.toString())
        Text((value.first.price*value.second).toString())
    }
}

@Composable
fun FoodInfoScope(foodInfo: FoodInfo,scope: @Composable FoodInfo.()->Unit){
    scope(foodInfo)
}