package com.manway.Toofoh.Screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

import com.manway.Toofoh.R
import com.manway.Toofoh.data.FoodCategory
import com.manway.Toofoh.data.OrderInfo
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.getImage
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@Composable
@Preview
fun RestaurantScreen(){
    val restaurant:RestaurantInfo
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
    val foodList= (0..10).map { FoodInfo.initialFoodInfo.copy(id=it, foodCategory = listOf(FoodCategory.VEG,FoodCategory.NON_VEG).random(), name = "name$it", dishCategory = listOf("cat1","cat2","cat3","cat4").random(), availableQty = (5..18).random() ) }
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
    AnimatedVisibility(!goToMore, enter = slideInHorizontally(),exit = slideOutVertically()) {
        Scaffold(bottomBar = {
            val color=Color(0xFFEC407A)
            Column(Modifier.fillMaxWidth().background(Color(0xFFFDFDFD))) {
                Text("Add items ${orderItems.map { it.quantity }.sum()}  cost${orderItems.map { it.price*it.quantity }.sum()}", style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center), modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth().background(color, MaterialTheme.shapes.large).padding(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(25.dp))
                    TextField(value = "", onValueChange = {}, trailingIcon = { IconButton({}) { Icon(Icons.Default.Search, "", Modifier.size(35.dp)) } }, modifier = Modifier
                        .background(Color.LightGray.copy(0.25f), MaterialTheme.shapes.large)
                        .fillMaxWidth(0.70f), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                    Spacer(Modifier.width(30.dp))
                    Column(
                        Modifier
                            .padding(5.dp)
                            .onGloballyPositioned {
                                menuOffset = it.localToWindow(Offset.Zero)
                            }, horizontalAlignment = Alignment.CenterHorizontally) {
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
        }, topBar = { Row(Modifier.background(Color.White).fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.Absolute.SpaceAround, verticalAlignment =Alignment.CenterVertically ) {
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
                if (openMenu) Box(modifier = Modifier.offset { menuOffset.round() }.absoluteOffset(x = -100.plus(175).dp, y = -64.plus(350).dp).zIndex(1f).background(Color.White, MaterialTheme.shapes.extraLarge).size(350.dp, 350.dp)) {
                    val groupedItems = foodList.groupBy { it.dishCategory }

                    LazyColumn(Modifier.padding(25.dp)) {
                        groupedItems.forEach { (category, itemsInCategory) ->
                            item {
                                Text(category.toString(),Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium) // Display category header
                            }
                            items(itemsInCategory) { item ->
                                Row(Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.name)
                                    Text(item.availableQty.toString())
                                }
                            }
                        }
                    }


                }

                Column(Modifier.verticalScroll(scroll).padding(top = 65.dp, start = 15.dp, end = 10.dp, bottom = 130.dp).fillMaxSize().background(Color.LightGray.copy(0.35f), MaterialTheme.shapes.extraLarge)) {
                    Spacer(Modifier.height(20.dp))
                    //Title
                    Row(Modifier.fillMaxWidth()) {
                        Text(name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.fillMaxWidth(0.6f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(rating.toString(), style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.Person, "Person")
                                Text(numberOfRatings.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }



                    //cuisine
                    LazyRow(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(50.dp).padding(end = 15.dp)) {
                        stickyHeader {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.background(Color(0xFFEBE9EB))
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
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.width(30.dp))
                        }

                    }
                  //  Text(orderItems.filter { it.quantity>0 }.joinToString { "${it.name} x ${it.quantity}" })
                    foodList.forEach {
                        it.FoodItem { id, count ->
                            orderItems = orderItems.map {
                                if (id.toString() == it.id) it.copy(quantity = count) else it
                            }
                        }
                    }

                    }

                }

            }
        }
    }

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RestaurantScreen(restaurantInfo: RestaurantInfo){

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

            var orderItems by remember {
        mutableStateOf(listOf<OrderItem>())

    }

    LaunchedEffect(key1 = restaurantInfo){
        _foodList.collect{
            foodList=it
        }
    }



//    val name="Sample Name"
//    val cuisine= listOf("sample1","sample2","sample3","sample4","sample5","sample6","sample7","sample8","sample9","sample10")
//    val estimatedDeliveryTime=30
//    val  rating=3.5f
//    val numberOfRatings=105

   //val foodList= (0..10).map { FoodInfo.initialFoodInfo.copy(id=it, foodCategory = listOf(FoodCategory.VEG,FoodCategory.NON_VEG).random(), name = "name$it", dishCategory = listOf("cat1","cat2","cat3","cat4").random(), availableQty = (5..18).random() ) }

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
        Scaffold(bottomBar = {
            val color=Color(0xFFEC407A)
            Column(Modifier.fillMaxWidth().background(Color(0xFFFDFDFD))) {
                Text("Add items ${orderItems.map { it.quantity }.sum()}  cost${orderItems.map { it.price*it.quantity }.sum()}", style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center), modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth().background(color, MaterialTheme.shapes.large).padding(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(25.dp))
                    TextField(value = "", onValueChange = {}, trailingIcon = { IconButton({}) { Icon(Icons.Default.Search, "", Modifier.size(35.dp)) } }, modifier = Modifier
                        .background(Color.LightGray.copy(0.25f), MaterialTheme.shapes.large)
                        .fillMaxWidth(0.70f), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                    Spacer(Modifier.width(30.dp))
                    Column(
                        Modifier
                            .padding(5.dp)
                            .onGloballyPositioned {
                                menuOffset = it.localToWindow(Offset.Zero)
                            }, horizontalAlignment = Alignment.CenterHorizontally) {
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
        }, topBar = { Row(Modifier.background(Color.White).fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.Absolute.SpaceAround, verticalAlignment =Alignment.CenterVertically ) {
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
                if (openMenu) Box(modifier = Modifier.offset { menuOffset.round() }.absoluteOffset(x = -100.plus(175).dp, y = -64.plus(350).dp).zIndex(1f).background(Color.White, MaterialTheme.shapes.extraLarge).size(350.dp, 350.dp)) {
                    val groupedItems = foodList.groupBy { it.dishCategory }

                    LazyColumn(Modifier.padding(25.dp)) {
                        groupedItems.forEach { (category, itemsInCategory) ->
                            item {
                                Text(category.toString(),Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium) // Display category header
                            }
                            items(itemsInCategory) { item ->
                                Row(Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.name)
                                    Text(item.availableQty.toString())
                                }
                            }
                        }
                    }


                }

                Column(Modifier.verticalScroll(scroll).padding(top = 65.dp, start = 15.dp, end = 10.dp, bottom = 130.dp).fillMaxSize().background(Color.LightGray.copy(0.35f), MaterialTheme.shapes.extraLarge)) {
                    Spacer(Modifier.height(20.dp))
                    //Title
                    Row(Modifier.fillMaxWidth()) {
                        Text(name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.fillMaxWidth(0.6f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(rating.toString(), style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.Person, "Person")
                                Text(numberOfRatings.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }



                    //cuisine
                    LazyRow(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(50.dp).padding(end = 15.dp)) {
                        stickyHeader {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.background(Color(0xFFEBE9EB))
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
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.width(30.dp))
                        }

                    }
                    //  Text(orderItems.filter { it.quantity>0 }.joinToString { "${it.name} x ${it.quantity}" })
                    foodList.forEach {
                        it.FoodItem { id, count ->
                            orderItems = orderItems.map {
                                if (id.toString() == it.id) it.copy(quantity = count) else it
                            }
                        }
                    }

                }

            }

        }
    }
        }
}



    @Composable
    fun FoodInfo.FoodItem(orderCountChange:(Int?,Int)->Unit){
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
            BoxWithConstraints(Modifier.padding(3.dp).fillMaxWidth().height(200.dp).background(Color.White, MaterialTheme.shapes.small)) {

                supabase.getImage(imageUrl,Modifier.absoluteOffset(maxWidth.minus(160.dp), 10.dp).clip(MaterialTheme.shapes.medium).size(140.dp), ContentScale.FillBounds)

//                Image(painter = painterResource(R.drawable.main_screen),"", modifier = Modifier
//                 .absoluteOffset(maxWidth.minus(160.dp), 10.dp)
//                    .clip(MaterialTheme.shapes.medium)
//                    .size(140.dp), contentScale = ContentScale.FillBounds)

                Text(name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.absoluteOffset(x=20.dp,y=10.dp))
                Text(price.toString(), style = MaterialTheme.typography.titleMedium, modifier = Modifier.absoluteOffset(x=20.dp,y=40.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, maxLines = 3, modifier = Modifier
                    .width(225.dp)
                    .absoluteOffset(x = 20.dp, y = 60.dp))
                Text(dishCategory?:"Loading", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.absoluteOffset(x=20.dp,y=maxHeight.minus(60.dp)))
                Text(foodCategory.name, style = MaterialTheme.typography.titleMedium, color = if(foodCategory==FoodCategory.VEG) Color.Green else Color.Red, modifier = Modifier.absoluteOffset(x=70.dp,y=40.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.absoluteOffset(x=20.dp,y=maxHeight.minus(40.dp))) {
                    Text(rating.toString(), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.Person, "Person")
                    Text(numberOfRatings.toString(), style = MaterialTheme.typography.bodySmall)
                }

                //Add Order
                val orderLimit=10
                val color=Color(0xFFEC407A)
                Row(
                    Modifier
                        .absoluteOffset(x = maxWidth.minus(145.dp), y = 130.dp)
                        .background(if (orderCount == 0 || orderCount == orderLimit) color else Color.White, MaterialTheme.shapes.medium)
                        .width(120.dp)
                        .height(40.dp)
                        .padding(5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    if(orderCount>0&&orderLimit!=orderCount)     TextButton({ orderCount-- }, enabled =orderCount>0&&orderLimit!=orderCount ) { Text("-") }
                    Text(if(orderCount==0) "Add Order" else if(orderCount==orderLimit) "Max" else orderCount.toString(), style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center), color =if(orderCount==0||orderCount==orderLimit) Color.Unspecified else color , modifier = Modifier.clickable { if(orderCount==0) orderCount=1 else if(orderCount==orderLimit) orderCount=orderLimit-1 })
                    if(orderCount<orderLimit&&orderCount!=0) TextButton({ orderCount++ }, enabled =orderCount<orderLimit&&orderCount!=0 ) { Text("+") }
                }
            }
            orderCountChange(id,orderCount)
        }

    }



