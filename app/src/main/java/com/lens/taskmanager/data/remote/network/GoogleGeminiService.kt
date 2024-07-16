package com.lens.taskmanager.data.remote.network
import okhttp3.*
import com.google.gson.Gson
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.data.remote.models.UserSuggestionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GoogleGeminiService(apiKey: String) {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun generateTaskSuggestion(userBehaviorData: List<TaskEntity>): String {
        val myHeaders = Headers.Builder()
            .add("Content-Type", "application/json")
            .build()

        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            gson.toJson(mapOf(
                "contents" to listOf(
                    mapOf(
                        "parts" to listOf(
                            mapOf(
                                "text" to "Generate a task suggestion based on this user data. Simply give the response in a statement of 100 words.: $userBehaviorData"
                            )
                        )
                    )
                )
            ))
        )

        val request = Request.Builder()
            .url(geminiApiUrl)
            .post(requestBody)
            .headers(myHeaders)
            .build()

        return withContext(Dispatchers.IO) {
            val maxRetries = 6
            val retryDelayBase = 2000 // in milliseconds

            for (attempt in 0 until maxRetries) {
                try {
                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) {
                        if (response.code() == 429 && attempt < maxRetries - 1) {
                            // Handle rate limiting
                            val retryAfter = response.header("Retry-After")?.toInt() ?: retryDelayBase * (attempt + 1)
                            println("Rate limited, retrying after $retryAfter ms...")
                            kotlinx.coroutines.delay(retryAfter.toLong())
                            continue // Retry the request
                        } else if (response.code() == 403) {
                            // Handle forbidden error
                            println("Forbidden error: ${response.message()}")
                            return@withContext "Forbidden error: Check API key and permissions"
                        }
                        throw IOException("HTTP error! status: ${response.code()}")
                    }

                    val responseBody = response.body()?.string()
                    println("API Response: $responseBody") // Log the response

                    val responseObject = gson.fromJson(responseBody, UserSuggestionResponse::class.java)
                    val suggestions = responseObject.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No suggestion available"
                    return@withContext suggestions

                } catch (e: IOException) {
                    println("Error fetching feedback for comment: ${e.message}")
                    if (attempt == maxRetries - 1) {
                        throw e
                    }
                }
            }
            "Error fetching feedback"
        }
    }
}
