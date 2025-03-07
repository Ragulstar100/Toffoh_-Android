package com.manway.Toofoh.Screen


import Ui.data.Address
import Ui.data.LocationType
import Ui.data.PhoneNumberField
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.manway.Toofoh.ViewModel.SharedViewModel
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.FavInfo
import com.manway.toffoh.admin.data.FoodInfo
import com.manway.toffoh.admin.data.RestaurantInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale

//@Composable
//fun SettingsScreen(_customerInfo: CustomerInfo,tab:MutableState<Int>){
//
//    var customerInfo by remember {
//        mutableStateOf(_customerInfo)
//    }
//
//    LaunchedEffect(Unit){
//        supabase.from(Table.CustomerInfo.name).select {
//            filter {
//                eq("channelId", customerInfo.channelId?:"")
//            }
//        }.decodeSingle<CustomerInfo>().let {
//            customerInfo=it
//        }
//    }
//
//
//    val (none,orders,addressBook) = listOf(0,1,2)
//
//    val scope=rememberCoroutineScope()
//
//   if(none == tab.value) Column(Modifier.fillMaxSize()) {
//        Text("Address Book",
//            Modifier
//                .clickable {
//                    tab.value = addressBook
//                }
//                .padding(10.dp)
//                .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
//                .fillMaxWidth()
//                .padding(10.dp))
//       Text("Orders", Modifier
//               .clickable {
//                   tab.value = orders
//               }
//               .padding(10.dp)
//               .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
//               .fillMaxWidth()
//               .padding(10.dp))
//
//       Text("CustomerInfo")
//    }
//
//    if(orders == tab.value) OrderScreen(customerInfo)
//
//    if(addressBook == tab.value) Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally){
//        Spacer(Modifier.height(10.dp))
//        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.End) {
//            IconButton({
//                tab.value = none
//            }) {
//                Icon(Icons.Default.Close, "")
//            }
//        }
//
//        addressLog(customerInfo.address) {
//            customerInfo=customerInfo.copy(address = it)
//            scope.launch {
//                supabase.from(Table.CustomerInfo.name).upsert(customerInfo.copy(address = it))
//            }
//        }
//    }
//
//}

