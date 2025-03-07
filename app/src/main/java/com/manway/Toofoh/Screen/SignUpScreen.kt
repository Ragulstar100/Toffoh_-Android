package com.manway.Toofoh.Screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manway.Toofoh.ViewModel.ServiceAreaViewModel
import com.manway.Toofoh.ViewModel.SharedViewModel
import com.manway.Toofoh.android.GoogleSignInButton
import com.manway.Toofoh.data.ErrorInfo
import com.manway.Toofoh.data.ErrorState
import com.manway.toffoh.admin.ui.MyOutlinedTextField

@Composable
fun SignUpScreen(
    googleButtonAction: () -> Unit,
    otpAction: () -> Unit,
    failureListener: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(MaterialTheme.colorScheme.primary)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Toffoh", style = MaterialTheme.typography.displayLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
        //   PhoneNumberField("",false,otpAction,failureListener)
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(Modifier.fillMaxWidth(0.7f))
        Spacer(modifier = Modifier.height(20.dp))
        GoogleSignInButton(googleButtonAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPermissionHandler(sharedViewModel: SharedViewModel, onPermissionGranted: () -> Unit) {

    var permissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (permissionGranted) {
                onPermissionGranted()
            }
        }
    )


    LaunchedEffect(key1 = Unit) {
        sharedViewModel.activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                permissionGranted = true
                onPermissionGranted()
            }
        }
    }
}


fun CasHFreePayments() {

}


fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ServiceCheckScreen(
    _pincode: String,
    modifier: Modifier = Modifier,
    error: () -> Unit = {},
    isServiceState: (String) -> Unit
) {
    //Language
    val Enter_Pin_code = "Enter Pin code"
    var listServiceArea = viewModel<ServiceAreaViewModel>()
    var pinCode by remember { mutableStateOf(_pincode) }
    var interact by remember { mutableStateOf(false) }
    val errorInfo = ErrorInfo(Enter_Pin_code, "Invalid Pin code")
    val errorState = ErrorState(
        try {
            if (pinCode.length == 6) {
                interact = true
            }; listServiceArea.list.map { it.pincode }.contains(pinCode.toInt())
        } catch (e: Exception) {
            if (pinCode.length == 6) {
                interact = true
            }; false
        }.not(), interact
    )
    if (errorState.error.not()) {
        isServiceState(pinCode)
    } else {
        error()
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MyOutlinedTextField(
            pinCode,
            { pinCode = it },
            Enter_Pin_code,
            errorInfo,
            errorState,
            modifier = modifier
        )
    }

}
