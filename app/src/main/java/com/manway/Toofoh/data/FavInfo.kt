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
    val star: Float
) {

    constructor(
        resFav: ResFav,
        isFavorite: Boolean,
        star: Float
    ) : this(Json.encodeToString(resFav), isFavorite, star)

    constructor(foodFav: FoodFav, isFavorite: Boolean, star: Float) : this(
        Json.encodeToString(
            foodFav
        ), isFavorite, star
    )

    @Serializable
    data class ResFav(val customerId: String, val tableName: String, val restaurantId: String?)

    @Serializable
    data class FoodFav(val customerId: String, val tableName: String, val foodId: Int?)

    companion object {
        suspend fun upsertFav(
            resFav: ResFav,
            isFavorite: Boolean? = null,
            isRating: Float? = null
        ) {

            try {
                supabase.from(Table.FavInfo.name).insert(FavInfo(resFav, false, 0.0f))
            } catch (e: Exception) {
            }

            supabase.from(Table.FavInfo.name).update({
                isFavorite?.let {
                    set("isFavorate", it)
                }

                isRating?.let {
                    set("star", it)
                }

            }) {
                filter {
                    eq("favId", Json.encodeToString(resFav))
                }
            }

            val favcount = supabase.from(Table.FavInfo.name).select {
                filter {
                    like("favId", "%\"restaurantId\":\"${resFav.restaurantId}\"%")
                    eq("isFavorate", true)
                }
            }.decodeList<FavInfo>().size



            supabase.from(Table.RestaurantInfo.name).update({
                set("numberOfRatings", favcount)
            }) {
                filter {
                    eq("channel_id", resFav.restaurantId.toString())
                }
            }


        }

        suspend fun upsertFav(
            foodFav: FoodFav,
            isFavorite: Boolean? = null,
            isRating: Float? = null
        ) {

            try {
                supabase.from(Table.FavInfo.name).insert(FavInfo(foodFav, false, 0.0f))
            } catch (e: Exception) {
            }

            supabase.from(Table.FavInfo.name).update({
                isFavorite?.let {
                    set("isFavorate", it)
                }

                isRating?.let {
                    set("star", it)
                }

            }) {
                filter {
                    eq("favId", Json.encodeToString(foodFav))
                }
            }

            val favcount = supabase.from(Table.FavInfo.name).select {
                filter {
                    like("favId", "%\"foodId\":${foodFav.foodId}%")
                    eq("isFavorate", true)
                }
            }.decodeList<FavInfo>().size

            // {"customerId":"8b0aa90e-3c0c-4872-8fda-d191ddf70760","tableName":"RestaurantInfo","restaurantId":"c4a1e7f4-c884-4591-b57c-ed7aed7b9005"}

            supabase.from(Table.FoodInfo.name).update({
                set("numberOfRatings", favcount)
            }) {
                filter {
                    FoodInfo::id eq foodFav.foodId
                }
            }

        }


        suspend fun getFav(resFav: ResFav): FavInfo {
            return try {
                supabase.from(Table.FavInfo.name).select {
                    filter {
                        eq("favId", Json.encodeToString(resFav))
                    }
                }.decodeSingle<FavInfo>()
            } catch (e: Exception) {
                FavInfo(resFav, false, 0.0f)
            }

        }

        suspend fun getFav(foodFav: FoodFav): FavInfo {
            return try {
                supabase.from(Table.FavInfo.name).select {
                    filter {
                        eq("favId", Json.encodeToString(foodFav))
                    }
                }.decodeSingle<FavInfo>()
            } catch (e: Exception) {
                FavInfo(foodFav, false, 0.0f)
            }

        }

    }

}