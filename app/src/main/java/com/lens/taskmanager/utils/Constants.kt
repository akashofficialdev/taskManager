package com.lens.taskmanager.utils

const val USER_DATA_STORE_FILE_NAME = "user_prefs.pb"
const val SERVERS_DATA_STORE_FILE_NAME = "servers_prefs.pb"
const val PROJECTS_DATA_STORE_FILE_NAME = "project_data.pb"

const val CONNECTION_TIMEOUT: Long = 15

enum class StatusCodeConstants(val statusCode: Int) {
    Unauthorized
        (401),
    NotFound
        (404),
    UnprocessedEntityException
        (422),
    OtpSentSuccessfully
        (2101),
    OtpVerifiedSuccessfully
        (2104),
    WrongEmail
        (902),
}

const val DEVICE_TYPE = "ANDROID"
const val TASK_DATABASE = "task_database"
const val TASK_TABLE = "task_table"

object Priority {
    const val HIGH = "High"
    const val LOW = "Low"
    const val MEDIUM = "Medium"
}


