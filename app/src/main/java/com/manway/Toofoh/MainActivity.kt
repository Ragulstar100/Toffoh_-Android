package com.manway.Toofoh

import Screen.MainScreen
import Screen.preview
import Ui.data.ImageUrl
import Ui.data.PhoneNumberField
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.manway.Toofoh.Screen.OrderScreen
import com.manway.Toofoh.Screen.RestaurantScreen
import com.manway.Toofoh.ViewModel.ServiceAreaViewModel
import com.manway.Toofoh.android.GoogleSignInButton
import com.manway.Toofoh.android.GoogleSignInClient
import com.manway.Toofoh.android.LocalDb
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.data.ErrorInfo
import com.manway.Toofoh.data.ErrorState
import com.manway.Toofoh.data.OrderItem
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.enums.LoginMethod
import com.manway.Toofoh.ui.theme.MyApplicationTheme
import com.manway.toffoh.admin.data.RestaurantInfo
import com.manway.toffoh.admin.ui.MyOutlinedTextField
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

enum class screen{
    Home, Restorent, serviceCheck, profile, signup, orders, favourite, splashScreen
}

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleSignInClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()

            var orderItems = remember {
                mutableStateOf(listOf<OrderItem>())
            }


            val localDb = LocalDb(this)

            var login by remember {
                mutableStateOf<Pair<LoginMethod, String?>>(localDb.getSignIn())
            }
            var loginEmail by remember {
                mutableStateOf<String?>(null)
            }
            var customerInfo by remember {
                mutableStateOf<CustomerInfo?>(null)
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->

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
                if (login.first == LoginMethod.Google) {
                    if (login.first == LoginMethod.Google) {
                        loginEmail = login.second
                        scope.launch {
                            try {
                                customerInfo = supabase.from(Table.CustomerInfo.name)
                                    .select { filter { eq("email", loginEmail ?: "") } }
                                    .decodeSingle()
                                navController.navigate(screen.orders.name)
                            } catch (e: Exception) {
//                                val profileUrl = ImageUrl("", "", session.session.user?.userMetadata?.get("avatar_url").toString())
//                                customerInfo = CustomerInfo.initialCustomerInfo.copy(email = loginEmail, profileUrl = profileUrl)
                                navController.navigate(screen.signup.name)
                            }

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
                                    customerInfo = supabase.from(Table.CustomerInfo.name)
                                        .select { filter { eq("email", loginEmail ?: "") } }
                                        .decodeSingle()
                                    navController.navigate(screen.Home.name)
                                    Toast.makeText(
                                        applicationContext,
                                        session.session.user?.email,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    val profileUrl = ImageUrl(
                                        "",
                                        "",
                                        session.session.user?.userMetadata?.get("avatar_url")
                                            .toString()
                                    )
                                    customerInfo = CustomerInfo.initialCustomerInfo.copy(
                                        email = loginEmail,
                                        profileUrl = profileUrl
                                    )
                                    navController.navigate(screen.profile.name)
                                }

                            }

                        }
                    }

                    is SessionStatus.NotAuthenticated -> {

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
                    startDestination = screen.splashScreen.name
                ) {

                    composable(screen.splashScreen.name) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Toffoh",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    composable(screen.signup.name) {
                        SignUpScreen(navController, {
                            lifecycleScope.launch {
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        googleAuthUiClient.signIn() ?: return@launch
                                    ).build()
                                )
                            }
                        }, {
                        }) {
                            Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    composable(screen.Home.name) {
                        customerInfo?.let { it1 ->
                           MainScreen(localDb, it1, {
                                navController.navigate(it.name)
                            }) {
                                restaurantInfo = it
                                navController.navigate(screen.Restorent.name)
                            }
                        }
                    }

                    composable(screen.serviceCheck.name) {
                        ServiceCheckScreen("") {
                            navController.navigate(screen.profile.name)
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()

                        }

                    }

                    composable(screen.profile.name) {
                        customerInfo?.let {
                            it.profileInfo {
                                navController.navigate(screen.Home.name)
                            }
                        }
                    }

                    composable(screen.Restorent.name) {
                        restaurantInfo?.let {
                            customerInfo?.let { cus ->
                                RestaurantScreen(cus,orderItems, it)
                            }
                        }
                        }

                    composable(screen.orders.name) {
                        customerInfo?.let {
                            OrderScreen(it)
                        }
                    }
                }

            }
        }
        }
    }

@Composable
fun SignUpScreen(
    naveController: NavHostController,
    googleButtonAction: () -> Unit,
    otpAction: () -> Unit,
    failureListener: () -> Unit,
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ServiceCheckScreen(
    _pincode: String,
    modifier: Modifier = Modifier,
    error: () -> Unit = {},
    isServiceState: (String) -> Unit,
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
