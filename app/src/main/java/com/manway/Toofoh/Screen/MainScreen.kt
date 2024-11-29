package Screen

import Ui.data.Address
import Ui.data.Filter
import Ui.enums.Availability
import android.annotation.SuppressLint
import android.widget.Toast
import com.manway.Toofoh.ViewModel.FoodViewModel
import com.manway.Toofoh.ViewModel.RestaurantViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.R
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.Toofoh.data.CommonFoodInfo
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.dp.getImage
import com.manway.Toofoh.ui.RatingFilter
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.util.regex.Pattern

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.outlined.FavoriteBorder

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout

import com.manway.Toofoh.ViewModel.CommonFoodViewModel
import com.manway.Toofoh.android.LocalDb
import com.manway.Toofoh.screen
import com.manway.toffoh.admin.data.FoodInfo
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from


//#f97a7b
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun preview(localDb: LocalDb, customerInfo: CustomerInfo, settingsListner: (screen) -> Unit, restorentPickListner: (RestaurantInfo) -> Unit, ) {

    var switchVegMode by remember {
        mutableStateOf(false)
    }

    var searchField by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf<Address?>(localDb.gerLocation())
    }


    val restorent = viewModel<RestaurantViewModel>()
    restorent.search(searchField)

    var newest_bool by remember {
        mutableStateOf(false)
        //updated_at
    }
    var quick_delivery_bool by remember {
        mutableStateOf(false)
        //estimated delivery time
    }
    var showAvailable by remember {
        mutableStateOf(false)
        //isAvailable
    }
    var aboveThreeStar by remember {
        mutableStateOf(-1.0)
    }
    var belowPrice by remember {
        mutableStateOf(Double.MAX_VALUE)
    }


    val food= viewModel<FoodViewModel>()

    address?.let {
        restorent.pincode=it.pincode
    }

    val scope= rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showLocationPicker by remember {
        mutableStateOf(false)
    }
    var showFilter by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(address) {
        if (address == null) {
            scaffoldState.bottomSheetState.expand()
            showLocationPicker = true
        }
    }


    // val themeColor=Color(0xFFFf97a7b)

    BottomSheetScaffold(scaffoldState = scaffoldState, sheetContent = {
        if (showLocationPicker) LocationPicker(customerInfo, {
               scope.launch {
                   showLocationPicker=false
               }
           }){
               address=it
            localDb.setLocation(it)
            restorent.pincode = it.pincode
               showLocationPicker=false
            showFilter = false
           }

        val (priceRange, starRating, cuisine) = listOf("Price Range", "Star Rating", "Cuisine")


        var tab by remember {
            mutableStateOf(priceRange)
        }


        if (showFilter) {
            Column(Modifier.fillMaxWidth().height(450.dp).padding(10.dp)) {

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(25.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(priceRange, Modifier.clickable { tab = priceRange }.width(80.dp).border(1.dp, if (tab != priceRange) MaterialTheme.colorScheme.primary else Color.Transparent, MaterialTheme.shapes.medium).padding(5.dp),
                        style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center)
                    )
                    Text(starRating, Modifier.clickable { tab = starRating }.width(80.dp).border(1.dp, if (tab != starRating) MaterialTheme.colorScheme.primary else Color.Transparent, MaterialTheme.shapes.medium).padding(5.dp), style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center))

                    if (false) Text(cuisine,
                        Modifier
                            .clickable { tab = cuisine }
                            .width(80.dp)
                            .border(
                                1.dp,
                                if (tab != cuisine) MaterialTheme.colorScheme.primary else Color.Transparent,
                                MaterialTheme.shapes.medium
                            )
                            .padding(5.dp),
                        style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center)
                    )
                }
                when (tab) {
                    starRating -> {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(aboveThreeStar == -1.0, {
                                restorent.aboveTheStar = -1.0
                                aboveThreeStar = -1.0
                            })
                            Text("None", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(aboveThreeStar == 3.0, {
                                restorent.aboveTheStar = 3.0
                                aboveThreeStar = 3.0
                            })
                            Text("3 Star Above", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(aboveThreeStar == 4.0, {
                                restorent.aboveTheStar = 4.0
                                aboveThreeStar = 4.0
                            })
                            Text("4 Star Above", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(aboveThreeStar == 4.5, {
                                restorent.aboveTheStar = 4.5
                                aboveThreeStar = 4.5
                            })
                            Text("4.5 Star Above", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    priceRange -> {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(belowPrice == Double.MAX_VALUE, {
                                restorent.belowPrice = Double.MAX_VALUE
                                belowPrice = Double.MAX_VALUE
                            })
                            Text("None", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(belowPrice == 30.0, {
                                restorent.belowPrice = 30.0
                                belowPrice = 30.0
                            })
                            Text("30 Rs Above", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(belowPrice == 100.0, {
                                restorent.belowPrice = 100.0
                                belowPrice = 100.0
                            })
                            Text("100 Rs Above", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(belowPrice == 300.0, {
                                restorent.belowPrice = 300.0
                                belowPrice = 300.0
                            })
                            Text("300 Rs Above", style = MaterialTheme.typography.bodyMedium)
                        }

                    }

                    cuisine -> {
                        val cuisineList = listOf(
                            "Latin American",
                            "Fusion",
                            "British",
                            "Vegan",
                            "North American",
                            "African",
                            "Caribbean",
                            "Chinese",
                            "Italian",
                            "Japanese",
                            "Vietnamese",
                            "French",
                            "Vegetarian",
                            "German",
                            "Spanish",
                            "Korean",
                            "Mexican",
                            "Indian",
                            "Thai",
                            "Greek",
                            "Tamil",
                            "South American",
                            "Mediterranean",
                            "Middle Eastern",
                            "dairy",
                            "mine"
                        )
                        var cuisineSelected by remember {
                            mutableStateOf(cuisineList.map {
                                Filter(
                                    "${Table.FoodInfo.name}.foodType",
                                    it,
                                    false
                                )
                            })
                        }
                        restorent.filterList = cuisineSelected.filter { it.enabled }

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())) {
                            cuisineSelected.forEachIndexed { i, item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Checkbox(item.enabled, {
                                        cuisineSelected = cuisineSelected.mapIndexed { j, it ->
                                            if (i == j) it.copy(enabled = !it.enabled) else it
                                        }
                                    })
                                    Text(item.value, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                    }
                }


            }
        }


    }, sheetPeekHeight = 0.dp) {

        var switchSearch by remember {
            mutableStateOf(false)
        }

        AnimatedVisibility(switchSearch){
            FoodGrid({ switchSearch = false }, restorentPickListner) {
                searchField=it
                switchSearch=false
            }
        }


        AnimatedVisibility(!switchSearch) {
            LazyColumn {

                //Location
                stickyHeader {
                    Column(Modifier.background(Color.White).fillMaxWidth()) {

                        //Address Location
                        Row(Modifier.fillMaxWidth().padding(5.dp).clickable { scope.launch { scaffoldState.bottomSheetState.expand();showLocationPicker = true } }) {
                            Icon(Icons.Default.LocationOn, "", Modifier.size(25.dp), tint =MaterialTheme.colorScheme.primary)
                            Column(Modifier.fillMaxWidth(0.85f)) {
                                Row(Modifier.fillMaxWidth().padding(start = 8.dp)) {
                                    address?.let {
                                        Text(it.pincode, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        "openLocationPicker",
                                        Modifier.size(25.dp)
                                    )
                                }
                                address?.let {
                                    Text(
                                        it.address,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 8.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            IconButton({}) {
                                supabase.getImage(customerInfo.profileUrl,
                                    Modifier
                                        .clip(CircleShape)
                                        .size(50.dp), ContentScale.FillBounds)
                                // Image(Icons.Default.Search, "Profile picture", Modifier.size(50.dp))
                            }
                        }

                        //Search
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp), horizontalArrangement = Arrangement.Start,verticalAlignment = Alignment.CenterVertically) {
                            AssistChip(
                                {
                                    showFilter =
                                        true;scope.launch { scaffoldState.bottomSheetState.expand() }
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                label = {
                                    Icon(
                                        painterResource(R.drawable.filter),
                                        "",
                                        Modifier.size(20.dp, 40.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                            )
                        Spacer(Modifier.height(10.dp))
                            AssistChip(
                                { switchSearch = true },
                                {
                                    Column(
                                        Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        if (searchField.isEmpty()) Text(
                                            "Search Your Dishes",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.DarkGray
                                        ) else Text(searchField)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(.70f)
                                    .height(40.dp)
                                    .padding(horizontal = 20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Search,
                                        "",
                                        Modifier.size(25.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {})

                            Button(
                                {
                                    switchVegMode = !switchVegMode
                                    RestaurantViewModel.vegOnly = switchVegMode
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.width(100.dp)
                            ) {
                                if (switchVegMode) Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Non Veg", style = MaterialTheme.typography.bodySmall)
                                    //  Image(painter = painterResource(R.drawable.veg), "", Modifier.size(50.dp))
                                }
                                else Row(verticalAlignment = Alignment.CenterVertically) {
                                    /// Icon(painter = painterResource(R.drawable.non_veg), "", Modifier.size(35.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text("Veg", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                        }

                        //Filter
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Spacer(Modifier.width(25.dp))


                            val fontthisrow=MaterialTheme.typography.bodySmall

                            AssistChip({
                                newest_bool = !newest_bool
                                restorent.enableNewest(newest_bool)
                            }, { Text("Newest",style = fontthisrow) }, trailingIcon = {
                                if (newest_bool) Icon(Icons.Default.Check, "")
                            })

                            AssistChip({
                                quick_delivery_bool = !quick_delivery_bool
                                restorent.enableQuickDelivery(quick_delivery_bool)
                            }, { Text("Quick Delivery",style = fontthisrow) }, trailingIcon = {
                                if (quick_delivery_bool) Icon(Icons.Default.Check, "")
                            })

                            AssistChip({
                                showAvailable = !showAvailable
                                restorent.enableAvailable(showAvailable)
                                food.enableAvailable(showAvailable)
                            }, { Text("Available",style = fontthisrow) }, trailingIcon = {
                                if (showAvailable) Icon(Icons.Default.Check, "")
                            })

                            AssistChip({
                                if(aboveThreeStar!=3.0) {
                                    aboveThreeStar = 3.0
                                    restorent.aboveTheStar=3.0
                                }
                                else{
                                    aboveThreeStar=-1.0
                                    restorent.aboveTheStar=-1.0
                                }

                            }, { Text("Above 3 Stars Hotel",style = fontthisrow) }, trailingIcon = {
                                if (aboveThreeStar==3.0) Icon(Icons.Default.Check, "")
                            })
                            Spacer(Modifier.width(25.dp))
                        }

                        Row(
                            Modifier
                                .scale(1.0f)
                                .fillMaxWidth()
                                .height(35.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                10.dp,
                                alignment = Alignment.End
                            )
                        ) {
                            IconButton({
                                settingsListner(screen.orders)
                            }) {
                                Icon(Icons.Default.ShoppingCart, "Cart")
                            }
                            if (false) IconButton({
                                settingsListner(screen.favourite)
                            }) {
                                Icon(Icons.Default.FavoriteBorder, "Cart")
                            }
                        }

                        Spacer(Modifier.height(30.dp))
                    }

                }



                //Restorer Item
                item {
                    Text("items " + restorent.list.size.toString())
                    if (address != null) if (restorent.list.isNotEmpty()) restorent.list.forEach {
                        customerItemView(
                            it,
                            restorentPickListner
                        )
                    }
                    else Text("Please select location")

                }


            }
        }

    }

}



@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(localDb: LocalDb, customerInfo: CustomerInfo, settingsListner: (screen) -> Unit, restorentPickListner: (RestaurantInfo) -> Unit ){
    var searchField by remember { mutableStateOf("") }
    var switchVegMode by remember { mutableStateOf(false) }
    var newest_bool by remember { mutableStateOf(false) }
    var quick_delivery_bool by remember {
        mutableStateOf(false)
    }

    var address by remember {
        mutableStateOf<Address?>(localDb.gerLocation())
    }

    val restorent = viewModel<RestaurantViewModel>()
    restorent.search(searchField)

    var showAvailable by remember {
        mutableStateOf(false)
        //isAvailable
    }

    var aboveThreeStar by remember {
        mutableStateOf(-1.0)
    }

    var belowPrice by remember {
        mutableStateOf(Double.MAX_VALUE)
    }

    val food= viewModel<FoodViewModel>()

    address?.let {
        restorent.pincode=it.pincode
    }

    val scope= rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showLocationPicker by remember {
        mutableStateOf(false)
    }
    var showFilter by remember {
        mutableStateOf(false)
    }

    var switchSearch by remember {
        mutableStateOf(false)
    }

    //ViewModels
   // val food= viewModel<FoodViewModel>()

    LaunchedEffect(key1 = Unit){
        restorent.recommended(0L..10L)
    }

    restorent.pincode=address?.pincode?:""

    LazyColumn {



        //Location
        item {
            Spacer(Modifier.height(50.dp))
            //Address Location
            Row(Modifier.fillMaxWidth().padding(5.dp).clickable { //scope.launch { scaffoldState.bottomSheetState.expand();showLocationPicker = true }
            }, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, "", Modifier.size(35.dp), tint = MaterialTheme.colorScheme.primary)

                Column(Modifier.fillMaxWidth(0.85f)) {

                    address?.let {
                        Text(it.pincode, style = MaterialTheme.typography.titleLarge)
                        Text(it.address, style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth().padding(end = 8.dp), maxLines = 1)

                    }



                    }

                Text(customerInfo.name.substring(0,1), Modifier.drawBehind {
                    // Calculate center coordinates
                    val centerX = size.width / 2
                    val centerY = size.height / 2-5f

                    // Draw the circle
                    drawCircle(color = Color(0xFFB2EBF2), radius = 50f, // Adjust radius as needed
                        center = Offset(centerX, centerY)
                    )

                    // Draw the text content

                }.size(50.dp).padding(5.dp), softWrap =false, style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center))
            }
        }

        item {
            Spacer(Modifier.height(10.dp))
            Image(painterResource(R.drawable.sample_banner),"",Modifier.padding(10.dp).clip(MaterialTheme.shapes.medium).fillMaxWidth().height(150.dp),contentScale = ContentScale.FillBounds)
        }


        item {
      //    val list = listOf(R.drawable.common_food1,R.drawable.common_food2,R.drawable.common_food3,R.drawable.common_food4,R.drawable.common_food5,R.drawable.common_food6,R.drawable.common_food7,R.drawable.common_food8,R.drawable.common_food9,R.drawable.common_food10,R.drawable.common_food11,R.drawable.common_food12)
            val style=MaterialTheme.typography.displaySmall
            var resizeableText by remember { mutableStateOf(style) }


            Text("What's On Your Mind?",modifier = Modifier.width(400.dp),softWrap = false, onTextLayout = {result->
                if(result.didOverflowWidth){
                    resizeableText=resizeableText.copy(fontSize = resizeableText.fontSize*0.9)
                }
            }, style = resizeableText)

//            LazyHorizontalGrid(GridCells.Fixed(2),Modifier.height(250.dp)) {
//                list.forEach {
//                    item {
//                        //                supabase.getImage(
////                    it.imageUrl,
////                    Modifier
////                        .clip(MaterialTheme.shapes.extraSmall)
////                        .fillMaxSize(),
////                    ContentScale.Crop
////                )
//                        //  Text(it.name)
//                        Image(
//                            painterResource(it),
//                            "",
//                            Modifier.padding(5.dp).clip(MaterialTheme.shapes.extraSmall)
//                                .size(100.dp, 100.dp).padding(10.dp).clickable {
//                                    // pickFood(it.name)
//                                },
//                            contentScale = ContentScale.FillBounds
//                        )
//
//                    }
//                }
//            }



        }

        stickyHeader {
            Box(Modifier.fillMaxWidth().height(50.dp).background(Color.White))



            Column(Modifier.background(Color.White).fillMaxWidth()) {

                //Search
                Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Start,verticalAlignment = Alignment.CenterVertically) {
                    AssistChip({
                      showFilter = true;scope.launch { scaffoldState.bottomSheetState.expand() }
                    }, border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), label = { Icon(painterResource(R.drawable.filter), "", Modifier.size(20.dp, 40.dp), tint = MaterialTheme.colorScheme.primary) },
                    )
                    Spacer(Modifier.height(10.dp))
                    AssistChip(
                        {
                            switchSearch = true
                        },
                        {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                                if (searchField.isEmpty()) Text("Search Your Dishes", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) else Text(searchField)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(.70f).height(40.dp).padding(horizontal = 20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        leadingIcon = {
                            Icon(Icons.Filled.Search, "Search", Modifier.size(25.dp), tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {

                        })

                    Column {
                        Switch(switchVegMode, {
                            switchVegMode = !switchVegMode
                            //   RestaurantViewModel.vegOnly = switchVegMode

                        }, Modifier.width(50.dp).height(30.dp), thumbContent = {
                            Icon(Icons.Default.Check, "", Modifier.size(15.dp))
                        }, colors = SwitchDefaults.colors(uncheckedBorderColor = Color.White))

                        Text("Veg Only", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)

                    }

                    Row(Modifier.fillMaxWidth().height(35.dp), horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.End)) {
                        IconButton({
                               settingsListner(screen.orders)
                        }) {
                            Icon(Icons.Default.ShoppingCart, "Cart", tint =MaterialTheme.colorScheme.primary)
                        }
                        if (false) IconButton({
                             settingsListner(screen.favourite)
                        }) {
                            Icon(Icons.Default.FavoriteBorder ,"Cart", tint =MaterialTheme.colorScheme.primary)
                        }
                    }



                }

                //Filter
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Spacer(Modifier.width(5.dp))


                    val fontthisrow=MaterialTheme.typography.bodySmall

                    AssistChip({
                          newest_bool = !newest_bool
                           restorent.enableNewest(newest_bool)
                    }, { Text("Newest",style = fontthisrow) }, trailingIcon = {
                        if (newest_bool) Icon(Icons.Default.Check, "")
                    })

                    AssistChip({
                         quick_delivery_bool = !quick_delivery_bool
                         restorent.enableQuickDelivery(quick_delivery_bool)
                    }, { Text("Quick Delivery",style = fontthisrow) }, trailingIcon = {
                        if (quick_delivery_bool) Icon(Icons.Default.Check, "")
                    })

                    AssistChip({
                         showAvailable = !showAvailable
                         restorent.enableAvailable(showAvailable)
                         food.enableAvailable(showAvailable)
                    }, { Text("Available",style = fontthisrow) }, trailingIcon = {
                          if (showAvailable) Icon(Icons.Default.Check, "")
                    })

                    AssistChip({
                        if(aboveThreeStar!=3.0) {
                            aboveThreeStar = 3.0
                            restorent.aboveTheStar=3.0
                        }
                        else{
                            aboveThreeStar=-1.0
                            restorent.aboveTheStar=-1.0
                        }

                    }, { Text("Above 3 Stars Hotel",style = fontthisrow) }, trailingIcon = {
                         if (aboveThreeStar==3.0) Icon(Icons.Default.Check, "")
                    })
                    Spacer(Modifier.width(25.dp))
                }

                Spacer(Modifier.height(30.dp))
            }

        }



        item {
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                HorizontalDivider(Modifier.fillMaxWidth())
                Text("Recommended", modifier = Modifier.fillMaxWidth(0.45f).background(Color.Unspecified).padding(10.dp),
                    style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center))
            }


            LazyHorizontalGrid(GridCells.Fixed(2),Modifier.height(500.dp)) {
                restorent.recommendlist.forEach {
                    item {
                        it.HotelItemDisplaySmall(restorentPickListner)
                    }
                }
            }


        }


        item {

            Text("All Hotels", modifier = Modifier.fillMaxWidth().padding(10.dp)
                .drawBehind {
                    // Calculate line coordinates
                    val lineY = size.height / 2 // Center vertically
                    val lineStartX = 0f
                    val lineEndX = size.width
                    // Draw the line
                    drawLine(color = Color.LightGray, // Adjust color as needed
                        start = Offset(lineStartX, lineY),
                        end = Offset(lineEndX, lineY),
                        strokeWidth = 2f // Adjust stroke width as needed
                    )
                },
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
            )

            if (address != null) if (restorent.list.isNotEmpty()) restorent.list.forEach {
                it.HotelItemDisplay(restorentPickListner)
            }
            else Text("Please select location")

        }


    }
}





@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun trail(){
    Dialog({}) {
        Column(
            Modifier
                .background(Color.White, MaterialTheme.shapes.medium)
                .width(450.dp)
                .height(500.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton({}){
                    Icon(Icons.Default.Close,"Close Action")
                }
                }

                    Column (
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth(),  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        var newest_bool by remember {
                            mutableStateOf(false)
                            //updated_at
                        }
                        var quick_delivery_bool by remember {
                            mutableStateOf(false)
                            //estimated delivery time
                        }
                        var showAvailable by remember {
                            mutableStateOf(false)
                            //isAvailable
                        }
                        var aboveThreeStar by remember {
                            mutableStateOf(false)
                        }

                        val modifier= Modifier
                            .width(300.dp)
                            .height(30.dp)
                            .padding(start = 10.dp)
                        val style=MaterialTheme.typography.bodySmall
                        val rangeSliderState=RangeSliderState(0f,8f,100)




                                AssistChip({
                                    newest_bool = !newest_bool
                                    // restorent.enableNewest(newest_bool)
                                }, { Text("Price", style = style) }, trailingIcon = {
                                    if (newest_bool) Icon(Icons.Default.Check, "")
                                    else RangeSlider(rangeSliderState,Modifier.fillMaxWidth(0.70f))
                                }, modifier = modifier)



                                AssistChip({
                                    quick_delivery_bool = !quick_delivery_bool
                                    //  restorent.enableQuickDelivry(quick_delivery_bool)
                                }, { Text("Quick Delivery", style = style) }, trailingIcon = {
                                    if (quick_delivery_bool) Icon(Icons.Default.Check, "")
                                }, modifier = modifier)


                                AssistChip({
                                    showAvailable = !showAvailable
                                    //restorent.enableAvailable(showAvailable)
                                    // food.enableAvailable(showAvailable)
                                }, { Text("Available", style = style) }, trailingIcon = {
                                    if (showAvailable) Icon(Icons.Default.Check, "")
                                }, modifier = modifier)

                        
                                AssistChip({
                                    aboveThreeStar = !aboveThreeStar
                                    // restorent.enableAbove3Star(aboveThreeStar)
                                }, { Text("Above 3 Stars Hotel", style = style) }, trailingIcon = {
                                    if (aboveThreeStar) Icon(Icons.Default.Check, "")
                                }, modifier = modifier)



                }
        }

    }

}

//Optimize this will be
@Composable
fun LocationPicker(customerInfo: CustomerInfo, closeAction: () -> Unit, pickAddress: (Address) -> Unit, ) {
    val scope= rememberCoroutineScope()
    var address= viewModel<RestaurantViewModel>()



    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer).verticalScroll(rememberScrollState()),horizontalAlignment = Alignment.CenterHorizontally) {

        var searchField by remember {
            mutableStateOf("")
        }
        var switchCustomerAddress by remember {
            mutableStateOf(true)
        }

        Row {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(switchCustomerAddress, {
                    switchCustomerAddress = true
                })
                Text("Customer Address", style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(!switchCustomerAddress, {
                    switchCustomerAddress = false
                })
                Text("Other location", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Row(Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.Start) {
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(searchField,{searchField=it}, leadingIcon = { Icon(Icons.Default.Search,"Search",Modifier.size(30.dp),tint = MaterialTheme.colorScheme.primary) }, shape = MaterialTheme.shapes.medium, colors = TextFieldDefaults.colors(focusedContainerColor = Color.White,unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent,unfocusedIndicatorColor = Color.Transparent ), maxLines =2, textStyle =MaterialTheme.typography.bodySmall , modifier = Modifier.fillMaxWidth(0.75f).height(50.dp))
            IconButton({closeAction()}){
                Icon(Icons.Default.LocationOn,"Close Action",Modifier.size(30.dp),tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(10.dp))
            IconButton({closeAction()}){
                Icon(Icons.Default.Close,"Close Action")
            }
        }



        Spacer(Modifier.height(10.dp))

        if (!switchCustomerAddress) address.unchangedList.filter {
            Pattern.compile(searchField).matcher(it.address.address).find() || Pattern.compile(
                searchField
            ).matcher(it.address.pincode).find()
        }.forEach {
            Column(Modifier.padding(15.dp)
                    .clickable {
                        pickAddress(it.address)
                        closeAction()
                    }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(it.address.pincode,style = MaterialTheme.typography.bodyMedium)
                    Text(it.address.geoLocation,style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(10.dp))
                Text(it.address.address,style = MaterialTheme.typography.bodyLarge)
            }

        }
        else customerInfo.address.filter {
            Pattern.compile(searchField).matcher(it.address).find() || Pattern.compile(searchField)
                .matcher(it.pincode).find()
        }.forEach {
            Column(Modifier.padding(15.dp).clickable {
                        pickAddress(it)
                        closeAction()
                    }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(it.address, style = MaterialTheme.typography.bodyMedium)
                    Text(it.pincode, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(10.dp))
                Text(it.geoLocation, style = MaterialTheme.typography.bodyLarge)
            }

        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun customerItemView(restaurant: RestaurantInfo,restaurantPickListner:(RestaurantInfo)->Unit) {

    var foodlist by remember {
        mutableStateOf(listOf<FoodInfo>())
    }

    LaunchedEffect(Unit) {
        foodlist = supabase.from(Table.FoodInfo.name).select {
            filter {
                eq("restaurantChannelId", restaurant.channel_id ?: "")
            }
        }.decodeList()
    }

    Card(modifier = Modifier.fillMaxWidth().height(300.dp).padding(8.dp).clickable { restaurantPickListner(restaurant) }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(Modifier.fillMaxWidth()) {

            Box(Modifier.fillMaxWidth(0.80f).fillMaxHeight(), contentAlignment = Alignment.BottomStart) {
                supabase.getImage(restaurant.imageUrl, Modifier.fillMaxWidth().height(300.dp), ContentScale.FillBounds)

                Text(restaurant.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(10.dp))
            }

            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {

            }
        }
    }


//    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { restaurantPickListner(restaurant) }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            // Image
//
//            Text(restaurant.id.toString())
//
//
//            Image(painter = painterResource(R.drawable.main_screen), contentDescription = "${restaurant.name} Image", modifier = Modifier.fillMaxWidth().height(200.dp).clip(
//                RoundedCornerShape(8.dp)
//            ))
//
//            // Name and Rating Row
//            Row(modifier = Modifier.fillMaxWidth()
//                .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = restaurant.name,
//                    style = MaterialTheme.typography.bodySmall,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = "${restaurant.rating} ★",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "(${restaurant.numberOfRatings})",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Gray
//                    )
//                }
//            }
//
//            // Address
//            Text(
//                text = restaurant.address.toString(),
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Gray,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//
//            // Cuisine List
//            Text(
//                text = restaurant.cuisine.joinToString(", "),
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//
//            // Estimated Delivery Time and Availability
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Delivery in ${restaurant.estimatedDeliveryTime} mins",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//
//                Text(
//                    text = if (restaurant.isAvailable== Availability.Available) "Available" else "Closed",
//                    color = if (restaurant.isAvailable== Availability.Available) Color.Green else Color.Red,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HotelItemDisplay(restaurant: RestaurantInfo, restaurantPickListner: (RestaurantInfo) -> Unit) {

    var foodlist by remember {
        mutableStateOf(listOf<FoodInfo>())
    }

    LaunchedEffect(Unit) {
        foodlist = supabase.from(Table.FoodInfo.name).select {
            filter {
                eq("restaurantChannelId", restaurant.channel_id ?: "")
            }
        }.decodeList()
    }

    Card {
        ConstraintLayout(
            Modifier
                .padding(10.dp)
                .clip(MaterialTheme.shapes.large)
                .background(Color.Yellow)
                .fillMaxWidth()
                .height(330.dp)
        ) {
            val (favorate, textContainer, hide, hoteldistance, price) = createRefs()
            HorizontalPager(rememberPagerState { foodlist.size }) {
                supabase.getImage(
                    foodlist[it].imageUrl,
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
            //Gps Avaiable
            Text(
                "1.5 km",
                Modifier
                    .zIndex(3F)
                    .padding(1.dp)
                    .constrainAs(hoteldistance) {
                        bottom.linkTo(
                            textContainer.top,
                            -10.dp
                        );end.linkTo(parent.end, -5.dp)
                    }
                    .background(Color.White, MaterialTheme.shapes.small)
                    .padding(5.dp)
            )

            Icon(Icons.Outlined.FavoriteBorder, "Favorites",
                Modifier
                    .constrainAs(favorate) {
                        top.linkTo(parent.top, 10.dp)
                        end.linkTo(parent.end, 10.dp)
                    }
                    .size(30.dp), tint = Color.White)

            Column(Modifier.constrainAs(textContainer) { bottom.linkTo(parent.bottom, 0.dp) }.background(Color.White).fillMaxWidth().height(130.dp)) {
                Row(Modifier.padding(top = 15.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    Text(restaurant.name, maxLines = 2, modifier = Modifier.fillMaxWidth(0.80f).padding(start = 10.dp), style = MaterialTheme.typography.headlineMedium)

                    Text("${restaurant.rating} Star", Modifier.padding(5.dp).background(color = Color(0xFF00897B), MaterialTheme.shapes.extraSmall).padding(horizontal = 3.dp, vertical = 2.dp), color = Color.White, style = MaterialTheme.typography.titleMedium)

                }
                Row(Modifier.padding(top = 10.dp, start = 10.dp)) { Text("Pure Veg");Text("South Indian");Text("Starts at") }
                Text("${restaurant.estimatedDeliveryTime} mins", Modifier.padding(10.dp), style = MaterialTheme.typography.titleMedium)

            }
        }
    }

}

//@Composable
//fun customerItemView(restaurant: RestaurantInfo,restaurantPickListner:(RestaurantInfo)->Unit) {
//    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { restaurantPickListner(restaurant) }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            // Image
//
//            Text(restaurant.id.toString())
//
//
//            Image(painter = painterResource(R.drawable.main_screen), contentDescription = "${restaurant.name} Image", modifier = Modifier.fillMaxWidth().height(200.dp).clip(
//                RoundedCornerShape(8.dp)
//            ))
//
//            // Name and Rating Row
//            Row(modifier = Modifier.fillMaxWidth()
//                .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = restaurant.name,
//                    style = MaterialTheme.typography.bodySmall,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = "${restaurant.rating} ★",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "(${restaurant.numberOfRatings})",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Gray
//                    )
//                }
//            }
//
//            // Address
//            Text(
//                text = restaurant.address.toString(),
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Gray,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//
//            // Cuisine List
//            Text(
//                text = restaurant.cuisine.joinToString(", "),
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//
//            // Estimated Delivery Time and Availability
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Delivery in ${restaurant.estimatedDeliveryTime} mins",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//
//                Text(
//                    text = if (restaurant.isAvailable== Availability.Available) "Available" else "Closed",
//                    color = if (restaurant.isAvailable== Availability.Available) Color.Green else Color.Red,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
//}

@Composable
fun customerItemView() {
    val restaurant=RestaurantInfo.initialRestaurantInfo.copy(name ="Hello" )
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            // restaurantPickListner(restaurant)
        }, elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image

            Text(restaurant.id.toString())


            Image(painter = painterResource(R.drawable.main_screen), contentDescription = "${restaurant.name} Image", modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                ))

            // Name and Rating Row
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${restaurant.rating} ★",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${restaurant.numberOfRatings})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // Address
            Text(
                text = restaurant.address.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Cuisine List
            Text(
                text = restaurant.cuisine.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Estimated Delivery Time and Availability
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery in ${restaurant.estimatedDeliveryTime} mins",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = if (restaurant.isAvailable== Availability.Available) "Available" else "Closed",
                    color = if (restaurant.isAvailable== Availability.Available) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun FoodGrid(closeAction: () -> Unit, restorentPickListner: (RestaurantInfo) -> Unit, pickFood: (String) -> Unit, ) {
    var list by remember { mutableStateOf(listOf<CommonFoodInfo>()) }
    var restaurant = viewModel<RestaurantViewModel>()
    var commonFood = viewModel<CommonFoodViewModel>()

    LaunchedEffect(key1 = true) {
        list = supabase.postgrest.from(Table.CommonFoodInfo.name).select().decodeList()
    }

    Column(
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize()) {
        var search by remember { mutableStateOf("") }
        Spacer(Modifier.height(25.dp))
        Row(Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.Start) {
            Spacer(Modifier.width(10.dp))

            OutlinedTextField(search, { search = it }, placeholder = { Text("Search Your Food Or Hotel", color = Color.LightGray, style = MaterialTheme.typography.bodySmall) }, leadingIcon = { Icon(Icons.Default.Search, "Search", Modifier.size(30.dp), tint = MaterialTheme.colorScheme.primary) }, shape = MaterialTheme.shapes.small, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.primary), maxLines = 2, textStyle = MaterialTheme.typography.bodySmall, modifier = Modifier.scale(0.80f).fillMaxWidth(0.75f).height(50.dp))

            Spacer(Modifier.width(10.dp))
            IconButton({ closeAction() }) {
                Icon(Icons.Default.Close, "Close Action")
            }
        }

        var foodOn by remember {
            mutableStateOf(true)
        }


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(25.dp), verticalAlignment = Alignment.CenterVertically) {

            Text("Food", Modifier.clickable { foodOn = !foodOn; }.width(80.dp).border(1.dp, if (foodOn) MaterialTheme.colorScheme.primary else Color.Transparent, MaterialTheme.shapes.medium).padding(5.dp), style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center))

            Text(
                "Hotel",
                Modifier
                    .clickable { foodOn = false }
                    .width(80.dp)
                    .border(
                        1.dp,
                        if (!foodOn) MaterialTheme.colorScheme.primary else Color.Transparent,
                        MaterialTheme.shapes.medium
                    )
                    .padding(5.dp),
                style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center)
            )
        }

        if (foodOn) LazyColumn {
            item {
                FlowRow(
                    Modifier
                        .padding(10.dp)
                        .fillMaxWidth(), maxItemsInEachRow = 3) {
                    commonFood.list.filter { Pattern.compile(search).matcher(it.name).find() }
                        .forEach {
                            Card(
                                {
                                    pickFood(it.name)
                                },
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth(.33f)
                                    .height(100.dp)
                                    .padding(vertical = 5.dp, horizontal = 20.dp)
                            ) {
                                supabase.getImage(
                                    it.imageUrl,
                                    Modifier
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .fillMaxSize(),
                                    ContentScale.Crop
                                )
                                //  Text(it.name)
                            }
                        }
                }
            }
        }

        if (!foodOn) LazyColumn {
            item {
                restaurant.unchangedList.filter { Pattern.compile(search).matcher(it.name).find() }
                    .forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clickable {
                                    restorentPickListner(it)
                                }) {
                            supabase.getImage(
                                it.imageUrl,
                                Modifier
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .size(100.dp),
                                ContentScale.FillBounds
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(
                                Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(it.name, style = MaterialTheme.typography.bodySmall)
                                //Rating
                                val decimal = it.rating - Math.floor(it.rating)
                                Row(Modifier) {
                                    Text(
                                        it.rating.toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    for (i in 1..Math.floor(it.rating).toInt()) {
                                        Icon(
                                            Icons.Default.Star,
                                            "",
                                            tint = Color.Yellow,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        if (i == Math.floor(it.rating)
                                                .toInt() && decimal != 0.0
                                        ) Icon(
                                            painterResource(R.drawable.half_star),
                                            "",
                                            tint = Color.Yellow,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }

                            }

                        }
                    }
            }

        }


    }
}

///** probulem with this */
//@Composable
//fun Clock(modifier: Modifier = Modifier) {
//    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
//
//    LaunchedEffect(key1 = Unit) {
//        while (true) {
//            delay(1000) // Update every second
//            currentTime = Calendar.getInstance()
//        }
//    }
//
//    Canvas(modifier = modifier.size(200.dp)) {
//        val centerX = size.width / 2
//        val centerY = size.height / 2
//        val radius = size.minDimension / 2 - 20.dp.toPx()
//
//        // Draw clock face
//        drawCircle(Color.LightGray, radius, center = Offset(centerX, centerY))
//
//        // Draw hour hand
//        val hourAngle = (currentTime.get(Calendar.HOUR) * 30 + currentTime.get(Calendar.MINUTE) / 2) * PI / 180
//        drawLine(
//            Color.Black,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.5f * cos(hourAngle)).toFloat(), centerY + (radius * 0.5f * sin(hourAngle)).toFloat()),
//            strokeWidth = 6.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//
//        // Draw minute hand
//        val minuteAngle = currentTime.get(Calendar.MINUTE) * 6 * PI / 180
//        drawLine(
//            Color.Black,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.7f * cos(minuteAngle)).toFloat(), centerY + (radius * 0.7f * sin(minuteAngle)).toFloat()),
//            strokeWidth = 4.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//
//        // Draw second hand
//        val secondAngle = currentTime.get(Calendar.SECOND) * 6 * PI / 180
//        drawLine(
//            Color.Red,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.9f * cos(secondAngle)).toFloat(), centerY + (radius * 0.9f * sin(secondAngle)).toFloat()),
//            strokeWidth = 2.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//    }
//}
//
//@Composable
//fun ClockIcon(modifier: Modifier = Modifier,hour: Int = 12, minute: Int = 0, second: Int = 0) {
//    Canvas(modifier = modifier.size(200.dp).rotate(-45.0f)) {
//        val centerX = size.width / 2
//        val centerY = size.height / 2
//        val radius = size.minDimension / 2 - 20.dp.toPx()
//
//        // Draw clock face
//        drawCircle(Color.Black, radius, center = Offset(centerX, centerY), style = Stroke(width = 1.dp.toPx()))
//
//        // Draw hour hand
//        val hourAngle = (((hour-1) % 12 + (minute-7.5) / 60f) * 30 * PI / 180) // Adjust hour hand based on minutes
//        drawLine(
//            Color.Black,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.5f * cos(hourAngle)).toFloat(), centerY + (radius * 0.5f * sin(hourAngle)).toFloat()),
//            strokeWidth = 2.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//
//        // Draw minute hand
//        val minuteAngle = ((minute-10) * 6 * PI / 180)
//        drawLine(
//            Color.Black,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.7f * cos(minuteAngle)).toFloat(), centerY + (radius * 0.7f * sin(minuteAngle)).toFloat()),
//            strokeWidth = 2.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//
//        // Draw second hand (optional)
//        if (second != 0) {
//            val secondAngle = (second * 6 * PI / 180)
//            drawLine(
//                Color.Red,
//                Offset(centerX, centerY),
//                Offset(centerX + (radius * 0.9f * cos(secondAngle)).toFloat(), centerY + (radius * 0.9f * sin(secondAngle)).toFloat()),
//                strokeWidth = 2.dp.toPx(),
//                cap = StrokeCap.Round
//            )
//        }
//    }
//}
//
//@Composable
//fun ClockIconMinutes(modifier: Modifier = Modifier, minute: Int = 0) {
//    Canvas(modifier = modifier.size(200.dp).rotate(-45.0f)) {
//        val centerX = size.width / 2
//        val centerY = size.height / 2
//        val radius = size.minDimension / 2 - 20.dp.toPx()
//
//        // Draw clock face
//        drawCircle(Color.Black, radius, center = Offset(centerX, centerY), style = Stroke(width = 1.dp.toPx()))
//
//        // Draw hour hand
//        val hourAngle = Math.toRadians(-50.0)
//        drawLine(
//            Color.Black,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.5f * cos(hourAngle)).toFloat(), centerY + (radius * 0.5f * sin(hourAngle)).toFloat()),
//            strokeWidth = 2.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//
//        // Draw minute hand
//        val minuteAngle = ((minute-10) * 6 * PI / 180)
//        drawLine(
//            Color.Black,
//            Offset(centerX, centerY),
//            Offset(centerX + (radius * 0.7f * cos(minuteAngle)).toFloat(), centerY + (radius * 0.7f * sin(minuteAngle)).toFloat()),
//            strokeWidth = 2.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//
//    }
//}

