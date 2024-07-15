package com.lens.taskmanager.data.remote.models

sealed class NetworkResult<T>(
    val httpStatus: String? = null,
    val httpCode: Int? = null,
    val data: T? = null
) {
    class Success<T>(httpCode: Int, httpMessage: String, data: T) : NetworkResult<T>(httpMessage, httpCode, data)

    class Error<T>(httpCode: Int, httpMessage: String, data: T? = null) : NetworkResult<T>(httpMessage, httpCode, data)

    class Loading<T> : NetworkResult<T>()
}
