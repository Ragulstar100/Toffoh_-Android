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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.dp.getImage
import com.manway.Toofoh.dp.supabase
import kotlinx.datetime.LocalDateTime

import kotlinx.serialization.Serializable

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
    fun FoodItemDisplay(orderItems:MutableState<List<OrderItem>>,onItemClick:(OrderItem)->Unit){
        Column(Modifier.fillMaxWidth().background(Color.Cyan), verticalArrangement = Arrangement.Center) {
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

                Text(name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.constrainAs(_name){ top.linkTo(vegIcon.bottom,10.dp);start.linkTo(parent.start,10.dp) })
                Row(
                    Modifier
                        .constrainAs(ratingBar) {
                            top.linkTo(_name.bottom, 10.dp)
                            start.linkTo(parent.start, 10.dp)
                        }
                        .width(100.dp)
                        .height(15.dp)
                        ) {
                    Text("Not Implemented")
                    StarRatingAndroidView(rating.toFloat(),Modifier.fillMaxSize()) {

                    }
                }
                Text("$numberOfRatings ratings",Modifier.constrainAs(_numberOfRatings){
                    top.linkTo(_name.bottom,5.dp)
                    start.linkTo(ratingBar.end,10.dp)
                }, style = MaterialTheme.typography.bodySmall)
                Text("₹$price", style =MaterialTheme.typography.titleSmall, modifier = Modifier.constrainAs(cost){
                    top.linkTo(ratingBar.bottom,10.dp)
                    start.linkTo(parent.start,10.dp)
                })
                AssistChip({},{
                    Text("Add to Collections")
                }, modifier = Modifier.constrainAs(addCollection){
                    top.linkTo(cost.bottom,10.dp)
                }, shape = MaterialTheme.shapes.small, border = BorderStroke(1.dp, Color.LightGray.copy(0.3f)))

                supabase.getImage(imageUrl,
                    Modifier
                        .clip(MaterialTheme.shapes.small)
                        .constrainAs(image) {
                            top.linkTo(parent.top, 10.dp)
                            end.linkTo(parent.end, 20.dp)
                        }
                        .size(125.dp), contentScale = ContentScale.FillBounds)

                //Add cart
                val color=MaterialTheme.colorScheme.primary


                Row(Modifier.constrainAs(addOrder) { top.linkTo(image.bottom, 15.dp);end.linkTo(image.end);start.linkTo(image.start)
                }
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
fun StarRatingAndroidView(
    rating: Float,
    modifier: Modifier,
    maxRating: Int = 5,
    onRatingChange: (Float) -> Unit
) {
//    AndroidView(
//        factory = { context ->
//            RatingBar(context).apply {
//                numStars = maxRating
//                stepSize = 0.1f
//                onRatingBarChangeListener =
//                    RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
//                        if (fromUser) {
//                            onRatingChange(rating)
//                        }
//                    }
//            }
//        },
//        modifier = modifier,
//        update = { view ->
//            view.rating = rating
//        }
//    )
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