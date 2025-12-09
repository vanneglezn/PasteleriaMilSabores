package com.example.pasteleriamilsabores.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val GIST_BASE_URL = "https://gist.githubusercontent.com/"

    private val gistRetrofit = Retrofit.Builder()
        .baseUrl(GIST_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productApiService: ExternalApiService by lazy {
        gistRetrofit.create(ExternalApiService::class.java)
    }
}
