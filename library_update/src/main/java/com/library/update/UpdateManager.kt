package com.library.update

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.library.update.data.ApkFile
import com.library.update.data.UpdateEntity
import java.lang.Exception

class UpdateManager {

    private var context: Context? = null
    private var updateConfig: UpdateConfig? = null

    fun start(context: Context, config: UpdateConfig) {
        this.context = context
        this.updateConfig = config
        val updateEntity = config.updateEntity
        if (needUpdate(updateEntity)) {
            alterDialog(updateEntity)
        }
    }

    private fun alterDialog(updateEntity: UpdateEntity) {
        AlertDialog.Builder(context!!)
                .setTitle("有新版本：${updateEntity.versionName}")
                .setMessage("本次更新内容：${updateEntity.updateDesc}")
                .setCancelable(false)
                .setPositiveButton("立即更新") { _, _ -> download(updateEntity) }
                .setNegativeButton("以后再说") { dialog, _ -> dialog!!.dismiss() }
                .show()
    }

    private fun download(updateEntity: UpdateEntity) {
        val dialog = ProgressDialog(context)
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setMessage("下载中......")
        dialog.isIndeterminate = false
        dialog.setCancelable(false)
        dialog.show()
        var listener = object : DownloadListener {
            override fun progress(progress: Int) {
                dialog.progress = progress
            }

            override fun success() {
                dialog.dismiss()
                installApk()
            }

            override fun error() {
                dialog.dismiss()
                var activity = context as Activity
                activity.runOnUiThread {
                    Toast.makeText(activity, "下载失败,请检查网络，手机存储空间，存储权限等...", Toast.LENGTH_LONG).show()
                }
            }
        }
        try {
            var file = updateConfig!!.apkFile.file
            DownloadAsyncTask(listener, file).execute(updateEntity.apkUrl)
        } catch (e: Exception) {
            listener.error()
        }
    }

    private fun needUpdate(updateEntity: UpdateEntity): Boolean {
        context?.let {
            val packageInfo = it.packageManager.getPackageInfo(it.packageName, 0)
            val versionCode = packageInfo.versionCode
            return updateEntity.versionCode > versionCode
        }
        return false
    }

    private fun installApk() {
        var type = "application/vnd.android.package-archive"
        var (file, uri) = updateConfig?.apkFile!!
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, type)
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }


    interface UpdateConfig {
        val apkFile: ApkFile
        val updateEntity: UpdateEntity
    }
}