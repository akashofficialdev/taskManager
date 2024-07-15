package com.lens.taskmanager.data.remote.network

import android.util.Log
import com.lens.taskmanager.data.remote.models.BaseApiResponse
import com.lens.taskmanager.data.remote.models.NetworkResult
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

suspend inline fun <reified T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    try {
        val response = apiCall()
        // Success = responseBody will be parsed into `Response<T>`
        // Error = responseBody will be a string of the body we receive in the case of an error
        val responseBody: Any? = if (response.isSuccessful) {
            response.body()
        } else {
            response.errorBody()
        }

        responseBody?.let { body ->
            if (body is T) {
                Log.i("safeApiCall", "response body: $body")
                return NetworkResult.Success(response.code(), response.message(), body)
            } else {
                val errorResponseBody = responseBody as ResponseBody
                Log.i("safeApiCall", "error response body: $errorResponseBody")
                // parsing the `errorBody` into the `BaseApiResponse<T>` as the `errorBody` will be a non-parsed string
                val parsedBody = BaseApiResponse.fromJson<BaseApiResponse<T>>(errorResponseBody.string()) as T
                Log.e("safeApiCall", "parsedBody: $parsedBody")
                return NetworkResult.Error(response.code(), response.message(), parsedBody)
            }
        }

        Log.e("safeApiCall", "response error: ${response.code()} ${response.message()}")
        return error("Response body is null or couldn't be parsed into a success or an error response", response.code())
    } catch (e: IOException) {
        Log.e("safeApiCall", "IOException: ${e.message}")
        return error(e.message ?: e.toString())
    }
}

fun <T> error(errorMessage: String, errorCode: Int = -1): NetworkResult<T> =
    NetworkResult.Error(errorCode, "Api call failed: $errorMessage")
