package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("top-headlines?q=cancer&category=health&language=en&apiKey=${BuildConfig.API_KEY}")
    fun getCancerNews(): Call<NewsResponse>
}