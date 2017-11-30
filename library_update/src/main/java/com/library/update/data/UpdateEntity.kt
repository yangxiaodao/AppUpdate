package com.library.update.data

data class UpdateEntity(
        var apkUrl: String?,
        var versionCode: Int,
        var versionName: String,
        var updateDesc: String
)