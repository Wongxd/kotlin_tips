package io.wongxd.kotlin_tips.log.core

import android.content.Context
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.util.*

object ZipLogFile {
    private val TAG = "ZipLogFile"

    /**
     * 压缩日志文件 返回日志文件路径
     *
     *
     * 建议：异步任务中执行
     *
     * @param context
     * @return
     */
    fun zipLogFiles(context: Context?): File? {
        Log.d(TAG, "---zipLogFiles---")
        if (context == null) {
            return null
        }
        val appFileDir = KtLog.appLogDir ?: ""
        Log.d(TAG, "appFileDir: $appFileDir")
        // 日志文件尚未初始化
        if (!TextUtils.isEmpty(appFileDir)) {
            // 当前时间作为文件名
            val fileName = "kotlin_tips_log.zip"
            // 最终文件路径
            val zipFile = File(KtLogFileUtils.getAppCachePath(context), fileName)
            Log.d(TAG, "zipFile: $zipFile")
            //
            Log.d(TAG, "start zip")
            val filePaths = ArrayList<String>()
            filePaths.add(appFileDir)
            val flag = KtLogFileUtils.exportZipFromPaths(filePaths, zipFile)
            if (flag) {
                Log.d(TAG, "zip log file successfully")
                return zipFile
            }
        }
        return null
    }


}
