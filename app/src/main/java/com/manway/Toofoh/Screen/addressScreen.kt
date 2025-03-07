package com.manway.Toofoh.Screen

import Ui.data.Address
import Ui.data.LocationType
import Ui.data.PhoneNumberField
import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.manway.Toofoh.R
import com.manway.Toofoh.ViewModel.SharedViewModel
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    sharedViewModel: SharedViewModel,
    onAddressChanged: (List<Address>) -> Unit,
    onCloseAction: (() -> Unit)? = null,
    onPickerState: (Address) -> Unit = {},
) {

    var addressList by remember {
        mutableStateOf(sharedViewModel.customerInfo?.address ?: listOf())
    }


    var address by remember {
        mutableStateOf<Address?>(null)
    }

    var addressIndex by remember {
        mutableStateOf(-1)
    }
    var openBottomSheet by remember {
        mutableStateOf(addressList.isEmpty())
    }

    LaunchedEffect(sharedViewModel.liveValue) {
        addressList = sharedViewModel.liveValue.customerInfo?.address ?: listOf()
    }



    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(60.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {


            if (onCloseAction != null) IconButton({
                onCloseAction()
            }) {
                Icon(Icons.Default.Close, "")
            }
            Spacer(Modifier.width(10.dp))
        }




        Card(
            {
                address = Address.intial
                openBottomSheet = true
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(0.90f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            TextButton({
                address = Address.intial
                openBottomSheet = true
            }) {
                Text("Add Your Address", Modifier.padding(15.dp))
            }
        }

        addressList.forEachIndexed { i, it ->
            AddressCard(it, {
                onPickerState(it)
            }) {
                address = it
                addressIndex = i
                openBottomSheet = true
            }
        }

        val sheetState = rememberModalBottomSheetState(true, { true })



        if (openBottomSheet) ModalBottomSheet(
            { openBottomSheet = false },
            modifier = Modifier.fillMaxWidth(0.98f),
            sheetState = sheetState,
        ) {
            address?.let { adr ->
                AddressBookBottomSheet(sharedViewModel, adr, { a, b ->
                    if (b && addressList.contains(a)) addressList =
                        addressList.filterIndexed { j, item -> addressIndex != j }
                    sharedViewModel.customerInfo =
                        sharedViewModel.customerInfo?.copy(address = addressList)
                    onAddressChanged(addressList)
                    openBottomSheet = false
                }) {
                    if (addressIndex >= 0) {
                        if (adr == addressList[addressIndex]) addressList =
                            addressList.mapIndexed { j, item -> if (addressIndex == j) it else item }
                        else addressList = addressList + it
                        sharedViewModel.customerInfo =
                            sharedViewModel.customerInfo?.copy(address = addressList)
                        onAddressChanged(addressList)
                        openBottomSheet = false
                    } else {
                        addressList = addressList + it
                        sharedViewModel.customerInfo =
                            sharedViewModel.customerInfo?.copy(address = addressList)
                        onAddressChanged(addressList)
                        openBottomSheet = false
                    }
                }
            }
        }

    }


}


@Composable
private fun AddressCard(
    address: Address,
    onPickerState: () -> Unit,
    onEditState: (Address) -> Unit,
) {
    address.apply {
        Card(
            {
                onPickerState()
            },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(10.dp),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            ConstraintLayout {
                val (icon, location, _address, edit) = createRefs()
                Icon(
                    painter = painterResource(
                        when (address.address.locationType) {
                            LocationType.Home.name -> R.drawable.home
                            LocationType.Office.name -> R.drawable.office
                            LocationType.Work.name -> R.drawable.factory
                            else -> R.drawable.location
                        }
                    ), "",
                    Modifier
                        .constrainAs(icon) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start, 20.dp)
                        }
                        .size(35.dp), tint = Color.LightGray
                )

                Text(
                    address.address.location,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.constrainAs(location) {
                        top.linkTo(parent.top, 5.dp)
                        start.linkTo(icon.end, 20.dp)
                    })

                Text(
                    this@apply.toString(),
                    softWrap = false,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .clickable {
                            onPickerState()
                        }
                        .constrainAs(_address) {
                            top.linkTo(location.bottom, 5.dp)
                            start.linkTo(icon.end, 20.dp)
                        }
                        .padding(end = 10.dp)
                )

                TextButton({
                    onEditState(address)
                }, modifier = Modifier.constrainAs(edit) {
                    top.linkTo(_address.bottom, 0.dp)
                    start.linkTo(icon.end, 5.dp)
                }) {
                    Text("Edit", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AddressBookBottomSheet(
    sharedViewModel: SharedViewModel,
    _address: Address,
    onCloseAction: (Address, Boolean) -> Unit,
    onAddressChanged: (Address) -> Unit,
) {
    var address by remember {
        mutableStateOf(_address)
    }

    val (name, doorNumber, location, locationType, phoneNumber, landmark, others, pinCode, geoLocation) = listOf(
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9
    )

    var errorList by remember {
        mutableStateOf(
            arrayOf(
                "Name Cannot Empty",
                "Empty",
                "",
                "",
                "Enter Valid PhoneNumber",
                "",
                "",
                "",
                "",
                ""
            )
        )
    }

    var extendedAddress by remember {
        mutableStateOf(_address.address)
    }

    var openMap by remember {
        mutableStateOf((address.geoLocation.latitude == 0.0 && address.geoLocation.longitude == 0.0))
    }

    val scope = rememberCoroutineScope()

    //Validation
    extendedAddress.let {
        errorList[name] = if (extendedAddress.name.isEmpty()) "Name Cannot Empty" else ""
        errorList[doorNumber] = if (extendedAddress.doorNumber.isEmpty()) "Empty" else ""
        errorList[location] = ""
        errorList[locationType] = ""
        errorList[phoneNumber] =
            if (extendedAddress.phoneNumber.checkPhoneNumber()) "" else "Enter Valid PhoneNumber"
        errorList[landmark.toString().toInt()] = ""
        errorList[others.toString().toInt()] = ""
        errorList[geoLocation.toString().toInt()] = ""
        errorList[pinCode.toString().toInt()] = ""
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                {
                    openMap = !openMap
                },
                enabled = !(address.geoLocation.latitude == 0.0 && address.geoLocation.longitude == 0.0)
            ) {
                Icon(Icons.Default.LocationOn, "")
            }

            IconButton({
                onCloseAction(address, false)
            }) {
                Icon(Icons.Default.Close, "")
            }
            Spacer(Modifier.width(10.dp))
        }

        var location by remember {
            mutableStateOf("")
        }


        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            val (_name, _doorNo, _location, _locationType, _phoneNumper, pincode, _landMark, _others, delete, ok, map) = createRefs()

            if (openMap) Column(
                Modifier
                    .zIndex(2.0f)
                    .background(Color.White)
                    .padding(10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .constrainAs(map) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxSize()) {


                if (!(address.geoLocation.latitude == 0.0 && address.geoLocation.longitude == 0.0)) {
                    val geocoder = Geocoder(sharedViewModel.activity ?: LocalContext.current)
                    val addresses: List<android.location.Address>? = try {
                        geocoder.getFromLocation(
                            address.geoLocation.latitude,
                            address.geoLocation.longitude,
                            1
                        )
                    } catch (e: HttpRequestTimeoutException) {
                        Log.e("InternetException", "Check your Connection")
                        null
                    } catch (e: Exception) {
                        Log.e("Exception", "OrderScreen", e)
                        null
                    }


                    if (addresses != null && addresses.isNotEmpty()) {
                        extendedAddress =
                            extendedAddress.copy(location = addresses[0].getAddressLine(0) ?: "")
                        address = address.copy(
                            address = extendedAddress,
                            pincode = addresses[0].postalCode
                        )
                        location = addresses[0].getAddressLine(0) ?: ""
                    } else {
                        location = "Unknown Location"
                    }
                }


                BoxWithConstraints(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    MapScreen(
                        sharedViewModel = sharedViewModel, address.geoLocation,
                        Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    ) {
                        address = address.copy(
                            geoLocation = address.geoLocation.copy(
                                latitude = it.latitude,
                                longitude = it.longitude
                            )
                        )
                    }



                    Row(
                        Modifier
                            .absoluteOffset(y = -10.dp)
                            .fillMaxWidth(0.80f)
                            .height(60.dp)
                            .background(Color.White, MaterialTheme.shapes.small)
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            location,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(0.9f)
                                .fillMaxWidth()
                        )
                        TextButton({
                            openMap = false
                        }, enabled = location.isNotEmpty()) {
                            Text("Next")
                        }
                    }
                }
            }

            OutlinedTextField(
                extendedAddress.name,
                {
                    extendedAddress = extendedAddress.copy(name = it)
                },
                Modifier
                    .constrainAs(_name) {
                        top.linkTo(parent.top, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .fillMaxWidth(0.95f),
                label = { Text("Name") },
                shape = MaterialTheme.shapes.small,
                supportingText = {
                    Text(errorList[name])
                }
            )

            OutlinedTextField(
                extendedAddress.doorNumber,
                {
                    extendedAddress = extendedAddress.copy(doorNumber = it)
                },
                Modifier
                    .constrainAs(_doorNo) {
                        top.linkTo(_name.bottom, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .fillMaxWidth(0.25f),
                label = { Text("Door No") },
                shape = MaterialTheme.shapes.small,
                supportingText = {
                    Text(errorList[doorNumber])
                }
            )

            OutlinedTextField(
                extendedAddress.location.toString(),
                {
                    extendedAddress = extendedAddress.copy(location = it)
                    address = address.copy(address = extendedAddress)

                },
                Modifier
                    .constrainAs(_location) {
                        top.linkTo(_name.bottom, 10.dp)
                        start.linkTo(_doorNo.end, 10.dp)
                    }
                    .fillMaxWidth(0.65f),
                singleLine = true,
                label = { Text("location") },
                shape = MaterialTheme.shapes.small
            )



            Row(
                Modifier
                    .constrainAs(_locationType) { top.linkTo(_doorNo.bottom, 10.dp) }
                    .padding(start = 5.dp)
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ), verticalAlignment = Alignment.CenterVertically
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
                    TextField(
                        extendedAddress.locationType,
                        {
                            extendedAddress = extendedAddress.copy(locationType = it)
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .height(60.dp)
                            .width(250.dp)
                            .padding(vertical = 5.dp)
                    )
                }


            }

            PhoneNumberField(
                extendedAddress.phoneNumber,
                errorList.toList(),
                phoneNumber,
                false,
                Modifier.constrainAs(_phoneNumper) {
                    top.linkTo(_locationType.bottom, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                }) {
                extendedAddress = extendedAddress.copy(phoneNumber = it)
                scope.launch {
                    //   errorList[phoneNumber] = supabase.postgrest.rpc("validate_phone_number", mapOf("_data" to extendedAddress.phoneNumber)).data.toString()
                    errorList[phoneNumber] = ""
                }
            }

            OutlinedTextField(
                address.pincode,
                {
                    address = address.copy(pincode = it)
                },
                Modifier
                    .constrainAs(pincode) {
                        top.linkTo(_phoneNumper.bottom, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .fillMaxWidth(0.95f),
                label = { Text("Pincode") },
                shape = MaterialTheme.shapes.small
            )

            OutlinedTextField(
                extendedAddress.landmark,
                {
                    extendedAddress = extendedAddress.copy(landmark = it)
                },
                Modifier
                    .constrainAs(_landMark) {
                        top.linkTo(pincode.bottom, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .fillMaxWidth(0.95f),
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
                    .fillMaxWidth(0.95f),
                placeholder = { Text("Others(Optional)") },
                shape = MaterialTheme.shapes.small
            )


            IconButton({
                onCloseAction(address, true)
            }, modifier = Modifier.constrainAs(delete) {
                top.linkTo(_others.bottom, 10.dp)
                start.linkTo(_others.start, 10.dp)

            }) {
                Icon(Icons.Default.Delete, "")
            }
            TextButton({
                onAddressChanged(address.copy(extendedAddress))
            }, modifier = Modifier.constrainAs(ok) {
                top.linkTo(_others.bottom, 10.dp)
                end.linkTo(_others.end, 10.dp)

            }, enabled = !errorList.map { it.isEmpty() }.contains(false)) {
                Text("OK", style = MaterialTheme.typography.titleMedium)
            }

        }
        Spacer(Modifier.height(50.dp))
    }
}

private operator fun <E> List<E>.component6(): Any {
    return 6
}

private operator fun <E> List<E>.component7(): Any {
    return 7
}

private operator fun <E> List<E>.component8(): Any {
    return 8
}

private operator fun <E> List<E>.component9(): Any {
    return 9
}