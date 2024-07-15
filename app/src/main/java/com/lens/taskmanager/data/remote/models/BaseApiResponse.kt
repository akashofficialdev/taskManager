package com.lens.taskmanager.data.remote.models

import com.google.gson.Gson

data class BaseApiResponse<T>(
    val status: Long,
    val message: String,
    val success: Boolean = false,
    val data: T?
) {
    companion object {
        fun <T> fromJson(json: String): BaseApiResponse<T> {
            return Gson().fromJson<BaseApiResponse<T>>(json, BaseApiResponse::class.java) ?: BaseApiResponse(
                -1, "Couldn't parse errorBody Json into BaseApiResponse", false, null
            )
        }
    }
}
