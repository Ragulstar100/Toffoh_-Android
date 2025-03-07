package com.example.paymentc.interfaces


import com.manway.Toofoh.models.Data
import com.manway.Toofoh.models.PaymentModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentInterface {
    @POST("/pg/orders")
    @Headers(
        "Content-Type: application/json",
        "x-client-id: TEST1043287341b9ca61150128c98e6837823401",
        "x-client-secret: cfsk_ma_test_ffcc8cfe70e87db988fef48f0a2d940b_f767a3c0",
        "x-api-version: 2022-09-01",
        "Accept: application/json"
    )
    fun getOrderID(
        @Body data: Data
    ): Call<PaymentModel>
}