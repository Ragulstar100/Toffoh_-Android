package Screen

import Ui.data.Address
import Ui.enums.Availability
import android.annotation.SuppressLint
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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

//#f97a7b
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun preview(customerInfo: CustomerInfo,restorentPickListner:(RestaurantInfo)->Unit){

    var switchVegMode by remember {
        mutableStateOf(false)
    }

    var searchField by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf<Address?>(null)
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
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        address=customerInfo.address[0]
        scaffoldState.bottomSheetState.expand()
    }


    // val themeColor=Color(0xFFFf97a7b)

    BottomSheetScaffold(scaffoldState = scaffoldState, sheetContent = {
       if(showLocationPicker) LocationPicker({
               scope.launch {
                   showLocationPicker=false
               }
           }){
               address=it
               showLocationPicker=false
           }
        }, sheetPeekHeight = 0.dp) {

        var switchSearch by remember {
            mutableStateOf(false)
        }

        AnimatedVisibility(switchSearch){
            FoodGrid({
                switchSearch=false
            }){
                searchField=it
                switchSearch=false
            }
        }

        AnimatedVisibility(!switchSearch) {
            LazyColumn {
                //Location
                stickyHeader {
                    Column(Modifier.fillMaxWidth()) {

                        //Address Location
                        Row(Modifier.fillMaxWidth().padding(5.dp).clickable {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                                showLocationPicker = true

                            }
                        }) {
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
                                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            IconButton({}) {
                                supabase.getImage(customerInfo.profileUrl, Modifier.clip(CircleShape).size(50.dp), ContentScale.FillBounds)
                                // Image(Icons.Default.Search, "Profile picture", Modifier.size(50.dp))
                            }
                        }

                        //Search
                        Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Start,verticalAlignment = Alignment.CenterVertically) {
                        AssistChip({ showFilter = true },border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), label = { Icon(painterResource(R.drawable.filter), "", Modifier.size(20.dp,40.dp), tint = MaterialTheme.colorScheme.primary) },)
                        Spacer(Modifier.height(10.dp))
                        AssistChip({ switchSearch = true }, { Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) { if (searchField.isEmpty()) Text("Search Your Dishes", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray) else Text(searchField) } },
                            modifier = Modifier.fillMaxWidth(.80f).height(40.dp).padding(horizontal = 20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            leadingIcon = { Icon(Icons.Filled.Search, "", Modifier.size(25.dp), tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = {
//                                IconButton({
//                                    switchVegMode = !switchVegMode
//                                }) {
//                                    Icon(
//                                        painter = painterResource(R.drawable.veg),
//                                        "",
//                                        Modifier.size(20.dp),
//                                        tint = if (switchVegMode) Color(
//                                            0xFFEF5350
//                                        ) else Color(0xFF66BB6A)
//                                    )
//                                }
                            })
                        }

                        //Filter
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Spacer(Modifier.width(25.dp))

//                            AssistChip(
//                                {
//                                    showFilterDialog = true
//                                },
//                                leadingIcon = {
//                                    Icon(
//                                        painterResource(R.drawable.filter),
//                                        "",
//                                        Modifier.size(20.dp),
//                                        tint = Color.LightGray
//                                    )
//                                },
//                                label = { Text("Filter",style = MaterialTheme.typography.bodyMedium) })

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

                //Bottom Sheet Filter
                item{
                    val (priceRange,starRating,cuisine)=listOf("Price Range","Star Rating","Cuisine")


                    var tab by remember {
                        mutableStateOf(priceRange)
                    }


                    if(showFilter) {
                        Column(Modifier.fillMaxWidth().height(450.dp).padding(10.dp)) {
                           Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(25.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(priceRange,Modifier.clickable { tab=priceRange }.width(80.dp).border(1.dp,if(tab!=priceRange) MaterialTheme.colorScheme.primary else Color.Transparent,MaterialTheme.shapes.medium).padding(5.dp),style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center))
                            Text(starRating,Modifier.clickable { tab=starRating }.width(80.dp).border(1.dp,if(tab!=starRating) MaterialTheme.colorScheme.primary else Color.Transparent,MaterialTheme.shapes.medium).padding(5.dp),style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center))
                            Text(cuisine,Modifier.clickable { tab=cuisine }.width(80.dp).border(1.dp,if(tab!=cuisine) MaterialTheme.colorScheme.primary else Color.Transparent,MaterialTheme.shapes.medium).padding(5.dp),style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center))
                           }
                            when(tab){
                                starRating->{
                                   Row(verticalAlignment = Alignment.CenterVertically) {
                                       RadioButton(aboveThreeStar==3.0, {
                                           restorent.aboveTheStar=3.0
                                           aboveThreeStar=3.0
                                       })
                                       Text("3 Star Above", style = MaterialTheme.typography.bodyMedium)
                                   }
                                   Row(verticalAlignment = Alignment.CenterVertically) {
                                       RadioButton(aboveThreeStar==4.0, {
                                           restorent.aboveTheStar=4.0
                                           aboveThreeStar=4.0
                                       })
                                       Text("4 Star Above", style = MaterialTheme.typography.bodyMedium)
                                   }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(aboveThreeStar==4.5, {
                                            restorent.aboveTheStar=4.5
                                            aboveThreeStar=4.5
                                        })
                                        Text("4.5 Star Above", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                priceRange->{
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(belowPrice==30.0, {
                                            restorent.belowPrice=30.0
                                            belowPrice=30.0
                                        })
                                        Text("3 Star Above", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(aboveThreeStar==4.0, {
                                            restorent.aboveTheStar=4.0
                                            aboveThreeStar=4.0
                                        })
                                        Text("4 Star Above", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(aboveThreeStar==4.5, {
                                            restorent.aboveTheStar=4.5
                                            aboveThreeStar=4.5
                                        })
                                        Text("4.5 Star Above", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                cuisine->Text("4.5 Star Above")
                            }


                        }
                    }

                }

                //Restorer Item
                item {
                    Text("items " + restorent.list.size.toString())
                    if (restorent.list.isNotEmpty()) restorent.list.forEach {
                        customerItemView(it, restorentPickListner)
                    }

                }


            }
        }



        //Filter
//      if(showFilter)  Dialog({}) {
//          Column(Modifier.width(450.dp).background(Color.White)) {
//              Row {
//                  IconButton({
//                      showFilter=false
//                  }) {
//                      Icon(Icons.Default.Close,"Close Action")
//                  }
//
//              }
//
//
//
//              var cuisineFilter by remember {
//                  mutableStateOf(listOf<String>())
//              }
//
//              LazyColumn(Modifier.padding(25.dp)) {
//
//
//                 // star rating
//                  val options = listOf(-1.0, 3.0, 4.0, 4.5)
//                  val optionLabels = listOf("none", "3.0+", "4.0+", "4.5+")
//                  val priceOptions=listOf(Double.MAX_VALUE,30.0,100.0,300.0)
//                  val priceLabels=listOf("none","30","100","300")
//
//
//                // Star range
//                 item {
//                     Text("Rating")
//                     RatingFilter(optionLabels=optionLabels,options=options,selectedRating = aboveThreeStar) {
//                         aboveThreeStar=it
//                         restorent.aboveTheStar=it
//                     }
//
//                 }
//               //Price range
//               item {
//                   Text("Price Range")
//                   RatingFilter(optionLabels=priceLabels,options=priceOptions,selectedRating = belowPrice) {
//                       belowPrice=it
//                       restorent.belowPrice=it
//                   }
//               }
//
////               //Cuisine
////               item {
////                   Text("Select Your Cuisine")
////               }
////
////                   val cuisineGroupedItems = restorent.list.groupBy { it.cuisine }
////
////                                     cuisineGroupedItems.forEach { (category, itemsInCategory) ->
////                      item {
////                          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
////                              Text("$category", Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium)
////                              Text("${itemsInCategory.size}", Modifier.padding(5.dp), style = MaterialTheme.typography.titleMedium)
////                          }
////                      }
//////                      items(itemsInCategory) { item ->
//////                          Row(Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
//////                              Text(item.name)
//////                              Text(item.availableQty.toString())
//////                          }
//////                      }
////                  }
//
//
//
//              }
//
//          }
//        }


    }

}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun trail(){
    Dialog({}) {
        Column(Modifier.background(Color.White,MaterialTheme.shapes.medium).width(450.dp).height(500.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton({}){
                    Icon(Icons.Default.Close,"Close Action")
                }
                }

                    Column (Modifier.horizontalScroll(rememberScrollState()).fillMaxWidth(),  verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                        val modifier=Modifier.width(300.dp).height(30.dp).padding(start = 10.dp)
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
fun LocationPicker(closeAction: ()->Unit,pickAddress: (Address)->Unit){
    val scope= rememberCoroutineScope()
    var address= viewModel<RestaurantViewModel>()



    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer).verticalScroll(rememberScrollState()),horizontalAlignment = Alignment.CenterHorizontally) {

        var searchField by remember {
            mutableStateOf("")
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

      //  OutlinedTextField(searchField,{searchField=it; })


        address.unchangedList.filter { Pattern.compile(searchField).matcher(it.address.address).find() || Pattern.compile(searchField).matcher(it.address.pincode).find() }.forEach {
            Column(Modifier.padding(15.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(it.address.pincode,style = MaterialTheme.typography.bodyMedium)
                    Text(it.address.geoLocation,style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(10.dp))
                Text(it.address.address,style = MaterialTheme.typography.bodyLarge)
            }

        }
    }

}

@Composable
fun customerItemView(restaurant: RestaurantInfo,restaurantPickListner:(RestaurantInfo)->Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { restaurantPickListner(restaurant) }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image

            Text(restaurant.id.toString())


            Image(painter = painterResource(R.drawable.main_screen), contentDescription = "${restaurant.name} Image", modifier = Modifier.fillMaxWidth().height(200.dp).clip(
                RoundedCornerShape(8.dp)
            ))

            // Name and Rating Row
            Row(modifier = Modifier.fillMaxWidth()
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

@Composable
@Preview
fun customerItemView() {
    val restaurant=RestaurantInfo.initialRestaurantInfo.copy(name ="Hello" )
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
       // restaurantPickListner(restaurant)
                                                                    }, elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image

            Text(restaurant.id.toString())


            Image(painter = painterResource(R.drawable.main_screen), contentDescription = "${restaurant.name} Image", modifier = Modifier.fillMaxWidth().height(200.dp).clip(
                RoundedCornerShape(8.dp)
            ))

            // Name and Rating Row
            Row(modifier = Modifier.fillMaxWidth()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodGrid(closeAction: () -> kotlin.Unit,pickFood:(String)->Unit) {
    var list by remember {
        mutableStateOf(listOf<CommonFoodInfo>())
    }
    LaunchedEffect(key1 = true) {
        list = supabase.postgrest.from(Table.CommonFoodInfo.name).select().decodeList()
    }
    Column(Modifier.background(MaterialTheme.colorScheme.primaryContainer).fillMaxSize()) {
        var search by remember { mutableStateOf("") }
        Spacer(Modifier.height(25.dp))
        Row(Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.Start) {
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(search,{search=it}, leadingIcon = { Icon(Icons.Default.Search,"Search",Modifier.size(30.dp),tint = MaterialTheme.colorScheme.primary) }, shape = MaterialTheme.shapes.medium, colors = TextFieldDefaults.colors(focusedContainerColor = Color.White,unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent,unfocusedIndicatorColor = Color.Transparent ), maxLines =2, textStyle =MaterialTheme.typography.bodySmall , modifier = Modifier.fillMaxWidth(0.75f).height(50.dp))

            Spacer(Modifier.width(10.dp))
            IconButton({closeAction()}){
                Icon(Icons.Default.Close,"Close Action")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 2 columns in the grid
            modifier = Modifier.fillMaxHeight()
        ) {
            items(list.filter { java.util.regex.Pattern.compile(search, java.util.regex.Pattern.CASE_INSENSITIVE).matcher(it.name).find() }) { foodInfo ->
                supabase.getImage(
                    foodInfo.imageUrl,
                    Modifier.size(100.dp).padding(5.dp),
                    ContentScale.FillHeight
                )
            }
        }
    }
}