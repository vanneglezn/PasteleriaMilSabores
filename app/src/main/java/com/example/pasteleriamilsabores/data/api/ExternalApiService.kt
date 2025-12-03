package com.example.pasteleriamilsabores.data.api

import retrofit2.http.GET

interface ExternalApiService {

    // 💡 API 2: CONSUMIR EL GIST DE PRODUCTOS
    // La URL base del cliente será https://gist.githubusercontent.com/
    // El resto del path se añade aquí:
    @GET("vanneglezn/56aae097c1bdb69ab0565f7ab66216ee/raw/03476429393ae42a546a966c9f2406c3b7e73514/productos.json")
    suspend fun getProductsFromGist(): List<ApiProduct>
}