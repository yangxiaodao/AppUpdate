package com.app.update

import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.library.update.UpdateManager
import com.library.update.data.ApkFile
import com.library.update.data.UpdateEntity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            UpdateManager().start(
                    this@MainActivity,
                    object : UpdateManager.UpdateConfig {
                        override val apkFile: ApkFile = getApkFile()
                        override val updateEntity: UpdateEntity = checkNewVersion()
                    })
        }
    }

    fun checkNewVersion(): UpdateEntity {
        return UpdateEntity(
                "http://imtt.dd.qq.com/16891/1CCA1665B54F7218FFB1E4ADB07CBAB7.apk?fsname=com.tencent.gamehelper.smoba_2.3.2.1018_170101801.apk&csr=1bbd",
                2,
                "2.0",
                "Fix Bugs"
        )
    }

    fun getApkFile(): ApkFile {
        var file = File(Environment.getExternalStorageDirectory(), "MYAPK" + File.separator + "APK")
        if (!file.exists()) file.mkdirs()
        var apk = File(file, "${System.currentTimeMillis()}.apk")
        return ApkFile(apk, FileProvider.getUriForFile(this@MainActivity, "com.app.update", apk))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
