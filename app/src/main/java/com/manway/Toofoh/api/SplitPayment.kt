package com.manway.Toofoh.api

import com.example.paymentc.interfaces.PaymentInterface
import com.manway.Toofoh.interfaces.SplitInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SplitPaymentAPI {

    private var retrofit: Retrofit? = null


    fun splitPayment(): SplitInterface {

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://sandbox.cashfree.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(SplitInterface::class.java)
    }

}