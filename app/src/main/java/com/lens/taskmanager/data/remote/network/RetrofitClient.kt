package com.lens.taskmanager.data.remote.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitClient(
    private val factory: GsonConverterFactory,
    private val client: OkHttpClient
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var baseUrl = "taskmanager.com/api/v1"


    val isApiClientReady
        get() = baseUrl.isNotBlank()

    private fun getBuilder() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(factory)
        .client(client)

    // FOR TESTING
    fun showBaseUrl() {
        println(baseUrl)
    }
}
