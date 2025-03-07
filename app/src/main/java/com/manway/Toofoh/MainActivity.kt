package com.manway.Toofoh

import Screen.MainScreen
import Ui.data.ImageUrl
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.base.exception.CFException
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.CFSession.CFSessionBuilder
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutTheme.CFWebCheckoutThemeBuilder
import com.example.paymentc.api.PaymentAPI
import com.example.paymentc.api.PaymentStatusAPI
import com.google.android.gms.auth.api.identity.Identity
import com.manway.Toofoh.Screen.OrderScreen
import com.manway.Toofoh.Screen.RestaurantScreen
import com.manway.Toofoh.Screen.ServiceCheckScreen
import com.manway.Toofoh.Screen.SettingsScreen
import com.manway.Toofoh.Screen.SignUpScreen
import com.manway.Toofoh.Screen.addressLog
import com.manway.Toofoh.ViewModel.SharedViewModel
import com.manway.Toofoh.android.GoogleSignInClient
import com.manway.Toofoh.android.LocalDb
import com.manway.Toofoh.api.SplitPaymentAPI
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.interfaces.AdjustmentRequest
import com.manway.Toofoh.models.Data
import com.manway.Toofoh.models.PaymentModel
import com.manway.Toofoh.models.PaymentStatusModel
import com.manway.Toofoh.models.UserData
import com.manway.Toofoh.ui.android.showErrorDialog
import com.manway.Toofoh.ui.channel.RDialog
import com.manway.Toofoh.ui.channel.dialogChannel
import com.manway.Toofoh.ui.enums.LoginMethod
import com.manway.Toofoh.ui.theme.MyApplicationTheme
import com.manway.toffoh.admin.data.RestaurantInfo
import data.enums.Role
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


enum class screen{
    Home, Restorent, serviceCheck, profile, signup, orders, favourite, splashScreen, Settings, Address, Permission
}

