package com.manway.Toofoh.interfaces

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path


interface SplitInterface {
    @Headers(
        "Content-Type: application/json",
        "x-client-id: TEST1043287341b9ca61150128c98e6837823401",
        "x-client-secret: cfsk_ma_test_ffcc8cfe70e87db988fef48f0a2d940b_f767a3c0",
        "x-api-version: 2022-09-01"
    )
    @POST("pg/easy-split/vendors/{vendor_id}/adjustment")
    fun adjustVendorPayment(
        @Path("vendor_id") vendorId: String,
        @Body request: AdjustmentRequest,
    ): Call<Void>
}

data class AdjustmentRequest(
    val vendor_id: String,
    val adjustment_id: Int,
    val amount: Int,
    val type: String,
    val remarks: String
)
