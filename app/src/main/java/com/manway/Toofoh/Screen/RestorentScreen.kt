package com.manway.Toofoh.Screen

import Ui.enums.Availability
import android.annotation.SuppressLint
import android.util.Log
import android.widget.RatingBar
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.manway.Toofoh.R
import com.manway.Toofoh.ViewModel.RestaurantViewModel
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.data.OrderInfo
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.data.OrderReciptScreen
import com.manway.Toofoh.data.OrderStatus
import com.manway.Toofoh.data.PaymentMethod
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.getImage
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.FavInfo
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.toffoh.admin.data.ServiceArea
import data.enums.Role
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.reflect.Array.set

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun RestaurantScreen(customerInfo: CustomerInfo, navgation: NavController, settingsScreen:MutableState<Int>, orderItems: MutableState<List<OrderItem>>, restaurantInfo: RestaurantInfo, onbackpressed: () -> Unit){

    var foodList by remember {
        mutableStateOf(listOf<FoodInfo>())
    }

    var (noneTab,billTab,filter)=listOf(0,1,2)
    var openBottomTab by remember {
        mutableStateOf(noneTab)
    }

    LaunchedEffect(key1 = Unit){
        var _foodList= flow<List<FoodInfo>> {
            while (true) {
                emit(supabase.from(Table.FoodInfo.name).select {
                    filter {
                        eq("restaurantChannelId", restaurantInfo.channel_id ?: "")
                    }
                }.decodeList())
                delay(250)
            }
        }
        _foodList.collect{
            foodList=it
        }

    }

    val onMorePicked:()->Unit={}
    val share:()->Unit={}
    val onTimePicked:()->Unit={}


    Scaffold (
        Modifier.fillMaxWidth()
        , bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White)
                    .padding(10.dp)) {
                Text("Added Items ${orderItems.value.map { it.quantity }.sum()}",Modifier.fillMaxWidth(0.55f))
                Button({
                    openBottomTab=billTab
                }, shape = MaterialTheme.shapes.medium, modifier =Modifier.fillMaxWidth()){
                    Text("Cost ${orderItems.value.map { it.total() }.sum()}")
                }

            }
        }

    ) {

        var scrollState = rememberLazyListState()

        var scope= rememberCoroutineScope()

        val (none,priceHighToLow,priceLowToHigh,ratingHighToLow)= listOf(0 to "Name",1 to "Price HighToLow",2 to "Price LowToHigh",3 to "Rating HighToLow")

        var sort  by remember {
            mutableStateOf(none)
        }

        var selectedFilter by remember { mutableStateOf<FoodCategory?>(null) }

        var vegOn by remember {
            mutableStateOf(false)
        }

        var nonVegOn by remember {
            mutableStateOf(false)
        }

        var isBestSeller by remember {
            mutableStateOf(false)
        }

        var above3Star by remember {
            mutableStateOf(false)
        }

        LazyColumn (Modifier.fillMaxSize(),state = scrollState) {



            item {
                HotelMainDisplay(
                    customerInfo,
                    restaurantInfo,
                    onbackpressed,
                    onMorePicked,
                    share,
                    onTimePicked
                )
                Text(restaurantInfo.id.toString())
            }

            if(scrollState.firstVisibleItemIndex>0)  stickyHeader {
                Row(Modifier.fillMaxWidth().background(Color.White).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Back",
                        Modifier
                            .size(40.dp)
                            .clickable {
                                onbackpressed()
                            })

                    Text(restaurantInfo.name, Modifier.fillMaxWidth().padding(10.dp), style = MaterialTheme.typography.titleLarge)
                }
            }

            item {


                Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) { // Filter buttons

                    var openSort by remember { mutableStateOf(false) }


                    Row(modifier = Modifier.border(1.dp, Color.LightGray.copy(0.0f), MaterialTheme.shapes.small).padding(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { openSort = !openSort },shape = MaterialTheme.shapes.small,modifier = Modifier.padding(5.dp)) {
                            Text("Sort", style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.width(10.dp))

                        if (!openSort) {
                            Text(sort.second, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.width(10.dp))
                        }

                        AnimatedVisibility(visible = openSort, enter = expandHorizontally(), exit = shrinkHorizontally()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FilterChip(
                                    selected = sort == none,
                                    onClick = {
                                        sort = none
                                        openSort = false
                                    },
                                    label = { Text("Name", style = MaterialTheme.typography.bodyMedium) },
                                    leadingIcon = {
                                        if (sort == none) Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                )

                                FilterChip(
                                    selected = sort == priceHighToLow,
                                    onClick = {
                                        sort = priceHighToLow
                                        openSort = false
                                    },
                                    label = { Text(priceHighToLow.second, style = MaterialTheme.typography.bodyMedium) },
                                    leadingIcon = {
                                        if (sort == priceHighToLow) Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                )

                                FilterChip(
                                    selected = sort == priceLowToHigh,
                                    onClick = {
                                        sort = priceLowToHigh
                                        openSort = false
                                    },
                                    label = { Text(priceLowToHigh.second, style = MaterialTheme.typography.bodyMedium) },
                                    leadingIcon = {
                                        if (sort == priceLowToHigh) Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                )

                                FilterChip(
                                    selected = sort == ratingHighToLow,
                                    onClick = {
                                        sort = ratingHighToLow
                                        openSort = false
                                    },
                                    label = { Text(ratingHighToLow.second, style = MaterialTheme.typography.bodyMedium) },
                                    leadingIcon = {
                                        if (sort == ratingHighToLow) Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                )

                                Spacer(Modifier.width(10.dp))




                            }
                        }
                    }



                    Button(onClick = { openBottomTab=filter },shape = MaterialTheme.shapes.small,modifier = Modifier.padding(5.dp)) {
                        Text("Filter", style = MaterialTheme.typography.bodyMedium)
                    }

                    AssistChip({
                        vegOn=!vegOn
                    }, { Text("Veg",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                        if (vegOn) Icon(Icons.Default.Check, "")
                    }, shape = MaterialTheme.shapes.small)

                    AssistChip({
                        nonVegOn=!nonVegOn
                    }, { Text("Non Veg",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                        if (nonVegOn) Icon(Icons.Default.Check, "")
                    },shape = MaterialTheme.shapes.small)

                    AssistChip({
                        isBestSeller=!isBestSeller
                    }, { Text("Best Seller",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                        if (isBestSeller) Icon(Icons.Default.Check, "")
                    },shape = MaterialTheme.shapes.small)

                        AssistChip({
                            above3Star=!above3Star
                        }, { Text("Above 3 Star",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                            if (above3Star) Icon(Icons.Default.Check, "")
                        },shape = MaterialTheme.shapes.small)






                }



              when(sort){
                    priceHighToLow->foodList.sortedByDescending { it.price }
                    priceLowToHigh->foodList.sortedBy { it.price }
                    ratingHighToLow->foodList.sortedByDescending { it.rating }
                    else->foodList.sortedBy { it.name }
                }.filter { arrayListOf<Boolean>().apply {
                  if(vegOn)  add(it.foodCategory==FoodCategory.VEG)
                   if(nonVegOn) add(it.foodCategory==FoodCategory.NON_VEG)
                    if(size==0) add(true)
                }.contains(true)&&(!isBestSeller||it.isBestSeller)&&(!above3Star||it.rating>=3) }.forEach {
                    it.FoodItemDisplay(customerInfo,orderItems) {
                        if (!orderItems.value.map { it.id }.contains(it.id)) orderItems.value =
                            orderItems.value.plus(it)
                        else orderItems.value = orderItems.value.map { item ->
                            if (item.id == it.id) item.copy(quantity = it.quantity) else item
                        }
                    }
                }


                val sheetState= rememberModalBottomSheetState(confirmValueChange = {false}, skipPartiallyExpanded = true)
                LaunchedEffect(openBottomTab) {
                    if(openBottomTab==noneTab) sheetState.expand() else sheetState.expand()
                }

                if (openBottomTab!=noneTab) ModalBottomSheet({
                    openBottomTab=noneTab
                }, sheetState = sheetState ) {

                   if(openBottomTab==billTab) Column(Modifier.fillMaxSize()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                         //   Text("Address")
                            IconButton({
                               openBottomTab =noneTab

                            }) {
                                Icon(Icons.Default.Clear, "Close Bottom Sheet")
                            }
                        }

                        var orderItemsGroup = orderItems.value.groupBy { it.restaurantChannelId }

                        orderItemsGroup.forEach { (id, items) ->
                            var resName by remember {
                                mutableStateOf("")
                            }
                            scope.launch {
                                resName = supabase.from(Table.RestaurantInfo.name).select {
                                    filter {
                                        eq("channel_id", id)
                                    }
                                }.decodeList<RestaurantInfo>().first().name
                            }


                            if (items.filter { it.quantity > 0 }.size > 0) Text(
                                "▶ " + resName,
                                Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            items.filter { it.quantity > 0 }.forEach { food ->
                                food.OrderItemDisplay() { food1 ->
                                    Log.e("OrderItems", orderItems.value.toString())
                                    orderItems.value = orderItems.value.map {
                                        if (it.id == food1.id) food1 else it
                                    }
                                }
                            }
                        }



                       Spacer(Modifier.height(10.dp))
                       if(orderItems.value.filter { it.quantity>0 }.map { it.total() }.size>0)
                           Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.End,verticalAlignment = Alignment.CenterVertically) {
                               Text(orderItems.value.map { it.total() }.sum().toString(), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right), modifier = Modifier.padding(25.dp))
                               Button({ scope.launch {
                                   val orderItemGroup=orderItems.value.groupBy { it.restaurantChannelId }
                                   orderItemGroup.forEach { (id, items) ->
                                   supabase.from(Table.OrderInfo.name).insert(OrderInfo(null, customerInfo.channelId, Role.Customer, customerInfo.name, customerInfo.address[0], customerInfo.phoneNumber, items.filter { it.quantity>0 }, items.filter { it.quantity>0 }.map { it.total() }.sum(), OrderStatus.PENDING, null, null, PaymentMethod.CASH_ON_DELIVERY, "none"))
                                   }
                                   orderItems.value= listOf()
                                   sheetState.hide()
                                   settingsScreen.value=1
                                   navgation.navigate("Settings")
                               }}) {

                                   Text("Place Order(Cash On Delivery)", style = MaterialTheme.typography.bodySmall)
                               }
                           }
                        }



                   if(openBottomTab==filter) Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton({ openBottomTab =noneTab; }) { Icon(Icons.Default.Clear, "Close Bottom Sheet") }
                        }
                        Text("Sort")
                        Row (horizontalArrangement  = Arrangement.spacedBy(10.dp),modifier = Modifier.horizontalScroll(rememberScrollState()).padding(15.dp).border(1.dp, Color.LightGray,MaterialTheme.shapes.small).fillMaxWidth().padding(10.dp)) {
                            FilterChip(
                                selected = sort == none,
                                onClick = {
                                    sort = none
                                },
                                label = { Text("Name", style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    if (sort == none) Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )

                            FilterChip(
                                selected = sort == priceHighToLow,
                                onClick = {
                                    sort = priceHighToLow
                                },
                                label = { Text(priceHighToLow.second, style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    if (sort == priceHighToLow) Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )

                            FilterChip(
                                selected = sort == priceLowToHigh,
                                onClick = {
                                    sort = priceLowToHigh
                                },
                                label = { Text(priceLowToHigh.second, style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    if (sort == priceLowToHigh) Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )

                            FilterChip(
                                selected = sort == ratingHighToLow,
                                onClick = {
                                    sort = ratingHighToLow
                                },
                                label = { Text(ratingHighToLow.second, style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    if (sort == ratingHighToLow) Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )

                            Spacer(Modifier.width(10.dp))




                        }
                        Text("Filter")
                        Row (horizontalArrangement  = Arrangement.spacedBy(10.dp),modifier = Modifier.horizontalScroll(rememberScrollState()).padding(15.dp).border(1.dp, Color.LightGray,MaterialTheme.shapes.small).fillMaxWidth().padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
                            Text("Food Catagery")
                            AssistChip({
                                vegOn=!vegOn
                            }, { Text("Veg",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                                if (vegOn) Icon(Icons.Default.Check, "")
                            }, shape = MaterialTheme.shapes.small)

                            AssistChip({
                                nonVegOn=!nonVegOn
                            }, { Text("Non Veg",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                                if (nonVegOn) Icon(Icons.Default.Check, "")
                            },shape = MaterialTheme.shapes.small)
                        }

                       Row (horizontalArrangement  = Arrangement.spacedBy(10.dp),modifier = Modifier.horizontalScroll(rememberScrollState()).padding(15.dp).border(1.dp, Color.LightGray,MaterialTheme.shapes.small).fillMaxWidth().padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
                           Text("Best Seller")
                           AssistChip({
                               isBestSeller=!isBestSeller
                           }, { Text("Best Seller",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                               if (isBestSeller) Icon(Icons.Default.Check, "")
                           },shape = MaterialTheme.shapes.small)
                       }

                       Row (horizontalArrangement  = Arrangement.spacedBy(10.dp),modifier = Modifier.horizontalScroll(rememberScrollState()).padding(15.dp).border(1.dp, Color.LightGray,MaterialTheme.shapes.small).fillMaxWidth().padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
                           Text("Star")
                           AssistChip({
                              above3Star=!above3Star
                           }, { Text("Above 3 Star",style = MaterialTheme.typography.bodyMedium) }, trailingIcon = {
                               if (above3Star) Icon(Icons.Default.Check, "")
                           },shape = MaterialTheme.shapes.small)
                       }

                    }

                }


            }

            item {
                Spacer(Modifier.height(100.dp))
            }
        }


    }

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HotelMainDisplay(customerInfo: CustomerInfo,restaurantInfo: RestaurantInfo,onbackpressed: ()->Unit,onMorePicked:()->Unit,share:()->Unit,onTimePicked:()->Unit){


    ConstraintLayout(Modifier.fillMaxWidth()) {
        val (back, favourite, share, more, isPureVeg, _name, info, _rating, ratingCount, timeing, hoteldistance) = createRefs()
        Icon(Icons.Default.KeyboardArrowLeft, "Back",
            Modifier
                .size(40.dp)
                .constrainAs(back) {
                    top.linkTo(parent.top, 40.dp)
                    start.linkTo(parent.start, 10.dp)
                }
                .clickable {
                    onbackpressed()
                })



//        Icon(Icons.Default.MoreVert, "More",
//            Modifier
//                .size(35.dp)
//                .constrainAs(more) {
//                    top.linkTo(parent.top, 45.dp)
//                    end.linkTo(parent.end, 10.dp)
//                }
//                .clickable {
//                    onMorePicked()
//                })

        var favorate by remember {
            mutableStateOf<FavInfo?>(null)
        }
        var scope= rememberCoroutineScope()

        var isFav by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(Unit) {
            try {
                favorate=
                    supabase.from(Table.FavInfo.name).select {
                        filter {
                            eq("favId", Json.encodeToString(FavInfo.ResFav(customerInfo.channelId?:"",Table.RestaurantInfo.name,restaurantInfo.channel_id)))
                        }
                    }.decodeSingle()
                isFav=favorate?.isFavorate?:false

            }catch (e:Exception){

            }
        }



        Icon(Icons.Default.FavoriteBorder, "Favorite", Modifier.size(45.dp)
            .constrainAs(favourite) {
                top.linkTo(parent.top, 40.dp)
                // end.linkTo(more.start, 10.dp)
                end.linkTo(parent.end, 10.dp)
            }
            .padding(10.dp)
            .clickable {
                isFav=!isFav
                favorate=favorate?.copy(isFavorate = isFav)
                scope.launch {
                    FavInfo.upsertRestaurant(customerInfo, restaurantInfo,(favorate?.isFavorate?:false),favorate?.star?:0.0f)
                }
            }, tint = if(isFav) Color.Red else Color.DarkGray)

//        Icon(Icons.Default.Share, "Share",
//            Modifier
//                .size(45.dp)
//                .constrainAs(share) {
//                    top.linkTo(parent.top, 40.dp)
//                    end.linkTo(favourite.start, 10.dp)
//                }
//                .padding(10.dp)
//                .clickable {
//                    share()
//                })

        if (restaurantInfo.pureVeg) Image(painterResource(R.drawable.pure_veg), "Pure Veg",
            Modifier.constrainAs(isPureVeg) { top.linkTo(back.bottom, 0.dp);start.linkTo(parent.start, 5.dp) }.size(75.dp).padding(10.dp))

        Text(restaurantInfo.name, modifier = Modifier.fillMaxWidth(0.70f).constrainAs(_name) { if(!restaurantInfo.pureVeg) top.linkTo(parent.top,85.dp) else top.linkTo(isPureVeg.bottom, 5.dp);start.linkTo(back.end, 10.dp);end.linkTo(info.start, 10.dp) }.padding(end = 5.dp), maxLines = 3, style = MaterialTheme.typography.titleLarge)
        Icon(Icons.Outlined.Info, "Info", Modifier.constrainAs(info) { top.linkTo(_name.top);bottom.linkTo(_name.bottom);end.linkTo(_rating.start, 5.dp) }.size(45.dp).padding(10.dp))
        Text("${restaurantInfo.rating} ★", Modifier.constrainAs(_rating) { top.linkTo(info.top);end.linkTo(parent.end, 10.dp) }.padding(5.dp).background(color = Color(0xFF00897B), MaterialTheme.shapes.extraSmall).padding(horizontal = 5.dp, vertical = 6.dp), color = Color.White, style = MaterialTheme.typography.titleSmall)
        Text("${restaurantInfo.numberOfRatings} ratings", style = MaterialTheme.typography.bodySmall, textDecoration = TextDecoration.Underline, modifier = Modifier.constrainAs(ratingCount) { top.linkTo(_rating.bottom, 5.dp);end.linkTo(_rating.end);start.linkTo(_rating.start) })


        /**GPS**/
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .constrainAs(hoteldistance) {
                    top.linkTo(_name.bottom, 5.dp)
                    start.linkTo(back.start, 5.dp)
                }) {
            Icon(Icons.Default.CheckCircle,"Timer Icon")
            Text("${restaurantInfo.estimatedDeliveryTime} Min")
            Icon(Icons.Outlined.LocationOn, "Distance")
            Text("13.5Km", style =MaterialTheme.typography.bodyMedium )
            Text(".Saravanampatty", style =MaterialTheme.typography.bodyMedium )
        }

        Row(Modifier
            .padding(8.dp)
            .constrainAs(timeing) {
                top.linkTo(hoteldistance.bottom, 5.dp)
                start.linkTo(back.start, 10.dp)
            }
            .clickable {
                onTimePicked()
            }) {
            Icon(Icons.Outlined.DateRange, "Open Time range")
            Spacer(Modifier.width(10.dp))
            Text("Today. 10.30 PM -11 AM", style =MaterialTheme.typography.bodyMedium)
        }

    }

}



@Composable
fun FoodInfo.FoodItem(restaurantInfo: RestaurantInfo,orderCountChange: (OrderItem) -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 0.dp)
            .fillMaxSize()
            .background(color = Color.White, shape = MaterialTheme.shapes.extraSmall)) {
//            val foodInfo:FoodInfo
//            val name="Sample Food"
//            val price=45.00
//            val dishCategory:String?="Curry"
//            val foodCategory=FoodCategory.VEG
//            val rating=3.5f
//            val numberOfRatings=1666
//            val description=""
        var orderCount by remember {
            mutableStateOf(0)
        }

        LaunchedEffect(key1 = this@FoodItem) {
            orderCount = Math.min(orderCount, available_qty)
            orderCountChange(
                OrderItem(
                    id.toString(),
                    name,
                    price,
                    Math.min(orderCount, available_qty),
                    foodCategory,
                    restaurantInfo.channel_id?:""
                )
            )

        }

        LaunchedEffect(key1 = orderCount) {
            orderCountChange(OrderItem(id.toString(), name, price, orderCount, foodCategory, restaurantInfo.channel_id?:""))
        }

        BoxWithConstraints(
            Modifier
                .padding(3.dp)
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White, MaterialTheme.shapes.small)
        ) {

            supabase.getImage(imageUrl,
                Modifier
                    .absoluteOffset(maxWidth.minus(160.dp), 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .size(140.dp), ContentScale.FillBounds)

//                Image(painter = painterResource(R.drawable.main_screen),"", modifier = Modifier
//                 .absoluteOffset(maxWidth.minus(160.dp), 10.dp)
//                    .clip(MaterialTheme.shapes.medium)
//                    .size(140.dp), contentScale = ContentScale.FillBounds)

            Text(
                name,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.absoluteOffset(x = 20.dp, y = 10.dp)
            )
            Text(
                price.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.absoluteOffset(x = 20.dp, y = 40.dp)
            )
            Text(
                foodType.subList(0, 2).joinToString(","),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                modifier = Modifier
                    .width(225.dp)
                    .absoluteOffset(x = 20.dp, y = 60.dp)
            )
            Text(
                dishCategory ?: "Loading",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.absoluteOffset(x = 20.dp, y = maxHeight.minus(60.dp))
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                modifier = Modifier
                    .absoluteOffset(x = 20.dp, y = maxHeight.minus(140.dp))
                    .width(150.dp)
            )
//                Text( "More", style = MaterialTheme.typography.bodySmall.copy(fontSize =9.sp), modifier = Modifier.absoluteOffset(x=260.dp,y=maxHeight.minus(80.dp)).width(240.dp).clickable {
//
//                })
            Text(
                foodCategory.name,
                style = MaterialTheme.typography.bodySmall,
                color = if (foodCategory == FoodCategory.VEG) Color.Green else Color.Red,
                modifier = Modifier.absoluteOffset(x = 70.dp, y = 40.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.absoluteOffset(x=20.dp,y=maxHeight.minus(40.dp))) {
                Text(rating.toString(), style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Default.Person, "Person", tint = MaterialTheme.colorScheme.primary)
                Text(numberOfRatings.toString(), style = MaterialTheme.typography.bodySmall)
            }

            //Add Order
            val orderLimit = available_qty
            val color = MaterialTheme.colorScheme.primary
            Row(
                Modifier
                    .scale(0.85f)
                    .absoluteOffset(x = maxWidth.minus(100.dp), y = 190.dp)
                    .background(
                        if (orderCount == 0) color else Color.White,
                        MaterialTheme.shapes.medium
                    )
                    .border(
                        1.dp,
                        if (orderCount != 0) color else Color.White,
                        MaterialTheme.shapes.medium
                    )
                    .width(120.dp)
                    .height(40.dp)
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (orderCount > 0 && orderLimit != orderCount) TextButton(
                    { orderCount--; },
                    enabled = orderCount > 0 && orderLimit != orderCount
                ) { Text("-") }
                Text(
                    if (orderCount == 0) "Add Order" else if (orderCount == orderLimit) "Max" else orderCount.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    color = if (orderCount == 0) Color.Unspecified else color,
                    modifier = Modifier.clickable {
                        if (orderCount == 0) {
                            orderCount = 1; } else if (orderCount == orderLimit) {
                            orderCount = orderLimit - 1; }
                    })
                if (orderCount < orderLimit && orderCount != 0) TextButton(
                    { orderCount++; },
                    enabled = orderCount < orderLimit && orderCount != 0
                ) { Text("+") }
            }
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .absoluteOffset(y = maxHeight.minus(10.dp))
            )
        }

    }

}

@Composable
fun StarRatingAndroidView(rating: Float, maxRating: Int = 5, onRatingChange: (Float) -> Unit, ) {
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                numStars = maxRating
                stepSize = 0.1f
                onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
                    if (fromUser) {
                        onRatingChange(rating)
                    }
                }
            }
        },
        update = { view ->
            view.rating = rating
        }
    )
}

//                    orderItems.value.filter { it.quantity>0 }.forEach { food->
//                        foodList.find { it.id.toString()==food.id }?.let {
//                            food.OrderItemDisplay(){ food1->
//                                orderItems.value=orderItems.value.map {
//                                    if(it.id==food1.id) food1  else it
//                                }
//                            }
//                        }
//                    }


//                    Row(Modifier.border(1.dp, Color.LightGray,MaterialTheme.shapes.small),horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
//
//                            TextButton({
//                                openSort=!openSort
//                            }) {
//                                Text("Sort")
//                            }
//
//                            Spacer(Modifier.width(10.dp))
//                           if(!openSort) {Text(sort.second);Spacer(Modifier.width(10.dp))}
//                            AnimatedVisibility(openSort, enter = expandHorizontally(), exit = shrinkHorizontally()) {
//                            Row( horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                                AssistChip(
//                                    {
//                                        sort = none
//                                        openSort=false
//                                    },
//                                    { Text("None", style = MaterialTheme.typography.bodyMedium) },
//                                    trailingIcon = {
//                                        if (sort == none) Icon(Icons.Default.Check, "")
//                                    },
//                                    shape = MaterialTheme.shapes.small
//                                )
//                                AssistChip(
//                                    {
//                                        sort = priceHighToLow
//                                        openSort=false
//                                    },
//                                    {
//                                        Text(
//                                            "Price High To Low",
//                                            style = MaterialTheme.typography.bodyMedium
//                                        )
//                                    },
//                                    trailingIcon = {
//                                        if (sort == priceHighToLow) Icon(Icons.Default.Check, "")
//                                    },
//                                    shape = MaterialTheme.shapes.small
//                                )
//
//                                AssistChip(
//                                    {
//                                        sort = priceLowToHigh
//                                        openSort=false
//                                    },
//                                    {
//                                        Text(
//                                            priceLowToHigh.second,
//                                            style = MaterialTheme.typography.bodyMedium
//                                        )
//                                    },
//                                    trailingIcon = {
//                                        if (sort == priceLowToHigh) Icon(Icons.Default.Check, "")
//                                    },
//                                    shape = MaterialTheme.shapes.small
//                                )
//
//                                AssistChip(
//                                    {
//                                        sort = ratingHighToLow
//                                        openSort=false
//                                    },
//                                    {
//                                        Text(
//                                            ratingHighToLow.second,
//                                            style = MaterialTheme.typography.bodyMedium
//                                        )
//                                    },
//                                    trailingIcon = {
//                                        if (sort == ratingHighToLow) Icon(Icons.Default.Check, "")
//                                    },
//                                    shape = MaterialTheme.shapes.small
//                                )
//
//
//
//
//
//                                Spacer(Modifier.width(10.dp))
//                            }
//                             }
//                    }



@Composable
fun OrderReceiptScreen2(customerInfo: CustomerInfo, foodInfos: List<FoodInfo>, orderItems: List<OrderItem>, ) {

    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxWidth().fillMaxHeight(0.80f)) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .background(Color.Transparent)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            orderItems.forEach {
                Row(
                    Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.extraSmall
                        )
                        .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
            Text(
                orderItems.map { it.total() }.sum().toString(),
                modifier = Modifier.padding(end = 20.dp),
                style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Right)
            )
            Button(onClick = {
                scope.launch {

                    supabase.from("public", "OrderInfo").insert(
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
                    orderItems.forEach {
                        val foodInfo = foodInfos.find { food -> food.id.toString() == it.id }
                        foodInfo?.let { food ->
                            supabase.from(Table.FoodInfo.name).update({
                                set("available_qty", food.available_qty - it.quantity)
                            }) {
                                filter {
                                    eq("id", it.id)
                                }
                            }
                        }
                    }


                }
            }) {
                Text("Place Order")
            }
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
//@Composable
//fun RestaurantScreen2(customerInfo: CustomerInfo, restaurantInfo: RestaurantInfo) {
//
//    val restorent = viewModel<RestaurantViewModel>()
//
//    restaurantInfo.apply {
//    var menuOffset by remember {
//        mutableStateOf(Offset(0f,0f))
//    }
//    var openMenu by remember {
//        mutableStateOf(false)
//    }
//    val scroll= rememberScrollState()
//    var goToMore by remember {
//        mutableStateOf(false)
//    }
//
//    var foodList by remember {
//        mutableStateOf(listOf<FoodInfo>())
//    }
//
//        var search by remember {
//            mutableStateOf("")
//        }
//        var switchVegMode by remember {
//            mutableStateOf(false to false)
//        }
//
//        var sortByCost by remember {
//            mutableStateOf(false)
//        }
//
//        var orderItems by remember { mutableStateOf(arrayListOf<OrderItem>()) }
//
//
//        val scope = rememberCoroutineScope()
//        var scaffoldState = rememberBottomSheetScaffoldState()
//        var openBottomSheet by remember {
//            mutableStateOf(false)
//        }
//        if (orderItems.filter { it.quantity > 0 }.isNotEmpty()) {
//            openBottomSheet = true
//            scope.launch {
//                scaffoldState.bottomSheetState.expand()
//            }
//        } else {
//            openBottomSheet = false
//        }
//
//        var openRecptScreen by remember {
//            mutableStateOf(false)
//        }
//
//
//
//        var _foodList= flow<List<FoodInfo>> {
//            while (true){
//                emit(supabase.from(Table.FoodInfo.name).select{
//                    filter {
//                        eq("restaurantChannelId",restaurantInfo.channel_id?:"")
//                    }
//                }.decodeList())
//                delay(1000L)
//            }
//        }
//
//        LaunchedEffect(key1 = Unit) {
//            while (true) {
//                _foodList.collect {
//                    foodList = it.filter {
//                        search.isEmpty() || it.name.contains(search, true) &&
//                                it.available_qty > 0 && it.isAvailable == Availability.Available
//                                && if (switchVegMode.first && switchVegMode.second) it.foodCategory == FoodCategory.VEG || it.foodCategory == FoodCategory.NON_VEG else if (switchVegMode.first) it.foodCategory == FoodCategory.VEG else if (switchVegMode.second) it.foodCategory == FoodCategory.NON_VEG else true
//
//                    }.sortedBy {
//                        if (sortByCost) it.name else it.price.toString()
//                    }
//
//                }
//                val tempList = ArrayList<OrderItem>()
//                tempList.addAll(foodList.map {
//                    OrderItem(it.id.toString(), it.name, it.price, 9, it.dishCategory, "")
//                })
//                delay(1000L)
//
//            }
//
//        }
//
//        LaunchedEffect(key1 = foodList) {
//            orderItems =
//                java.util.ArrayList<OrderItem>().apply {
//                    addAll(orderItems)
//                    foodList.forEach {
//                        if (!orderItems.map { it.id }.contains(it.id.toString())) {
//                            add(OrderItem(it.id.toString(), it.name, it.price, 0))
//
//                        }
//                    }
//                    removeIf {
//                        !foodList.map { it.id.toString() }.contains(it.id)
//                    }
//                    //when update
//
//                    Log.e("OrderItems", (foodList.size).toString())
//
//                }
//        }
//
//
//
//
//
//    AnimatedVisibility(goToMore, enter = slideInHorizontally(),exit = slideOutVertically()) {
//        Column(Modifier.fillMaxSize()) {
//            Text("Next Hello")
//            IconButton({
//                goToMore=false
//            }){
//                Icon(Icons.Default.MoreVert, "", Modifier.size(35.dp))
//            }
//        }
//    }
//    AnimatedVisibility(!goToMore, enter = slideInHorizontally(),exit = slideOutVertically()) {
//        BottomSheetScaffold({
//                //Bottom Sheet Content
//                if (openBottomSheet) if (orderItems.isNotEmpty()) {
//                    Column(Modifier.fillMaxWidth()) {
//
//                        if (openRecptScreen) OrderReceiptScreen2(
//                            customerInfo,
//                            foodList,
//                            orderItems.filter { it.quantity > 0 })
//
//                    }
//                }
//            }, sheetSwipeEnabled = false,
//            sheetContainerColor = Color.White,
//            sheetPeekHeight = 0.dp, scaffoldState = scaffoldState, topBar = {
//                Row(
//                    Modifier
//                        .background(Color.White)
//                        .fillMaxWidth()
//                        .height(30.dp),
//                    horizontalArrangement = Arrangement.Absolute.SpaceAround,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    if (scroll.value > 150) {
//                Text(name, style = MaterialTheme.typography.titleLarge)
//                        if (false) IconButton({
//                    goToMore=true
//                }){
//                    Text("More")
//                    // Icon(Icons.Default.MoreVert, "", Modifier.size(35.dp))
//                }
//            }
//        }
//        }) {
//            Box(modifier = Modifier.fillMaxSize()) {
//
//                //Menu box
//                if (openMenu) Box(modifier = Modifier
//                    .offset { menuOffset.round() }
//                    .absoluteOffset(x = -100.plus(175).dp, y = -64.plus(350).dp)
//                    .zIndex(1f)
//                    .background(Color.White, MaterialTheme.shapes.extraLarge)
//                    .size(350.dp, 350.dp)) {
//                    val groupedItems = foodList.groupBy { it.dishCategory }
//
//                    LazyColumn(Modifier.padding(25.dp)) {
//                        groupedItems.forEach { (category, itemsInCategory) ->
//                            item {
//                                Text(category.toString(),Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium) // Display category header
//                            }
//                            items(itemsInCategory) { item ->
//                                Row(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
//                                    Text(item.name)
//                                    Text(item.available_qty.toString())
//                                }
//                            }
//                        }
//                    }
//
//
//                }
//
//                Column(Modifier.padding(top = 45.dp, start = 15.dp, end = 10.dp, bottom = 70.dp).fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraLarge)) {
//                    Spacer(Modifier.height(20.dp))
//                    //Title
//
//                    if (orderItems.map { it.quantity > 0 }.contains(true)) Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(
//                            "Added items ${
//                                orderItems.map { it.quantity }.sum()
//                            }  cost${orderItems.map { it.price * it.quantity }.sum()}",
//                            color = MaterialTheme.colorScheme.primary,
//                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
//                            modifier = Modifier.padding(horizontal = 20.dp)
//                        )
//                        TextButton({
//                            openRecptScreen = !openRecptScreen
//                        }) {
//                            Text(if (!openRecptScreen) "Proceed" else "Close")
//                        }
//                    }
//
//                    Column(
//                        Modifier
//                            .fillMaxHeight(0.90f)
//                            .verticalScroll(scroll)
//                            .fillMaxWidth()
//                            .padding(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)
//                    ) {
//                        Text(restaurantInfo.name, style = MaterialTheme.typography.titleLarge)
//                        //Rating
//                        val decimal = restaurantInfo.rating - Math.floor(restaurantInfo.rating)
//                        Row(Modifier) {
//                            Text(
//                                restaurantInfo.rating.toString(),
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Spacer(Modifier.width(10.dp))
//                            for (i in 1..Math.floor(restaurantInfo.rating).toInt()) {
//                                Icon(
//                                    Icons.Default.Star,
//                                    "",
//                                    tint = Color.Yellow,
//                                    modifier = Modifier.size(10.dp)
//                                )
//                                if (i == Math.floor(restaurantInfo.rating)
//                                        .toInt() && decimal != 0.0
//                                ) Icon(
//                                    painterResource(R.drawable.half_star),
//                                    "",
//                                    tint = Color.Yellow,
//                                    modifier = Modifier.size(10.dp)
//                                )
//                            }
//                        }
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            Icon(Icons.Default.Person, "Person")
//                            Text(
//                                numberOfRatings.toString(),
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                        }
//                        //cuisine
//                        LazyRow(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .height(50.dp)
//                                .padding(end = 15.dp)
//                        ) {
//                            stickyHeader {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                ) {
//                                    Spacer(Modifier.width(15.dp))
//                                    Icon(
//                                        painter = painterResource(R.drawable.timer),
//                                        "Timer",
//                                        tint = MaterialTheme.colorScheme.primary,
//                                        modifier = Modifier
//                                            .background(
//                                                Color.White,
//                                                MaterialTheme.shapes.small
//                                            )
//                                            .width(30.dp)
//                                            .padding(5.dp)
//                                    )
//                                    Text(
//                                        "$estimatedDeliveryTime min",
//                                        style = MaterialTheme.typography.bodyLarge,
//                                        modifier = Modifier.padding(vertical = 10.dp)
//                                    )
//                                    Spacer(Modifier.width(20.dp))
//                                }
//                            }
//                            item {
//                                Spacer(Modifier.width(10.dp))
//                                Text(
//                                    cuisine.joinToString(","),
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
//                                Spacer(Modifier.width(30.dp))
//                            }
//
//                        }
//
//                        Spacer(Modifier.height(10.dp))
//
//                        //Filter
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(10.dp)
//                        ) {
//
//                            AssistChip(
//                                {
//                                    switchVegMode = switchVegMode.copy(first = !switchVegMode.first)
//                                },
//                                { Text("Veg", style = MaterialTheme.typography.bodyMedium) },
//                                trailingIcon = {
//                                    if (switchVegMode.first) Icon(Icons.Default.Check, "")
//                                })
//
//                            AssistChip(
//                                {
//                                    switchVegMode =
//                                        switchVegMode.copy(second = !switchVegMode.second)
//                                },
//                                { Text("Non Veg", style = MaterialTheme.typography.bodyMedium) },
//                                trailingIcon = {
//                                    if (switchVegMode.second) Icon(Icons.Default.Check, "")
//                                })
//
//                            AssistChip(
//                                {
//                                    sortByCost = !sortByCost
//                                },
//                                {
//                                    Text(
//                                        "Order By Cost",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                },
//                                trailingIcon = {
//                                    if (sortByCost) Icon(Icons.Default.Check, "")
//                                })
//
//
//                        }
//
//                        foodList.forEach {
//                            it.FoodItem { order ->
//                                orderItems = arrayListOf<OrderItem>().apply {
//                                    addAll(orderItems.map {
//                                        if (order.id == it.id) order else it
//                                    })
//                                }
//
//                            }
//                        }
//
//                    }
//
//                    val color = Color(0xFFEC407A)
//                    Spacer(Modifier.height(15.dp))
//                    Column(
//                        Modifier
//                            .fillMaxWidth()
//                            .background(Color(0xFFFDFDFD))) {
//                        Row(
//                            Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.Start,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Spacer(Modifier.width(25.dp))
//                            OutlinedTextField(search,
//                                { search = it },
//                                placeholder = {
//                                    Text(
//                                        "Search Your Food",
//                                        color = Color.LightGray,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                },
//                                leadingIcon = {
//                                    Icon(
//                                        Icons.Default.Search,
//                                        "Search",
//                                        Modifier.size(30.dp),
//                                        tint = MaterialTheme.colorScheme.primary
//                                    )
//                                },
//                                shape = MaterialTheme.shapes.small,
//                                colors = OutlinedTextFieldDefaults.colors(
//                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
//                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
//                                ),
//                                maxLines = 2,
//                                textStyle = MaterialTheme.typography.bodySmall,
//                                modifier = Modifier
//                                    .scale(0.80f)
//                                    .fillMaxWidth(0.75f)
//                                    .height(50.dp)
//                            )
//                            Spacer(Modifier.width(30.dp))
//                            Column(
//                                Modifier
//                                    .padding(5.dp)
//                                    .onGloballyPositioned {
//                                        menuOffset = it.localToWindow(Offset.Zero)
//                                    }, horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                IconButton({
//                                    openMenu = !openMenu
//                                }) {
//                                    Icon(
//                                        painter = painterResource(R.drawable.menu),
//                                        "",
//                                        Modifier.size(35.dp)
//                                    )
//                                }
//                                Text("Menu", style = MaterialTheme.typography.bodySmall)
//                            }
//                        }
//                        Spacer(Modifier.height(15.dp))
//                    }
//
//
//                    //  Text(orderItems.filter { it.quantity>0 }.joinToString { "${it.name} x ${it.quantity}" })
//
//
//                }
//
//            }
//
//        }
//    }
//        }
//}