val currentRole = Role.Customer
val distanecovered = 15000
class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleSignInClient(context = this, oneTapClient = Identity.getSignInClient(applicationContext))
    }

    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {

            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            var settingsScreen= remember { mutableStateOf(0) }
            var sharedViewModel = viewModel<SharedViewModel>()

            var orderItems = remember {
                mutableStateOf(listOf<OrderItem>())
            }
            var locationPermissionOn by remember {
                mutableStateOf(false)
            }


            val localDb = LocalDb(this)

            var login by remember {
                mutableStateOf<Pair<LoginMethod, String?>>(localDb.getSignIn())
            }
            var loginEmail by remember {
                mutableStateOf<String?>(null)
            }
            var loginPhone by remember {
                mutableStateOf<String?>(null)
            }
            var customerInfo by remember {
                mutableStateOf<CustomerInfo?>(null)
            }

            var dialog by remember {
                mutableStateOf<List<RDialog>>(listOf())
            }

            LaunchedEffect(Unit) {
                dialogChannel.receiveAsFlow().collect {
                    dialog = (dialog + it).toSet().toList()
                }

            }

            if (dialog.isNotEmpty()) {
                dialog.filter { !it.ignore }[0].openDialog {
                    dialog = dialog.filterIndexed { index, it -> index != 0 }
                }
            }





            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(), onResult = { result ->

                    if (result.resultCode == RESULT_OK) {
                        scope.launch {
                            val signInData = googleAuthUiClient.signInWithIntent(result.data)
                            if (signInData != null) {
                                lifecycleScope.launch {
                                    supabase.auth.signInWith(IDToken) {
                                        idToken = signInData["Token"] ?: "";provider = Google
                                    }
                                }
                                login = LoginMethod.Google to signInData["Email"]
                                localDb.setSignIn(login)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "google sign in failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    } else {
                        Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            LaunchedEffect(Unit) {

                sharedViewModel.activity = this@MainActivity



                if (login.first == LoginMethod.Google) {

                    loginEmail = login.second

                    scope.launch {
                        try {
                            sharedViewModel.customerInfo = supabase.from(Table.CustomerInfo.name)
                                .select { filter { eq("email", loginEmail ?: "") } }.decodeSingle()
                        } catch (e: Exception) {
                            val profileUrl = ImageUrl("", "", "")
                            customerInfo = CustomerInfo.initialCustomerInfo.copy(
                                email = loginEmail,
                                profileUrl = profileUrl
                            )
                            navController.navigate(screen.profile.name)
                        }

                    }

                } else if (login.first == LoginMethod.PhoneNumber) {

                } else {
                    navController.navigate(screen.signup.name)
                }

            }

            supabase.auth.sessionStatus.collectAsStateWithLifecycle().value.let { session ->
                var scope = rememberCoroutineScope()


                when (session) {
                    is SessionStatus.Authenticated -> {
                        if (login.first == LoginMethod.Google) {
                            loginEmail = session.session.user?.email
                            scope.launch {
                                try {
                                    sharedViewModel.customerInfo =
                                        supabase.from(Table.CustomerInfo.name)
                                            .select { filter { eq("email", loginEmail ?: "") } }
                                            .decodeSingle()
                                    navController.navigate(screen.Home.name)
                                } catch (e: HttpRequestTimeoutException) {
                                    showErrorDialog(
                                        "Internet",
                                        "Check Your Internet Connection",
                                        this@MainActivity
                                    )
                                } catch (e: Exception) {
                                    val profileUrl = ImageUrl(
                                        "",
                                        "",
                                        session.session.user?.userMetadata?.get("avatar_url")
                                            .toString()
                                    )
                                    sharedViewModel.customerInfo =
                                        CustomerInfo.initialCustomerInfo.copy(
                                            email = loginEmail,
                                            profileUrl = profileUrl
                                        )
                                    navController.navigate(screen.profile.name)
                                }

                            }

                        } else if (login.first == LoginMethod.PhoneNumber) {
                            loginPhone = session.session.user?.phone
                            scope.launch {
                                try {
                                    customerInfo = supabase.from(Table.CustomerInfo.name).select {
                                        filter { eq("phoneNumber", loginPhone ?: "") }
                                    }.decodeSingle()
                                    navController.navigate(screen.Home.name)
                                }catch (e: HttpRequestTimeoutException) {
                                    showErrorDialog(
                                        "Internet",
                                        "Check Your Internet Connection",
                                        this@MainActivity
                                    )
                                }
                                catch (e: Exception) {
                                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                                    val profileUrl = ImageUrl("", "", session.session.user?.userMetadata?.get("avatar_url").toString())
                                    customerInfo = CustomerInfo.initialCustomerInfo.copy(email = loginEmail, profileUrl = profileUrl)
                                    navController.navigate(screen.profile.name)
                                }

                            }

                        } else {
                            navController.navigate(screen.signup.name)
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        scope.launch {
//                                sharedViewModel.customerInfo = supabase.from(Table.CustomerInfo.name).select {
//                                    filter { eq("email", "ragulson200@gmail.com") }
//
//                                }.decodeSingle()
                            navController.navigate(screen.Home.name)
                        }
                    }

                    else -> {

                    }
                }
            }

            MyApplicationTheme {


                var restaurantInfo by remember {
                    mutableStateOf<RestaurantInfo?>(null)
                }

                NavHost(
                    navController = navController,
                    startDestination = if (sharedViewModel.onLunch) screen.splashScreen.name else screen.Home.name
                ) {

                    composable(screen.splashScreen.name) {


                        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("Toffoh", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)

                            sharedViewModel.onLunch = false
                        }

                    }

                    composable(screen.signup.name) {
                        SignUpScreen({
                            lifecycleScope.launch {
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        googleAuthUiClient.signIn() ?: return@launch
                                    ).build()
                                )
                            }
                        }, {
                        }) {
                            Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    composable(screen.Home.name) {

                        LaunchedEffect(sharedViewModel._liveValue) {
                            customerInfo = sharedViewModel.customerInfo
                        }
                        //   MapScreen(sharedViewModel)
                        customerInfo?.let { it1 ->
                            // upiPayments(paymentStatusListener)

                            MainScreen(sharedViewModel, localDb, it1, {
                                navController.navigate(it.name)
                            }) {
                                restaurantInfo = it
                                navController.navigate(screen.Restorent.name)
                            }
                        }
                    }

                    composable(screen.serviceCheck.name) {
                        //  Toast.makeText(applicationContext, "Home2", Toast.LENGTH_SHORT).show()
                        ServiceCheckScreen("") {
                            navController.navigate(screen.profile.name)
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()

                        }

                    }

                    composable(screen.profile.name) {

                        customerInfo?.let {
                            it.profileInfo(sharedViewModel) {
                                navController.navigate(screen.Home.name)
                            }
                        }
                    }

                    composable(screen.Restorent.name) {
                        restaurantInfo?.let {
                            customerInfo?.let { cus ->
                                RestaurantScreen(sharedViewModel, orderItems, it, {
                                    navController.navigate(screen.orders.name)
                                }) {
                                    navController.navigate(screen.Home.name)
                                }
                            }
                        }
                        }

                    composable(screen.Address.name) {
                        customerInfo?.let {
                            addressLog(it.address, {
                            }) {
                                CustomerInfo.pickedAddress = it
                                sharedViewModel.currentAddress = it
                                navController.navigate(screen.Restorent.name)
                            }
                        }
                    }

                    composable(screen.orders.name) {
                        customerInfo?.let {
                            OrderScreen(sharedViewModel) {
                                navController.navigate(screen.Settings.name)
                            }
                        }
                    }


                    composable(screen.Settings.name){
                        customerInfo?.let {
                            SettingsScreen(sharedViewModel, it, orderItems, {
                                navController.navigate(screen.orders.name)
                            }, {
                                scope.launch {
                                    googleAuthUiClient.signOut()
                                    localDb.setSignIn(LoginMethod.None to null)
                                    supabase.auth.signOut()
                                }
                                navController.navigate(screen.signup.name)
                            }, {
                                restaurantInfo = it
                                navController.navigate(screen.Restorent.name)
                            })
                        }
                    }
                }

            }
        }

    }


}







