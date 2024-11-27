package com.manway.Toofoh.Screen

import Ui.enums.Availability
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel

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
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.toffoh.admin.data.ServiceArea
import data.enums.Role
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.reflect.Array.set


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@Composable
@Preview
fun RestaurantScreen(){
    val restorent = viewModel<RestaurantViewModel>()
    var menuOffset by remember {
        mutableStateOf(Offset(0f,0f))
    }
    var openMenu by remember {
        mutableStateOf(false)
    }
    val scroll= rememberScrollState()
    var goToMore by remember {
        mutableStateOf(false)
    }

    val name="Sample Nam"
    val cuisine= listOf("sample1","sample2","sample3","sample4","sample5","sample6","sample7","sample8","sample9","sample10")
    val estimatedDeliveryTime=30
    val  rating=3.5f
    val numberOfRatings=105
    val foodList = (0..10).map {
        FoodInfo.initialFoodInfo.copy(
            id = it,
            foodCategory = listOf(FoodCategory.VEG, FoodCategory.NON_VEG).random(),
            name = "name$it",
            dishCategory = listOf("cat1", "cat2", "cat3", "cat4").random(),
            available_qty = (5..18).random()
        )
    }
    var orderItems by remember {
        mutableStateOf(foodList.map {
            OrderItem(it.id.toString(),it.name,it.price,0)
        })
    }
    AnimatedVisibility(goToMore, enter = slideInHorizontally(),exit = slideOutVertically()) {
        Column(Modifier.fillMaxSize()) {
            Text("Next Hello")
            IconButton({
                goToMore=false
            }){
                Icon(Icons.Default.MoreVert, "", Modifier.size(35.dp))
            }
        }
    }

    AnimatedVisibility(!goToMore, enter = slideInHorizontally(), exit = slideOutVertically()) {
        Scaffold(bottomBar = {
            val color=Color(0xFFEC407A)
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDFDFD))) {
                Text("Add items ${orderItems.map { it.quantity }.sum()}  cost${orderItems.map { it.price*it.quantity }.sum()}", style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center), modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .fillMaxWidth()
                    .background(color, MaterialTheme.shapes.large)
                    .padding(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(25.dp))
                    TextField(
                        value = "",
                        onValueChange = {},
                        trailingIcon = {
                            IconButton({}) {
                                Icon(
                                    Icons.Default.Search,
                                    "",
                                    Modifier.size(35.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .background(
                                Color.LightGray.copy(0.25f),
                                MaterialTheme.shapes.large
                            )
                            .fillMaxWidth(0.70f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(Modifier.width(30.dp))
                    Column(
                        Modifier
                            .padding(5.dp)
                            .onGloballyPositioned { menuOffset = it.localToWindow(Offset.Zero) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton({
                            openMenu = !openMenu
                        }) {
                            Icon(painter = painterResource(R.drawable.menu), "", Modifier.size(35.dp))
                        }
                        Text("Menu", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(15.dp))
            }

        }, topBar = { Row(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(100.dp), horizontalArrangement = Arrangement.Absolute.SpaceAround, verticalAlignment =Alignment.CenterVertically ) {
                if (scroll.value > 320){
                    Text(name, style = MaterialTheme.typography.titleLarge)
                    IconButton({
                        goToMore=true
                    }){
                        Text("More")
                      // Icon(Icons.Default.MoreVert, "", Modifier.size(35.dp))
                    }
                }
            }
        }) {
            Box(modifier = Modifier.fillMaxSize()) {

                //Menu box
                if (openMenu) Box(modifier = Modifier
                    .offset { menuOffset.round() }
                    .absoluteOffset(x = -100.plus(175).dp, y = -64.plus(350).dp)
                    .zIndex(1f)
                    .background(Color.White, MaterialTheme.shapes.extraLarge)
                    .size(350.dp, 350.dp)) {
                    val groupedItems = foodList.groupBy { it.dishCategory }

                    LazyColumn(Modifier.padding(25.dp)) {
                        groupedItems.forEach { (category, itemsInCategory) ->
                            item {
                                Text(category.toString(),Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium) // Display category header
                            }
                            items(itemsInCategory) { item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.name)
                                    Text(item.available_qty.toString())
                                }
                            }
                        }
                    }


                }

                Column(
                    Modifier
                        .verticalScroll(scroll)
                        .padding(top = 65.dp, start = 15.dp, end = 10.dp, bottom = 130.dp)
                        .fillMaxSize()
                        .background(Color.LightGray.copy(0.35f), MaterialTheme.shapes.extraLarge)) {
                    Spacer(Modifier.height(20.dp))
                    //Title
                    Row(Modifier.fillMaxWidth()) {
                        Text(name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.fillMaxWidth(0.6f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(rating.toString(), style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    "Person",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(numberOfRatings.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    //cuisine
                    LazyRow(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .height(50.dp)
                        .padding(end = 15.dp)) {
                        stickyHeader {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                            ) {
                                Spacer(Modifier.width(15.dp))
                                Icon(
                                    painter = painterResource(R.drawable.timer),
                                    "Timer",
                                    Modifier
                                        .width(30.dp)
                                        .padding(5.dp)
                                )
                                Text(
                                    "$estimatedDeliveryTime min ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                        item {
                            Spacer(Modifier.width(10.dp))
                            Text(
                                cuisine.joinToString(","),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.width(30.dp))
                        }

                    }

                    Text(
                        "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    //Filter
                  //  Text(orderItems.filter { it.quantity>0 }.joinToString { "${it.name} x ${it.quantity}" })
                    foodList.forEach {
                        it.FoodItem { order ->
                            orderItems = orderItems.map {
                                if (order.id.toString() == it.id) order else it
                            }
                        }
                    }

                    }

                }

            }
        }

    }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun RestaurantScreen(customerInfo: CustomerInfo, restaurantInfo: RestaurantInfo) {

    val restorent = viewModel<RestaurantViewModel>()

    restaurantInfo.apply {
    var menuOffset by remember {
        mutableStateOf(Offset(0f,0f))
    }
    var openMenu by remember {
        mutableStateOf(false)
    }
    val scroll= rememberScrollState()
    var goToMore by remember {
        mutableStateOf(false)
    }

    var foodList by remember {
        mutableStateOf(listOf<FoodInfo>())
    }

        var search by remember {
            mutableStateOf("")
        }
        var switchVegMode by remember {
            mutableStateOf(false to false)
        }

        var sortByCost by remember {
            mutableStateOf(false)
        }

        var orderItems by remember { mutableStateOf(arrayListOf<OrderItem>()) }


        val scope = rememberCoroutineScope()
        var scaffoldState = rememberBottomSheetScaffoldState()
        var openBottomSheet by remember {
            mutableStateOf(false)
        }
        if (orderItems.filter { it.quantity > 0 }.isNotEmpty()) {
            openBottomSheet = true
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        } else {
            openBottomSheet = false
        }

        var openRecptScreen by remember {
            mutableStateOf(false)
        }



        var _foodList= flow<List<FoodInfo>> {
            while (true){
                emit(supabase.from(Table.FoodInfo.name).select{
                    filter {
                        eq("restaurantChannelId",restaurantInfo.channel_id?:"")
                    }
                }.decodeList())
                delay(1000L)
            }
        }

        LaunchedEffect(key1 = Unit) {
            while (true) {
                _foodList.collect {
                    foodList = it.filter {
                        search.isEmpty() || it.name.contains(search, true) &&
                                it.available_qty > 0 && it.isAvailable == Availability.Available
                                && if (switchVegMode.first && switchVegMode.second) it.foodCategory == FoodCategory.VEG || it.foodCategory == FoodCategory.NON_VEG else if (switchVegMode.first) it.foodCategory == FoodCategory.VEG else if (switchVegMode.second) it.foodCategory == FoodCategory.NON_VEG else true

                    }.sortedBy {
                        if (sortByCost) it.name else it.price.toString()
                    }

                }
                val tempList = ArrayList<OrderItem>()
                tempList.addAll(foodList.map {
                    OrderItem(it.id.toString(), it.name, it.price, 9)
                })
                delay(1000L)

            }

        }

        LaunchedEffect(key1 = foodList) {
            orderItems =
                java.util.ArrayList<OrderItem>().apply {
                    addAll(orderItems)
                    foodList.forEach {
                        if (!orderItems.map { it.id }.contains(it.id.toString())) {
                            add(OrderItem(it.id.toString(), it.name, it.price, 0))

                        }
                    }
                    removeIf {
                        !foodList.map { it.id.toString() }.contains(it.id)
                    }
                    //when update

                    Log.e("OrderItems", (foodList.size).toString())

                }
        }





    AnimatedVisibility(goToMore, enter = slideInHorizontally(),exit = slideOutVertically()) {
        Column(Modifier.fillMaxSize()) {
            Text("Next Hello")
            IconButton({
                goToMore=false
            }){
                Icon(Icons.Default.MoreVert, "", Modifier.size(35.dp))
            }
        }
    }
    AnimatedVisibility(!goToMore, enter = slideInHorizontally(),exit = slideOutVertically()) {
        BottomSheetScaffold(
            {
                //Bottom Sheet Content
                if (openBottomSheet) if (orderItems.isNotEmpty()) {
                    Column(Modifier.fillMaxWidth()) {

                        if (openRecptScreen) OrderReceiptScreen2(
                            customerInfo,
                            foodList,
                            orderItems.filter { it.quantity > 0 })

                    }
                }
            }, sheetSwipeEnabled = false,
            sheetContainerColor = Color.White,
            sheetPeekHeight = 0.dp, scaffoldState = scaffoldState, topBar = {
                Row(
                    Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .height(30.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (scroll.value > 150) {
                Text(name, style = MaterialTheme.typography.titleLarge)
                        if (false) IconButton({
                    goToMore=true
                }){
                    Text("More")
                    // Icon(Icons.Default.MoreVert, "", Modifier.size(35.dp))
                }
            }
        }
        }) {
            Box(modifier = Modifier.fillMaxSize()) {

                //Menu box
                if (openMenu) Box(modifier = Modifier
                    .offset { menuOffset.round() }
                    .absoluteOffset(x = -100.plus(175).dp, y = -64.plus(350).dp)
                    .zIndex(1f)
                    .background(Color.White, MaterialTheme.shapes.extraLarge)
                    .size(350.dp, 350.dp)) {
                    val groupedItems = foodList.groupBy { it.dishCategory }

                    LazyColumn(Modifier.padding(25.dp)) {
                        groupedItems.forEach { (category, itemsInCategory) ->
                            item {
                                Text(category.toString(),Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium) // Display category header
                            }
                            items(itemsInCategory) { item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.name)
                                    Text(item.available_qty.toString())
                                }
                            }
                        }
                    }


                }

                Column(
                    Modifier
                        .padding(top = 45.dp, start = 15.dp, end = 10.dp, bottom = 70.dp)
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.extraLarge
                        )
                ) {
                    Spacer(Modifier.height(20.dp))
                    //Title

                    if (orderItems.map { it.quantity > 0 }.contains(true)) Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Added items ${
                                orderItems.map { it.quantity }.sum()
                            }  cost${orderItems.map { it.price * it.quantity }.sum()}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        TextButton({
                            openRecptScreen = !openRecptScreen
                        }) {
                            Text(if (!openRecptScreen) "Proceed" else "Close")
                        }
                    }

                    Column(
                        Modifier
                            .fillMaxHeight(0.90f)
                            .verticalScroll(scroll)
                            .fillMaxWidth()
                            .padding(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(restaurantInfo.name, style = MaterialTheme.typography.titleLarge)
                        //Rating
                        val decimal = restaurantInfo.rating - Math.floor(restaurantInfo.rating)
                        Row(Modifier) {
                            Text(
                                restaurantInfo.rating.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.width(10.dp))
                            for (i in 1..Math.floor(restaurantInfo.rating).toInt()) {
                                Icon(
                                    Icons.Default.Star,
                                    "",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(10.dp)
                                )
                                if (i == Math.floor(restaurantInfo.rating)
                                        .toInt() && decimal != 0.0
                                ) Icon(
                                    painterResource(R.drawable.half_star),
                                    "",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Person, "Person")
                            Text(
                                numberOfRatings.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        //cuisine
                        LazyRow(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(50.dp)
                                .padding(end = 15.dp)
                        ) {
                            stickyHeader {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                ) {
                                    Spacer(Modifier.width(15.dp))
                                    Icon(
                                        painter = painterResource(R.drawable.timer),
                                        "Timer",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .background(
                                                Color.White,
                                                MaterialTheme.shapes.small
                                            )
                                            .width(30.dp)
                                            .padding(5.dp)
                                    )
                                    Text(
                                        "$estimatedDeliveryTime min",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                    Spacer(Modifier.width(20.dp))
                                }
                            }
                            item {
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    cuisine.joinToString(","),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.width(30.dp))
                            }

                        }

                        Spacer(Modifier.height(10.dp))

                        //Filter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            AssistChip(
                                {
                                    switchVegMode = switchVegMode.copy(first = !switchVegMode.first)
                                },
                                { Text("Veg", style = MaterialTheme.typography.bodyMedium) },
                                trailingIcon = {
                                    if (switchVegMode.first) Icon(Icons.Default.Check, "")
                                })

                            AssistChip(
                                {
                                    switchVegMode =
                                        switchVegMode.copy(second = !switchVegMode.second)
                                },
                                { Text("Non Veg", style = MaterialTheme.typography.bodyMedium) },
                                trailingIcon = {
                                    if (switchVegMode.second) Icon(Icons.Default.Check, "")
                                })

                            AssistChip(
                                {
                                    sortByCost = !sortByCost
                                },
                                {
                                    Text(
                                        "Order By Cost",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                trailingIcon = {
                                    if (sortByCost) Icon(Icons.Default.Check, "")
                                })


                        }

                        foodList.forEach {
                            it.FoodItem { order ->
                                orderItems = arrayListOf<OrderItem>().apply {
                                    addAll(orderItems.map {
                                        if (order.id == it.id) order else it
                                    })
                                }

                            }
                        }

                    }

                    val color = Color(0xFFEC407A)
                    Spacer(Modifier.height(15.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFDFDFD))) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(25.dp))
                            OutlinedTextField(search,
                                { search = it },
                                placeholder = {
                                    Text(
                                        "Search Your Food",
                                        color = Color.LightGray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        "Search",
                                        Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                shape = MaterialTheme.shapes.small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 2,
                                textStyle = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .scale(0.80f)
                                    .fillMaxWidth(0.75f)
                                    .height(50.dp)
                            )
                            Spacer(Modifier.width(30.dp))
                            Column(
                                Modifier
                                    .padding(5.dp)
                                    .onGloballyPositioned {
                                        menuOffset = it.localToWindow(Offset.Zero)
                                    }, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton({
                                    openMenu = !openMenu
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.menu),
                                        "",
                                        Modifier.size(35.dp)
                                    )
                                }
                                Text("Menu", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                    }


                    //  Text(orderItems.filter { it.quantity>0 }.joinToString { "${it.name} x ${it.quantity}" })


                }

            }

        }
    }
        }
}

    @Composable
    fun FoodInfo.FoodItem(orderCountChange: (OrderItem) -> Unit) {
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
                        Math.min(orderCount, available_qty)
                    )
                )

            }

            LaunchedEffect(key1 = orderCount) {
                orderCountChange(OrderItem(id.toString(), name, price, orderCount))
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
fun OrderReceiptScreen2(
    customerInfo: CustomerInfo,
    foodInfos: List<FoodInfo>,
    orderItems: List<OrderItem>,
) {

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.80f)) {
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






