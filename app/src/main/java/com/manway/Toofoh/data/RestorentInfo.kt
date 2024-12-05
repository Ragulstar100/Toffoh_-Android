package com.manway.toffoh.admin.data

import Ui.data.Address
import Ui.enums.Availability
import Ui.data.ImageUrl
import Ui.data.LocationType
import Ui.data.PhoneNumber
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.getImage
import com.manway.Toofoh.dp.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

import kotlinx.datetime.LocalDateTime

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RestaurantInfo private constructor(
    val id: Int?=null,
    val created_at: LocalDateTime?=null,
    val updated_at: LocalDateTime?=null,//filter
    val channel_id: String?=null,
    val owner_id: String,
    val fssaiNumber: String,
    val name: String,//filter
    val pureVeg:Boolean,
    val address: Address,//filter
    val phoneNumber: PhoneNumber,//
    val imageUrl: ImageUrl?=null,
    val cuisine: List<String>,//filter
    val upi_id: String,
    val minOrderAmount: Double,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,//filter
    val isAvailable: Availability,//filter
    val rating: Double,//filter
    val numberOfRatings: Int,
    val others: HashMap<String, String> =hashMapOf()
){


    companion object{
        val initialRestaurantInfo=RestaurantInfo(null,null,null,null,"","","",false, Address("","",""), PhoneNumber("",""),null, listOf(),"",100.00,15.00,45, Availability.NotAvailable,0.0,0)
        val bucketName="restorentImages"
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun HotelItemDisplaySmall( onClick:(RestaurantInfo)->Unit){

        Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.clickable {
            if(isAvailable==Availability.Available)   onClick(this@RestaurantInfo)
        }.padding(5.dp)) {
            var randomFood by remember {
                mutableStateOf<List<FoodInfo>?>(null)
            }
            val scope = rememberCoroutineScope()
            val randam=randomFood?.map { it.imageUrl }?.random()


            scope.launch {
                try {
                    randomFood = supabase.from(Table.FoodInfo.name).select() {
                        filter {
                            eq("restaurantChannelId", channel_id ?: "")
                        }
                    }.decodeList()
                }catch (e:Exception){

                }
            }

            ConstraintLayout(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .width(180.dp)
                    .height(270.dp)
                    .background(Color.White)
                    .padding(7.dp)
            ) {
                val (image, nameText) = createRefs()

                supabase.getImage(
                    randam,
                    Modifier
                        .constrainAs(image) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .clip(MaterialTheme.shapes.extraSmall)
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable {
                            if(isAvailable==Availability.Available)      onClick(this@RestaurantInfo)
                            // pickFood(it.name)
                        },
                    contentScale = ContentScale.FillBounds
                    ,  isAvailable!=Availability.Available
                )
                Column(Modifier.fillMaxWidth().constrainAs(nameText){
                    top.linkTo(image.bottom,5.dp)
                }) {
                    Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (name.length > 23) name.substring(0, name.length - 1)
                                .plus("...") else name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(0.65f).height(50.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "$rating ★",
                            Modifier
                               .height(25.dp)
                                .background(
                                    color = Color(0xFF00897B),
                                    shape = MaterialTheme.shapes.extraSmall
                                )
                                .padding(2.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Row(Modifier.fillMaxWidth().height(50.dp).padding(5.dp), verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            "$estimatedDeliveryTime mins",
                            Modifier.fillMaxHeight(),
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            "Starts at $minOrderAmount",
                            Modifier.fillMaxWidth().fillMaxHeight(),
                            style = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Right)
                        )
                    }
                }



            }
        }
    }

    @SuppressLint("UnusedBoxWithConstraintsScope", "CoroutineCreationDuringComposition")
    @Composable
    fun HotelItemDisplay(customerInfo: CustomerInfo,onClick:(RestaurantInfo)->Unit){

        var foods by remember {
            mutableStateOf<List<FoodInfo>?>(null)
        }



        val scope = rememberCoroutineScope()

        scope.launch {
            try {
                foods = supabase.from(Table.FoodInfo.name).select() {
                    filter {
                        eq("restaurantChannelId", channel_id ?: "")
                    }
                }.decodeList()
            }catch (e:Exception){

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
                            eq("favId", Json.encodeToString(FavInfo.ResFav(customerInfo.channelId?:"",Table.RestaurantInfo.name,channel_id)))
                        }
                    }.decodeSingle()
                isFav=favorate?.isFavorate?:false

            }catch (e:Exception){

            }
        }



        Column(Modifier) {
            Spacer(Modifier.height(25.dp))
            Card(Modifier.clickable {
                if(isAvailable==Availability.Available) onClick(this@RestaurantInfo)
            }) {
                ConstraintLayout(Modifier.padding(10.dp).clip(MaterialTheme.shapes.large).background(Color.Yellow).fillMaxWidth().height(330.dp)) {
                    var (_favorate, textContainer, hide, hoteldistance,price) = createRefs()
                    HorizontalPager(rememberPagerState { foods?.size?:0 }) {
                        supabase.getImage(foods?.get(it)?.imageUrl,  Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.FillBounds,isAvailable!=Availability.Available)
                    }
                    /**Gps**/
                    Text("1.5 km", Modifier.zIndex(3F).padding(1.dp).constrainAs(hoteldistance) { bottom.linkTo(textContainer.top, -10.dp);end.linkTo(parent.end,-5.dp) }.background(Color.White, MaterialTheme.shapes.small).padding(5.dp))
//                Text("For ${price[it]}",
//                    Modifier
//                        .zIndex(3F).padding(1.dp)
//                        .constrainAs(price) {
//                            bottom.linkTo(textContainer.top, -10.dp)
//                           start.linkTo(parent.start,-5.dp)
//                        }
//                        .padding(5.dp), style = MaterialTheme.typography.displayMedium)
                    IconButton({
                        isFav=!isFav
                        favorate=favorate?.copy(isFavorate = isFav)
                        scope.launch {
                            FavInfo.upsertRestaurant(customerInfo, this@RestaurantInfo,(favorate?.isFavorate?:false),favorate?.star?:0.0f)
                        }
                    }) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            "Favorites",
                            Modifier.constrainAs(_favorate) {
                                top.linkTo(
                                    parent.top,
                                    10.dp
                                );end.linkTo(parent.end, 10.dp)
                            }.size(30.dp),
                            tint = if(isFav) Color.Red else Color.White
                        )
                    }
//                Icon(Icons.Outlined.FavoriteBorder, "Hide",
//                    Modifier
//                        .constrainAs(hide) {
//                            top.linkTo(parent.top, 10.dp)
//                            end.linkTo(parent.end, 10.dp)
//                        }
//                        .size(30.dp), tint = Color.White)
                    Column(Modifier.constrainAs(textContainer) { bottom.linkTo(parent.bottom, 0.dp) }.background(Color.White).fillMaxWidth().height(130.dp)) {
                        Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(name, maxLines = 2, modifier = Modifier.fillMaxWidth(0.80f).height(50.dp).padding(start =10.dp), style = MaterialTheme.typography.headlineMedium)
                            Text("$rating ★", Modifier.padding(5.dp).background(color = Color(0xFF00897B), MaterialTheme.shapes.extraSmall).padding(horizontal = 3.dp, vertical = 2.dp), color = Color.White, style = MaterialTheme.typography.titleMedium)

                        }
                        Row(Modifier.padding( start =10.dp)) {

                                Text(cuisine.subList(0,2).joinToString("."))
                        }
                        Text("$estimatedDeliveryTime mins",Modifier.padding(10.dp), style = MaterialTheme.typography.titleMedium)

                    }
                }
            }

        }

    }




        }

@Composable
fun RestaurantInfoScope(RestaurantInfo: RestaurantInfo,scope: @Composable RestaurantInfo.()->Unit){
    scope(RestaurantInfo)
}

