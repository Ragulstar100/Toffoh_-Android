package Ui.data

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manway.Toofoh.dp.CouldFunction
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.toffoh.admin.data.Country
import com.manway.Toofoh.data.ErrorInfo
import com.manway.Toofoh.data.ErrorState
import com.manway.toffoh.admin.ui.MyOutlinedTextField
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.regex.Pattern

@Serializable
data class PhoneNumber(val countryCode:String,val phoneNumber:String){
    override fun toString(): String {
        return "$countryCode $phoneNumber"
    }
}

fun String.toPhoneNumber(): PhoneNumber {
    val pattern = Pattern.compile("(\\+\\d{1,3})\\s(\\d{9,14})")
    val matcher = pattern.matcher(this)
    var phoneNumber= PhoneNumber("","")
    if (matcher.find()) {
        phoneNumber= PhoneNumber(matcher.group(1),matcher.group(2))
    }
    return phoneNumber

}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberField(initialField:String="",successButtonAction: () -> Unit, failureListener: () -> Unit) {
    val PhoneNumber = "Phone Number"
    var phoneNumber by remember { mutableStateOf(initialField) }
    var expanded by remember { mutableStateOf(false) }

    var countries by remember {
        mutableStateOf(listOf(Country(1, "https://fzuiczfdbipljksnyydj.supabase.co/storage/v1/object/public/defaultImages/+91.png?t=2024-10-30T06%3A42%3A33.119Z", "India", "+91", "^(\\+91[\\s-]?)?[6-9]\\d{9}\$"),))
    }
    var selectedItem by remember { mutableStateOf(countries[0]) }

    // Error handling state
    var isInvalid by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Function to validate phone number
    fun validatePhoneNumber(number: String): Boolean {
        val pattern = selectedItem.pattern.toRegex()
        return pattern.matches(number)
    }

    // Handle phone number changes
    val onPhoneNumberChanged: (String) -> Unit = { newNumber ->
        phoneNumber = newNumber
        isInvalid = !validatePhoneNumber(newNumber)
        errorMessage = if (isInvalid) "Invalid Phone Number" else ""
    }

    LaunchedEffect(key1 = Unit) {
        onPhoneNumberChanged(phoneNumber)
    }

    var scope= rememberCoroutineScope()

    var _findThePattern= flow<Pair<Int,List<Country>>> {
        while (true) {
            scope.launch {
             val findThePattern=
                 supabase.postgrest.rpc(CouldFunction.phone_number_pattern.first,mapOf(CouldFunction.phone_number_pattern.second[0] to phoneNumber)).data.toInt()
                val response: List<Country> = try {
                    supabase.from(Table.Country.name).select().decodeList<Country>()
                } catch (e: Exception) {
                    listOf<Country>(
                        Country(
                            1,
                            "https://fzuiczfdbipljksnyydj.supabase.co/storage/v1/object/public/defaultImages/+91.png?t=2024-10-30T06%3A42%3A33.119Z",
                            "India",
                            "+91",
                            "^(\\+91[\\s-]?)?[6-9]\\d{9}\$"
                        )
                    )
                }
                emit(findThePattern to response)
            }

            delay(1000)
        }
    }
    scope.launch {
        _findThePattern.collect{
            countries=it.second
        }
    }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        MyOutlinedTextField(
            phoneNumber,
            onValueChange = onPhoneNumberChanged,
            label = PhoneNumber,
            errorInfo = ErrorInfo(PhoneNumber, "Invalid Phone Number"),
            errorState = ErrorState(isInvalid, true),
            leadingIcon = {
                Row(Modifier.padding(start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                   // AsyncImage(selectedItem.imageUrl ?: "", contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(selectedItem.countryCode)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        if (validatePhoneNumber(phoneNumber)) {
                            // Send OTP or proceed with verification logic
                            successButtonAction() // Or your specific action
                        } else {
                            // Handle invalid number scenario (display error)
                            failureListener() // Or your specific error handling
                        }
                    }
                ) {
                    Text("Send Otp")
                }
            }
        )


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(280.dp)
        ) {
            countries.forEach { item ->
                DropdownMenuItem({
                    Text(item.countryCode)
                },{
                    selectedItem = item
                    expanded = false
                    onPhoneNumberChanged(phoneNumber)
                },Modifier.width(200.dp))
            }
        }
    }

    // ... rest of your composable content
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberField(initialField:String="",readOnly:Boolean,errorCheck:Boolean,successButtonAction: (String) -> Unit, failureListener: () -> Unit) {
    val PhoneNumber = "Phone Number"
    var phoneNumber by remember { mutableStateOf(initialField) }
    var expanded by remember { mutableStateOf(false) }

    var countries by remember {
        mutableStateOf(listOf(Country(1, "https://fzuiczfdbipljksnyydj.supabase.co/storage/v1/object/public/defaultImages/+91.png?t=2024-10-30T06%3A42%3A33.119Z", "India", "+91", "^(\\+91[\\s-]?)?[6-9]\\d{9}\$"),))
    }
    var selectedItem by remember { mutableStateOf(countries[0]) }

    // Error handling state
    var isInvalid by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Function to validate phone number
    fun validatePhoneNumber(number: String): Boolean {
        val pattern = selectedItem.pattern.toRegex()
        return pattern.matches(number)
    }

    // Handle phone number changes
    val onPhoneNumberChanged: (String) -> Unit = { newNumber ->
        phoneNumber = newNumber
        isInvalid = !validatePhoneNumber(newNumber)&&errorCheck
        errorMessage = if (isInvalid) "Invalid Phone Number" else ""

        if (validatePhoneNumber(phoneNumber)) {
            successButtonAction(phoneNumber)
        } else {
            failureListener()
        }

    }

    LaunchedEffect(key1 = Unit) {
        onPhoneNumberChanged(phoneNumber)
    }

    var scope= rememberCoroutineScope()

    var _findThePattern= flow<Pair<Int,List<Country>>> {
        while (true) {
            scope.launch {
                val findThePattern= supabase.postgrest.rpc(
                    CouldFunction.phone_number_pattern.first,mapOf(CouldFunction.phone_number_pattern.second[0] to phoneNumber)).data.toInt()
                val response: List<Country> = try {
                    supabase.from(Table.Country.name).select().decodeList<Country>()
                } catch (e: Exception) {
                    listOf<Country>(
                        Country(
                            1,
                            "https://fzuiczfdbipljksnyydj.supabase.co/storage/v1/object/public/defaultImages/+91.png?t=2024-10-30T06%3A42%3A33.119Z",
                            "India",
                            "+91",
                            "^(\\+91[\\s-]?)?[6-9]\\d{9}\$"
                        )
                    )
                }
                emit(findThePattern to response)
            }

            delay(1000)
        }
    }


    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        MyOutlinedTextField(
            phoneNumber,
            onValueChange = onPhoneNumberChanged,
            label = PhoneNumber,
            errorInfo = ErrorInfo(PhoneNumber, "Invalid Phone Number"),
            errorState = ErrorState(isInvalid, true),
            readOnly = readOnly,
            leadingIcon = {
                Row(Modifier.padding(start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                 //   AsyncImage(selectedItem.imageUrl ?: "", contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(selectedItem.countryCode)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }
            ,
            trailingIcon ={
                if(isInvalid&&errorCheck) Text("Enter the valid number")
            }
        )


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(280.dp)
        ) {
            countries.forEach { item ->
                DropdownMenuItem({
                    Text(item.countryCode)
                },{
                    selectedItem = item
                    expanded = false
                    onPhoneNumberChanged(phoneNumber)
                },Modifier.width(200.dp))
            }
        }
    }

    // ... rest of your composable content
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberField(_phoneNumber: PhoneNumber,errorList:List<String>,errorIndex:Int,readOnly: Boolean=false,onPhoneNumberChanged:(PhoneNumber)->Unit) {
    val PhoneNumber = "Phone Number"
    var phoneNumber by remember { mutableStateOf(_phoneNumber) }

    var expanded by remember { mutableStateOf(false) }

    var countries by remember {
        mutableStateOf(listOf(Country(1, "https://fzuiczfdbipljksnyydj.supabase.co/storage/v1/object/public/defaultImages/+91.png?t=2024-10-30T06%3A42%3A33.119Z", "India", "+91", "^(\\+91[\\s-]?)?[6-9]\\d{9}\$"),))
    }
    var selectedItem by remember { mutableStateOf(countries[0]) }

    // Error handling state
    var isInvalid by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        onPhoneNumberChanged(phoneNumber)
    }



    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        MyOutlinedTextField(
            phoneNumber.phoneNumber,
            onValueChange = {
                phoneNumber=phoneNumber.copy(phoneNumber = it)
                onPhoneNumberChanged(phoneNumber)
            },
            label = PhoneNumber,
            errorInfo = ErrorInfo(PhoneNumber, errorList[errorIndex]),
            errorState = ErrorState(true, false),
            readOnly = readOnly,
            leadingIcon = {
                Row(Modifier.menuAnchor().padding(start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                   // AsyncImage(selectedItem.imageUrl ?: "", contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(selectedItem.countryCode)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

        )


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(280.dp)
        ) {
            countries.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.countryCode) },
                    onClick = {
                        selectedItem = item
                        expanded = false
                        phoneNumber=phoneNumber.copy(countryCode = selectedItem.countryCode)
                        onPhoneNumberChanged(phoneNumber) // Update validation on country change
                    },
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }

    // ... rest of your composable content
}
