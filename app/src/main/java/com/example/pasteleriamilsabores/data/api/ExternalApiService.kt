package com.example.pasteleriamilsabores.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalApiService {

    @GET("vanneglezn/56aae097c1bdb69ab0565f7ab66216ee/raw/5af6c5c2075fa240925b40cfdf002321e3c9d494/productos.json")
    suspend fun getProductsFromGist(
        @Query("t") timestamp: Long
    ): List<ApiProduct>
}
