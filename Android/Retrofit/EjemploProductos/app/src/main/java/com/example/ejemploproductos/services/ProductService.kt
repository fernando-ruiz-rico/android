package com.example.ejemploproductos.services

import com.example.ejemploproductos.model.Product
import com.example.ejemploproductos.model.ProductInsert
import com.example.ejemploproductos.model.ProductListResponse
import com.example.ejemploproductos.model.SingleProductResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductService {
  @GET("products")
  suspend fun getProducts(): ProductListResponse
  @GET("products/{id}")
  suspend fun getProduct(@Path("id") id: Int): SingleProductResponse
  @POST("products")
  suspend fun createProduct(@Body request: ProductInsert): SingleProductResponse
  @PUT("products/{id}")
  suspend fun updateProduct(@Body request: ProductInsert, @Path("id") id: Int)
  @DELETE("products/{id}")
  suspend fun deleteProduct(@Path("id") id: Int)
}
