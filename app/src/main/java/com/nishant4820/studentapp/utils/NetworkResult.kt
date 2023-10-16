package com.nishant4820.studentapp.utils

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val statusCode: Int? = null
) {

    class Success<T>(data: T, message: String?, statusCode: Int?) : NetworkResult<T>(data, message, statusCode)
    class Error<T>(message: String?, statusCode: Int?) : NetworkResult<T>(null, message, statusCode)
    class Loading<T>(message: String?, statusCode: Int?) : NetworkResult<T>(null, message, statusCode)

}