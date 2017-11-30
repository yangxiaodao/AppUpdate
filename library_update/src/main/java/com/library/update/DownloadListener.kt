package com.library.update

interface DownloadListener {
    fun progress(progress: Int)
    fun success()
    fun error()
}