@Composable
fun Collection(
    sharedViewModel: SharedViewModel,
    _customerInfo: CustomerInfo,
    tab: MutableState<Int>,
    orderItems: MutableState<List<OrderItem>>,
    onOrdered: () -> Unit,
    restaurantPickListener: (RestaurantInfo) -> Unit
) {
    var favList by remember {
        mutableStateOf<List<FavInfo>>(listOf())
    }
    var foodOn by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        try {
            val t = flow<List<FavInfo>> {
                while (true) {
                    val list = supabase.from(Table.FavInfo.name).select {
                        filter {
                            eq("isFavorate", true)
                        }
                    }.decodeList<FavInfo>().filter {
                        try {
                            Json.decodeFromString<FavInfo.ResFav>(it.favId).customerId == (sharedViewModel.liveValue.customerInfo?.channelId
                                ?: "")
                        } catch (_: Exception) {
                            Json.decodeFromString<FavInfo.FoodFav>(it.favId).customerId == (sharedViewModel.liveValue.customerInfo?.channelId
                                ?: "")
                        }
                    }
                    emit(list)

                    delay(1000L)
                }

            }
            t.collect {
                sharedViewModel.favList = it
                favList = it
            }
        } catch (e: Exception) {

        }
    }





    Column(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(50.dp))
        Row {
            TextButton({
                foodOn = false
            }) {
                Text("Hotel")
            }
            TextButton({
                foodOn = true
            }) {
                Text("Foods")
            }
        }




        favList.let {
            if (foodOn) it.filter {
                try {
                    Json.decodeFromString<FavInfo.FoodFav>(it.favId).customerId == _customerInfo.channelId
                } catch (_: Exception) {
                    false
                }
            }.map {
                Json.decodeFromString<FavInfo.FoodFav>(it.favId)
            }.forEach {
                var foodInfo by remember {
                    mutableStateOf<FoodInfo?>(null)
                }
                var restaurentInfo by remember {
                    mutableStateOf<RestaurantInfo?>(null)
                }
                LaunchedEffect(Unit) {
                    foodInfo = supabase.from(Table.FoodInfo.name).select {
                        filter {
                            eq("id", it.foodId ?: 0)
                        }
                    }.decodeSingle()
                    restaurentInfo = supabase.from(Table.RestaurantInfo.name).select {
                        filter {
                            eq("channel_id", foodInfo?.restaurantChannelId ?: "")
                        }
                    }.decodeSingle()
                }
                restaurentInfo?.let {

                    foodInfo?.FoodItemDisplay(
                        customerInfo = _customerInfo,
                        it,
                        orderItems = orderItems,
                        {})
                }
            }
            else it.filter {
                try {
                    Json.decodeFromString<FavInfo.ResFav>(it.favId).customerId == _customerInfo.channelId
                } catch (e: Exception) {
                    Log.e("Exception", "OrderScreen", e)
                    false
                }
            }.map {
                Json.decodeFromString<FavInfo.ResFav>(it.favId)
            }.forEach {

                var resInfo by remember {
                    mutableStateOf<RestaurantInfo?>(null)
                }

                LaunchedEffect(Unit) {
                    resInfo = supabase.from(Table.RestaurantInfo.name).select {
                        filter {
                            eq("channel_id", it.restaurantId ?: "")
                        }
                    }.decodeSingle()
                }

                resInfo?.HotelItemDisplay(sharedViewModel, _customerInfo) {
                    restaurantPickListener(it)
                }
            }
        }

        if (foodOn) if (orderItems.value.map { it.quantity }.sum() > 0) Row(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
                .padding(10.dp)
        ) {
            Text(
                "Added Items ${orderItems.value.map { it.quantity }.sum()}",
                Modifier.fillMaxWidth(0.55f)
            )
            Button({
                sharedViewModel.orders = orderItems.value.filter { it.quantity > 0 }
                onOrdered()
            }, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                Text("Cost ${orderItems.value.map { it.total() }.sum()}")
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addressLog(
    _addressList: List<Address>,
    onAddressChanged: (List<Address>) -> Unit,
    onPickListener: (Address) -> Unit
) {
    var addressList by remember {
        mutableStateOf(_addressList)
    }
    var addressIndex by remember {
        mutableStateOf<Int>(-1)
    }
    var (none, addressEditer) = listOf(0, 1)
    var openAddressBottomSheet by remember { mutableStateOf(none) }
    var bottomSheetState = rememberModalBottomSheetState { true }

    LaunchedEffect(addressList) {
        onAddressChanged(addressList)
    }



    LaunchedEffect(openAddressBottomSheet) {
        bottomSheetState.expand()
    }



    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        addressList.forEachIndexed { i, it ->
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        when (it.address.locationType) {
                            LocationType.Home.name -> Icons.Default.Home
                            LocationType.Work.name -> Icons.AutoMirrored.Filled.KeyboardArrowRight
                            LocationType.Office.name -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
                            else -> Icons.Default.Add
                        },
                        "",
                        tint = when (it.address.locationType) {
                            LocationType.Home.name -> Color(0xFFF48FB1)
                            LocationType.Work.name -> Color(0xFF80CBC4)
                            LocationType.Office.name -> Color(0xFFBD8E02)
                            else -> Color(0xFFFFCC80)
                        },
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color(0xFFE1BEE7), CircleShape)
                            .padding(5.dp)
                    )
                    Column(
                        Modifier.fillMaxWidth(0.9f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            it.address.location,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            it.address.doorNumber + it.address.landmark + it.address.others,
                            maxLines = 1,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
                HorizontalDivider(Modifier
                    .fillMaxWidth()
                    .padding(20.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(25.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton({
                        addressIndex = i
                        openAddressBottomSheet = addressEditer
                    }) {
                        Text("Edit")
                    }
                    if (CustomerInfo.addressPickable) TextButton({
                        onPickListener(it)
                    }) {
                        Text("Pick")
                    }
                }
            }
        }

        TextButton({
            addressList = addressList + Address.intial

        }) {
            Text("Add")
        }

    }

    if (openAddressBottomSheet != none) ModalBottomSheet({
        openAddressBottomSheet = none
    }, sheetState = bottomSheetState) {

        AddressBookBottomSheet(addressList[addressIndex], {
            if (it) addressList = addressList.filterIndexed { j, item -> addressIndex != j }
            openAddressBottomSheet = none
        }) {
            addressList = addressList.mapIndexed { j, item -> if (addressIndex == j) it else item }
        }
    }
}


@Composable
fun AddressBookBottomSheet(
    _address: Address,
    onCloseAction: (Boolean) -> Unit,
    onAddressChanged: (Address) -> Unit
) {
    var address by remember {
        mutableStateOf(_address)
    }


    var extendedAddress by remember {
        mutableStateOf(_address.address)
    }

    LaunchedEffect(extendedAddress) {
        onAddressChanged(address.copy(extendedAddress))
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("Address", fontSize = 22.sp, modifier = Modifier.padding(5.dp))
            IconButton({
                onCloseAction(true)
            }) {
                Icon(Icons.Default.Delete, "")
            }
            IconButton({
                onCloseAction(false)
            }) {
                Icon(Icons.Default.Close, "")
            }
            Spacer(Modifier.width(10.dp))
        }
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (_doorNo, _location, _locationType, _phoneNumper, _landMark, _others) = createRefs()
            OutlinedTextField(extendedAddress.doorNumber, {
                extendedAddress = extendedAddress.copy(doorNumber = it)
            }, Modifier
                .constrainAs(_doorNo) {
                    top.linkTo(parent.top, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                }
                .fillMaxWidth(0.25f), label = { Text("Door No") }, shape = MaterialTheme.shapes.small)

            OutlinedTextField(extendedAddress.location, {
                extendedAddress = extendedAddress.copy(location = it)
            }, Modifier
                .constrainAs(_location) {
                    top.linkTo(_doorNo.top)
                    bottom.linkTo(_doorNo.bottom)
                    start.linkTo(_doorNo.end, 10.dp)
                }
                .fillMaxWidth(0.65f), label = { Text("location") }, shape = MaterialTheme.shapes.small)

            Row(
                Modifier.constrainAs(_locationType) { top.linkTo(_doorNo.bottom, 10.dp) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                if (listOf(
                        LocationType.Home.name,
                        LocationType.Work.name,
                        LocationType.Office.name
                    ).contains(extendedAddress.locationType)
                ) {
                    FilterChip(extendedAddress.locationType == LocationType.Home.name, {
                        extendedAddress =
                            extendedAddress.copy(locationType = LocationType.Home.name)
                    }, leadingIcon = {
                        Icon(Icons.Default.Home, "")
                    }, label = { Text(LocationType.Home.name) })
                    Spacer(Modifier.width(10.dp))
                    FilterChip(extendedAddress.locationType == LocationType.Work.name, {
                        extendedAddress =
                            extendedAddress.copy(locationType = LocationType.Work.name)
                    }, leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "")
                    }, label = { Text(LocationType.Work.name) })
                    Spacer(Modifier.width(10.dp))

                    FilterChip(extendedAddress.locationType == LocationType.Office.name, {
                        extendedAddress =
                            extendedAddress.copy(locationType = LocationType.Office.name)
                    }, leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "")
                    }, label = { Text(LocationType.Office.name) })
                }
                Spacer(Modifier.width(10.dp))

                FilterChip(extendedAddress.locationType == LocationType.Other.name, {
                    extendedAddress = extendedAddress.copy(
                        locationType = if (listOf(
                                LocationType.Home.name,
                                LocationType.Work.name,
                                LocationType.Office.name
                            ).contains(extendedAddress.locationType)
                        ) LocationType.Other.name else LocationType.Home.name
                    )

                }, leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "")
                }, label = { Text(LocationType.Other.name) }
                )
                Spacer(Modifier.width(10.dp))
                if (!listOf(
                        LocationType.Home.name,
                        LocationType.Work.name,
                        LocationType.Office.name
                    ).contains(extendedAddress.locationType)
                ) {
                    TextField(extendedAddress.locationType, {
                        extendedAddress = extendedAddress.copy(locationType = it)
                    })
                }


            }

            PhoneNumberField(
                extendedAddress.phoneNumber,
                listOf(""),
                0,
                false,
                Modifier.constrainAs(_phoneNumper) {
                    top.linkTo(_locationType.bottom, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                }) {
                extendedAddress = extendedAddress.copy(phoneNumber = it)
            }

            OutlinedTextField(
                extendedAddress.landmark,
                {
                    extendedAddress = extendedAddress.copy(landmark = it)
                },
                Modifier
                    .constrainAs(_landMark) {
                        top.linkTo(_phoneNumper.bottom, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .fillMaxWidth(),
                placeholder = { Text("LandMark(Optional)") },
                shape = MaterialTheme.shapes.small
            )

            OutlinedTextField(
                extendedAddress.others,
                {
                    extendedAddress = extendedAddress.copy(others = it)
                },
                Modifier
                    .constrainAs(_others) {
                        top.linkTo(_landMark.bottom, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .fillMaxWidth(),
                placeholder = { Text("Others(Optional)") },
                shape = MaterialTheme.shapes.small
            )

        }
        Spacer(Modifier.height(50.dp))
    }
}

@Composable
fun SettingsScreen(
    sharedViewModel: SharedViewModel,
    _customerInfo: CustomerInfo,
    orderItems: MutableState<List<OrderItem>>,
    onOrdered: () -> Unit,
    signOut: () -> Unit,
    restaurantPickListener: (RestaurantInfo) -> Unit
) {

    var customerInfo by remember {
        mutableStateOf(_customerInfo)
    }

    LaunchedEffect(Unit) {
        supabase.from(Table.CustomerInfo.name).select {
            filter {
                eq("channelId", customerInfo.channelId ?: "")
            }
        }.decodeSingle<CustomerInfo>().let {
            customerInfo = it
        }
    }


    val (none, orders, addressBook, collections, profile) = listOf(0, 1, 2, 3, 4)

    var tab = remember { mutableStateOf(orders) }

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(50.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                customerInfo.name.uppercase(Locale.getDefault())[0].toString(),
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Cyan.copy(0.35f), CircleShape)
                    .padding(top = 10.dp),
                softWrap = false,
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
            )

            IconButton({
                signOut()
            }, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    "",
                    Modifier.size(35.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

            }


        }

        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton({
                tab.value = orders
            }) {
                Icon(Icons.Default.ShoppingCart, "", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton({
                tab.value = addressBook
            }) {
                Icon(Icons.Default.AccountBox, "", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton({
                tab.value = collections
            }) {
                Icon(Icons.Default.Favorite, "", tint = MaterialTheme.colorScheme.primary)
            }
        }

        if (orders == tab.value) OrderListScreen(sharedViewModel)

        if (collections == tab.value) Collection(
            sharedViewModel,
            _customerInfo,
            tab,
            orderItems,
            onOrdered
        ) {
            restaurantPickListener(it)
        }

        if (addressBook == tab.value) Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            //.filter { Pattern.compile(searchField).matcher(it.address).find() || Pattern.compile(searchField).matcher(it.pincode).find() }
            AddressScreen(sharedViewModel, {
                scope.launch {
                    supabase.from(Table.CustomerInfo.name).update({
                        set("address", it)
                    }) {
                        filter {
                            eq("id", customerInfo.id ?: "")
                        }
                    }
                    customerInfo = customerInfo.copy(address = it)
                }
            }, null)

        }
    }

    if (none == tab.value) Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {


    }
}


