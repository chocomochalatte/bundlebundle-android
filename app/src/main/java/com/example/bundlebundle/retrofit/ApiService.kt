package com.example.bundlebundle.retrofit

import com.example.bundlebundle.group.GroupMemberCartVO
import com.example.bundlebundle.retrofit.dataclass.Cart
import com.example.bundlebundle.retrofit.dataclass.GroupIdVO
import com.example.bundlebundle.retrofit.dataclass.ProductVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {

    @POST("individual/{id}")
    fun getInfo(
        @Query("id") memberId: Int
    ): Call<Cart>

    @GET("products")
    fun showProducts(
        @Query("sort") sortType: String
    ): Call<List<ProductVO>>

    @GET("products/{productId}")
    fun showProductDetail(
        @Path("productId") productId: Int
    ): Call<ProductVO>

    @GET("member/group-check")
    fun checkIfGroupIsPresent(
        @Header("Authorization") accessToken: String
    ): Call<GroupIdVO>

    @POST("cart/group")
    fun addToGroupCart(
    ): Call<GroupMemberCartVO>

    @POST("cart")
    fun addToPersonalCart(
    ): Call<GroupMemberCartVO>

}