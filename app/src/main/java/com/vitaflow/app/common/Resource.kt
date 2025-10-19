package com.vitaflow.app.common

import retrofit2.Response
import java.io.IOException

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)

}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
    return try {
        val res = apiCall.invoke()
        if (res.isSuccessful) {
            val body = res.body()
            if (body != null) {
                Resource.Success(data = body)
            } else {
                Resource.Error(message = "Response body is null")
            }
        } else {
            Resource.Error(message = res.message() ?: "Unknown error occurred")
        }
    } catch (e: Exception) {
        Resource.Error(message = e.message ?: "Unknown error occurred")
    }
}