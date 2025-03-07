package com.manway.Toofoh.dp
import Ui.data.ImageUrl
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json


val supabaseUrl = "https://fzuiczfdbipljksnyydj.supabase.co"
val supabaseKey="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ6dWljemZkYmlwbGprc255eWRqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjk4ODAwNDcsImV4cCI6MjA0NTQ1NjA0N30.z8w55GYxiRDESkT4iBS__gN8I4sVeDv9rKVtj5xwYk0"

//Create a supabase client
val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
    //Already the default serializer, but you can provide a custom Json instance (optional):
    defaultSerializer = KotlinXSerializer(Json{
        install(Postgrest)
        install(Auth)
        install(Realtime)
        install(Storage)
        ignoreUnknownKeys=true
    })
}

val supabaseMasterUrl = "https://fzuiczfdbipljksnyydj.supabase.co"
val supabaseMasterKey="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ6dWljemZkYmlwbGprc255eWRqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTcyOTg4MDA0NywiZXhwIjoyMDQ1NDU2MDQ3fQ.ixfdI-OwYQhsL-fRn0VfkGDybRh79YtCfZtBg_Dx2W0"

//Create a supabase client
val supabaseMaster = createSupabaseClient(supabaseMasterUrl, supabaseMasterKey) {
    defaultSerializer = KotlinXSerializer(Json{
        install(Postgrest)
        install(Auth)
        install(Realtime)
        install(Storage)
    })
}

//Andriod Only
@Composable
fun SupabaseClient.getImage(
    url: ImageUrl?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    enableBlackAndWhite: Boolean = false
) {
    url?.let {
        val publicUrl = storage.from(url.bucketName).publicUrl(url.filePath)
        println(publicUrl)

        val grayscaleMatrix = ColorMatrix().apply { this.setToSaturation(0f) }
        val grayscaleFilter = ColorFilter.colorMatrix(grayscaleMatrix)


        if ((url.imageUrl ?: "").isEmpty()) AsyncImage(
            publicUrl,
            "+",
            modifier,
            colorFilter = if(enableBlackAndWhite) grayscaleFilter else null,
            contentScale = contentScale
        )
        else AsyncImage(url.imageUrl, "+", modifier, contentScale = contentScale)
    }
}


//enum class Schema{
//    public
//}



class CouldFunction{


    companion object{
        val customerInfoValidate= "customerinfovalidate" to listOf<String>("customerdata")
        val phone_number_pattern = "phone_number_pattern" to listOf<String>("number")
        val validatePhoneNumber="validatePhoneNumber" to listOf<String>("country_code","number")
        val RestaurantInfoValidate="restaurantinfovalidate" to listOf<String>("restaurantdata")
        val foodinfovalidate ="foodinfovalidate" to listOf<String>("fooddata")
        val orderinfovalidate="orderinfovalidate" to listOf<String>("orderdata")
    }

}

enum class Table{
    CustomerInfo,ServiceArea,Country,
    OwnerInfo,RestaurantInfo,FoodInfo,OrderInfo,CommonFoodInfo,FavInfo
}

enum class Bucket{
    customerProfileImg,defaultImages
}