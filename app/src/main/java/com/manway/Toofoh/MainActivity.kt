package com.manway.Toofoh

import Screen.preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.manway.Toofoh.Screen.RestaurantScreen
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.theme.MyApplicationTheme
import com.manway.toffoh.admin.data.RestaurantInfo
import io.github.jan.supabase.postgrest.from

enum class screen{
    Home,Restorent
}
//https://github.com/Ragulstar100/Toffoh-Andriod
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var sampleCustomerInfo by remember {
                mutableStateOf<CustomerInfo?>(null)
            }
            LaunchedEffect(Unit) {
                sampleCustomerInfo= supabase.from(Table.CustomerInfo.name).select{
                    filter {
                        eq("id", 54)
                    }
                }.decodeSingle<CustomerInfo>()
            }
            MyApplicationTheme {
                val navController = rememberNavController()
                var restaurantInfo by remember {
                    mutableStateOf<RestaurantInfo?>(null)
                }
                NavHost(navController = navController, startDestination = screen.Home.name) {
                    composable(screen.Home.name) {
                        sampleCustomerInfo?.let {
                            preview(it) {
                                restaurantInfo = it
                                navController.navigate(screen.Restorent.name)
                            }
                        }
                    }
                    composable(screen.Restorent.name) {
                        restaurantInfo?.let {
                            RestaurantScreen(it)
                        }
                    }
                    // ... other composable destinations
                }
                }
            }
        }
    }


