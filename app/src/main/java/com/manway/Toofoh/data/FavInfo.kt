package com.manway.toffoh.admin.data

import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.dp.Table
import com.manway.Toofoh.dp.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class FavInfo private constructor(
    val favId: String,
    val isFavorate: Boolean,
    val star: Float,
) {

    constructor(
        resFav: ResFav,
        isFavorate: Boolean,
        star: Float,
    ) : this(Json.encodeToString(resFav), isFavorate, star)

    constructor(
        resFav: FoodFav,
        isFavorate: Boolean,
        star: Float,
    ) : this(Json.encodeToString(resFav), isFavorate, star)

    @Serializable
    data class ResFav(
        val customerId: String,
        val tableName: String = Table.RestaurantInfo.name,
        val resturentId: String?,
    )

    @Serializable
    data class FoodFav(
        val customerId: String,
        val tableName: String = Table.FoodInfo.name,
        val foodId: Int?,
    )

    companion object {
        suspend fun upsertRestaurant(
            customerInfo: CustomerInfo,
            restaurantInfo: RestaurantInfo,
            isFavorate: Boolean,
            star: Float,
        ) {
            supabase.from(Table.FavInfo.name).upsert(
                FavInfo(
                    ResFav(
                        customerInfo.channelId ?: "",
                        Table.RestaurantInfo.name,
                        restaurantInfo.channel_id
                    ), isFavorate, star
                )
            )
        }

        suspend fun upsertFood(
            customerInfo: CustomerInfo,
            foodInfo: FoodInfo,
            isFavorate: Boolean,
            star: Float,
        ) {
            supabase.from(Table.FavInfo.name).upsert(
                FavInfo(
                    FoodFav(
                        customerInfo.channelId ?: "",
                        Table.FoodInfo.name,
                        foodInfo.id
                    ), isFavorate, star
                )
            )
        }


    }

}