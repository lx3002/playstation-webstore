package com.example.webstore.Seller.Checkout.others
import com.example.webstore.Seller.Checkout.models.Checkoutmodel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface CheckoutApi {
    @POST("newcheckoutpay")
    fun simulateCheckout(@Body checkoutmodel: Checkoutmodel?):Call<Checkoutmodel?>
}