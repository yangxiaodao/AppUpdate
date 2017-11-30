package com.library.update

import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DownloadAsyncTask(
        private var downloadListener: DownloadListener,
        private var apkFile: File) :
        AsyncTask<String, Int, Void>() {

    private var mTimeLast: Long = 0

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val time = System.currentTimeMillis()
        if (time - mTimeLast >= 300) {
            downloadListener.progress(values[0]!!)
            mTimeLast = time
        }
        if (100 == values[0]) {
            downloadListener.success()
        }
    }

    override fun doInBackground(vararg params: String?): Void? {
        val connection: HttpURLConnection
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            connection = URL(params[0]).openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept", "application/*")
            connection.connectTimeout = 10000
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.inputStream
                val fileSize = connection.contentLength.toDouble()
                var fileSizeDownloaded = 0.0
                val fileReader = ByteArray(1024)
                outputStream = FileOutputStream(apkFile)
                var read: Int
                while (true) {
                    read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    } else {
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read
                        val progress = ((fileSizeDownloaded / fileSize) * 100).toInt()
                        publishProgress(progress)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            downloadListener.error()
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (outputStream != null)
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }
        return null
    }

}