package io.wongxd.kotlin_tips.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import io.wongxd.kotlin_tips.BuildConfig
import java.io.File
import java.util.*


data class UpdateInfo(
    var info: String? = null,
    var ver: String? = null,
    var downloadUrl: String? = null
)


class UpdateUtil {

    private lateinit var me: Context
    private lateinit var packageName: String
    private var onDownloadListener: OnDownloadListener? = null

    private var downloadManager: DownloadManager? = null
    private var downloadId: Long = 0
    private var mReceiver: DownloadFinishReceiver? = null
    private var file: File? = null

    private var downloadProgressTimer: Timer? = null

    private var isDownloadCompleted = false

    private var isForced = false

    val isWifi: Boolean
        get() {
            val connectivityManager = me.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            @SuppressLint("MissingPermission") val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
        }

    private var progressDialog: ProgressDialog? = null

    private constructor() {}

    constructor(me: Context, packageName: String) {
        this.me = me
        this.packageName = packageName
        downloadManager = me.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    fun doUpdate(updateInfo: UpdateInfo?): Boolean {
        if (updateInfo == null) {
            return false
        }
        mReceiver = DownloadFinishReceiver()
        me.registerReceiver(mReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        log("开始下载：" + updateInfo.downloadUrl!!)
        val ver = if (updateInfo.ver == null) "" else "_" + updateInfo.ver!!
        file = File(me.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$packageName$ver.apk")
        if (file!!.exists()) file!!.delete()       //文件存在则删除
        val path = Uri.fromFile(file)
        log("下载到:$path")

        val request = DownloadManager.Request(Uri.parse(updateInfo.downloadUrl))
        request.setDestinationUri(path)
        request.setMimeType("application/vnd.android.package-archive")
        request.setTitle(progressDialogTitle)
        request.setDescription(progressDescription)
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE or android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadId = downloadManager!!.enqueue(request)
        if (onDownloadListener != null) onDownloadListener!!.onStart(downloadId)
        doGetProgress()
        showProgressDialog()

        return true
    }

    private fun OpenWebBrowserAndOpenLink(downloadUrl: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(downloadUrl)
        intent.data = content_url
        me.startActivity(intent)
    }

    private fun doGetProgress() {
        if (downloadProgressTimer != null) downloadProgressTimer!!.cancel()
        downloadProgressTimer = Timer()
        downloadProgressTimer!!.schedule(object : TimerTask() {
            override fun run() {
                val progress = getProgress(downloadId)
                log(progress)
                if (onDownloadListener != null)
                    onDownloadListener!!.onDownloading(downloadId, progress)
                if (progress != 100) {
                    if (progressDialog != null) progressDialog!!.progress = progress
                } else {
                    downloadProgressTimer!!.cancel()
                    if (progressDialog != null) progressDialog!!.dismiss()
                    isDownloadCompleted = true
                    installApk(me)

                    if (onDownloadListener != null) onDownloadListener!!.onSuccess(downloadId)
                    return
                }
            }
        }, 100, 10)
    }

    fun installApk(context: Context) {
        if (!isDownloadCompleted) {
            log("请先确保下载完成")
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri = FileProvider.getUriForFile(context, "$packageName.fileProvider", file!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.setDataAndType(
                Uri.fromFile(getRealFileInAndroudM(context, downloadId)),
                "application/vnd.android.package-archive"
            )
        } else {
            intent.setDataAndType(
                Uri.fromFile(file),
                "application/vnd.android.package-archive"
            )
        }
        context.startActivity(intent)
    }

    private fun getRealFileInAndroudM(context: Context, downloadId: Long): File? {
        var file: File? = null
        val downloader = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
        if (downloadId != -1L) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            val cur = downloader.query(query)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    val uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if (!uriString.isEmpty()) {
                        file = File(Uri.parse(uriString).path)
                    }
                }
                cur.close()
            }
        }
        return file
    }

    private fun getProgress(downloadId: Long): Int {
        val query = DownloadManager.Query()
            .setFilterById(downloadId)
        var cursor: Cursor? = null
        var progress = 0
        try {
            cursor = downloadManager!!.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val downloadSoFar =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))       //当前的下载量
                val totalBytes =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))          //文件总大小
                progress = (downloadSoFar * 1.0f / totalBytes * 100).toInt()
            }
        } finally {
            cursor?.close()
        }
        return progress
    }

    @JvmOverloads
    fun showNormalUpdateDialog(
        updateInfo: UpdateInfo,
        titleStr: String = updateTitle,
        downloadByShopStr: String? = "从应用商店下载",
        downloadNowStr: String? = "立即下载",
        cancelStr: String? = "取消",
        isForced: Boolean = false
    ): UpdateUtil {
        var cancelStr = cancelStr
        isShowProgressDialog = true
        this.isForced = isForced
        val builder: android.support.v7.app.AlertDialog.Builder = android.support.v7.app.AlertDialog.Builder(me)
        val alertDialog = builder.create()
        alertDialog.setTitle(titleStr)
        alertDialog.setCancelable(!isForced)
        alertDialog.setMessage(updateInfo.info)
        if (downloadNowStr != null) {
            alertDialog.setButton(
                android.content.DialogInterface.BUTTON_POSITIVE,
                downloadNowStr
            ) { dialog, which -> doUpdate(updateInfo) }
        }
        if (downloadByShopStr != null) {
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, downloadByShopStr) { dialog, which -> }
            //            builder.setNeutralButton(downloadByShopStr, new DialogInterface.OnClickListener() {
            //                @Override
            //                public void onClick(DialogInterface dialog, int which) {
            //                    openMarket();
            //                }
            //            });
        }
        if (cancelStr == null) cancelStr = "CANCEL"
        if (!isForced) {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelStr) { dialog, which -> }
        }
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener { openMarket() }
        }
        alertDialog.show()
        return this
    }

    private fun openMarket() {
        try {
            val str = "market://details?id=$packageName"
            val localIntent = Intent(Intent.ACTION_VIEW)
            localIntent.data = Uri.parse(str)
            me.startActivity(localIntent)
        } catch (e: Exception) {
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            e.printStackTrace()
            // 调用系统浏览器进入商城
            openLinkBySystem("https://www.coolapk.com/apk/$packageName")
        }

    }

    private fun openLinkBySystem(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        me.startActivity(intent)
    }

    private inner class DownloadFinishReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //            if (onDownloadListener != null) onDownloadListener.onSuccess(downloadId);
        }
    }

    fun getOnDownloadListener(): OnDownloadListener? {
        return onDownloadListener
    }

    fun setOnDownloadListener(onDownloadListener: OnDownloadListener): UpdateUtil {
        this.onDownloadListener = onDownloadListener
        return this
    }

    interface OnDownloadListener {

        fun onStart(downloadId: Long)

        fun onDownloading(downloadId: Long, progress: Int)

        fun onSuccess(downloadId: Long)

        fun onCancel(downloadId: Long)

    }

    interface OnDownloadingListener

    private fun log(o: Any) {
        if (BuildConfig.DEBUG)
            Log.d(">>>", o.toString())
    }

    fun showProgressDialog() {
        if (!isShowProgressDialog) return
        progressDialog = ProgressDialog(me)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)// 设置水平进度条
        progressDialog?.setCancelable(!isForced)// 设置是否可以通过点击Back键取消
        progressDialog?.setCanceledOnTouchOutside(false)// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog?.setTitle(progressDialogTitle)
        progressDialog?.max = 100
        if (!isForced) {
            progressDialog?.setButton(
                android.content.DialogInterface.BUTTON_POSITIVE, hideProgressDialogButtonCaption
            ) { dialog, which -> progressDialog?.dismiss() }
        }
        progressDialog?.setButton(
            DialogInterface.BUTTON_NEUTRAL, cancelProgressDialogButtonCaption
        ) { dialog, which ->
            progressDialog?.dismiss()
            cancel()
        }
        progressDialog?.show()
    }

    fun cancel() {
        if (mReceiver != null) me.unregisterReceiver(mReceiver)
        if (downloadManager != null && downloadId != 0L) downloadManager?.remove(downloadId)
        if (onDownloadListener != null) onDownloadListener?.onCancel(downloadId)
        if (downloadProgressTimer != null) downloadProgressTimer?.cancel()
    }

    private fun isNull(s: String?): Boolean {
        return s == null || s.trim { it <= ' ' }.isEmpty() || s == "null"
    }

    companion object {

        var updateTitle = "发现新的版本"
        var isShowProgressDialog = false          //是否显示默认更新进度对话框
        var hideProgressDialogButtonCaption = "后台下载"
        var cancelProgressDialogButtonCaption = "取消下载"
        var progressDialogTitle = "正在下载更新"
        var progressDescription = ""
    }
}



