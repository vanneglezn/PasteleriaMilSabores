package com.example.pasteleriamilsabores.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalApiService {

    @GET("vanneglezn/56aae097c1bdb69ab0565f7ab66216ee/raw/productos.json")
    suspend fun getProductsFromGist(
        @Query("timestamp") timestamp: Long // evita caché y trae cambios al instante
    ): List<ApiProduct>
}
