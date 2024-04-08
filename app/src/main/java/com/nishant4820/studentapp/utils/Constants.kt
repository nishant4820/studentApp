package com.nishant4820.studentapp.utils

object Constants {

    const val BASE_URL = "http://api.bpitindia.ac.in/"

    const val LOG_TAG = "StudentLogTag"

    // API Queries
    const val ENROLLMENT_NUMBER = "enrollment_number"
    const val QUERY_SOCIETY = "society"
    const val QUERY_STUDENT_ID = "studentId"
    const val QUERY_OFFSET = "offset"
    const val QUERY_LIMIT = "limit"
    const val FORMAT = "format"
    const val JSON = "json"

    // ROOM DATABASE
    const val DATABASE_NAME = "my_database"
    const val NOTICES_TABLE = "notices_table"
    const val SETTINGS_TABLE = "settings_table"
    const val RESULTS_TABLE = "results_table"
    const val PROFILE_TABLE = "profile_table"

    // Bottom Sheet and Preferences
    const val DEFAULT_OFFSET = "0"
    const val DEFAULT_LIMIT = "10"

    const val PREFERENCES_TOKEN = "student_token"
    const val PREFERENCES_ID = "student_id"
    const val PREFERENCES_ENROLLMENT = "student_enrollment"
    const val PREFERENCES_SOCIETY = "selected_society"
    const val PREFERENCES_SOCIETY_CHIP_ID = "selected_society_chip_id"
    const val PREFERENCES_IS_UPLOADED_BY_ME = "is_uploaded_by_me"

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
    const val NETWORK_RESULT_MESSAGE_NO_RESULTS = "No results found"

    // General Messages
    const val MESSAGE_NOTICE_UPLOADED = "Notice Uploaded Successfully"
    const val MESSAGE_FILE_UPLOADED = "File Uploaded Successfully"
}