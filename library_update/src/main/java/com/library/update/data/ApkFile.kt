package com.library.update.data

import android.net.Uri
import java.io.File

data class ApkFile(
        var file: File,
        var uri: Uri
)