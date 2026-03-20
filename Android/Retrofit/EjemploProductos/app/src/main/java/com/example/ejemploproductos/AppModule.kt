package com.example.ejemploproductos

import com.example.ejemploproductos.services.ProductService
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@ComponentScan("com.example.ejemploproductos") // Ajusta esto a tu paquete principal
class AppModule {

  @Single
  fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
      .baseUrl("https://api.fullstackpro.es/products-example/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Single
  fun provideProductService(retrofit: Retrofit): ProductService {
    return retrofit.create(ProductService::class.java)
  }
}
