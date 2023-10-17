package com.nishant4820.studentapp.utils

object Constants {

    const val BASE_URL = "https://gateway-gazh.onrender.com/"

    const val ARG_PARAM1 = "param1"
    const val ARG_PARAM2 = "param2"
    const val LOG_TAG = "StudentLogTag"

    // API Queries
    const val ENROLLMENT_NUMBER = "enrollment"
    const val QUERY_SOCIETY = "society"
    const val QUERY_STUDENT_ID = "studentId"
    const val QUERY_OFFSET = "offset"
    const val QUERY_LIMIT = "limit"

    // ROOM DATABASE
    const val DATABASE_NAME = "notices_database"
    const val NOTICES_TABLE = "notices_table"

    // Bottom Sheet and Preferences
    const val DEFAULT_OFFSET = "0"
    const val DEFAULT_LIMIT = "10"

    const val PREFERENCES_NAME = "student_settings"
    const val PREFERENCES_TOKEN = "student_token"
    const val PREFERENCES_ID = "student_id"
    const val PREFERENCES_SOCIETY = "selected_society"
    const val PREFERENCES_SOCIETY_CHIP_ID = "selected_society_chip_id"
    const val PREFERENCES_IS_UPLOADED_BY_ME = "is_uploaded_by_me"
    const val PREFERENCES_BACK_ONLINE = "back_online"

    // Network Result Status Codes
    const val NETWORK_RESULT_STATUS_UNKNOWN = 0
    const val NETWORK_RESULT_STATUS_NO_INTERNET = 1
    const val NETWORK_RESULT_STATUS_TIMEOUT = 2
    const val NETWORK_RESULT_STATUS_LOADING = 3

    // Network Result Messages
    const val NETWORK_RESULT_MESSAGE_UNKNOWN = "Some Error Occurred"
    const val NETWORK_RESULT_MESSAGE_NO_INTERNET = "No Internet Connection"
    const val NETWORK_RESULT_MESSAGE_TIMEOUT = "Request timed out"
    const val NETWORK_RESULT_MESSAGE_LOADING = "Loading"
    const val NETWORK_RESULT_MESSAGE_NO_RESULTS = "No Results found"
}