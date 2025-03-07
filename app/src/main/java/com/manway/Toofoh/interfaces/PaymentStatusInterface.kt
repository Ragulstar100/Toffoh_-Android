package com.example.paymentc.interfaces

import com.manway.Toofoh.models.PaymentStatusModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface PaymentStatusInterface {

    @GET("/pg/orders/{order_id}")
    @Headers(
        "accept: application/json",
        "x-client-id: TEST1043287341b9ca61150128c98e6837823401",
        "x-client-secret: cfsk_ma_test_ffcc8cfe70e87db988fef48f0a2d940b_f767a3c0",
        "x-api-version: 2022-01-01"
    )
    fun create(
        @Path("order_id") orderId: String
    ): Call<PaymentStatusModel>
}


//interface PaymentStatusInterface {
//
//    @GET("/pg/orders/{order_id}")
//    @Headers(
//        "accept: application/json",
//        "x-client-id: *******YOUR_APP_ID(Client-id)********",
//        "x-client-secret: ******YOUR_SECRET_KEY*******",
//        "x-api-version: 2022-01-01"
//    )
//    fun create(
//        @Path("order_id") orderId: String
//    ):Call<PaymentStatusModel>
//}