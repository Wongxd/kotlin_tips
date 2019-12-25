package io.wongxd.kotlin_tips.log.core

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.Executors


/**
 * 日志工具
 * 1、debug 为 true 时，展示在控制台；同时打印到文件中；
 * 2、debug 为 fase 时，只打印到文件中；
 *
 *
 * 文件目录：/data/data/%packetname%/files/
 */
class KtLog {


    companion object {
        // App可通过该tag 查看日志信息
        val APP_TAG = "kotlin_tips"

        /**
         * config
         */
        // 日志输出级别
        private val LOG_LEVEL = Log.VERBOSE
        // 日志文件的最大长度
        private val LOG_MAXSIZE = 6 * 1024 * 1024
        // 文件名称
        private val LOG_FILE_TEMP = "log.temp"
        private val LOG_FILE_LAST = "log_last.txt"
        // 输出到控制台 or 输出到文件
        private val SHOW_LOG_CONSOLE = 0x01 // 输出到控制台
        private val SHOW_LOG_FILE = 0x10 // 输出到文件
        // debug模式：打印到控制台 和 文件
        // release模式：打印到文件
        private val MODEL_DEBUG = SHOW_LOG_CONSOLE or SHOW_LOG_FILE
        private val MODEL_RELEASE = SHOW_LOG_FILE
        // 单线程池
        private val mExecutorService = Executors.newFixedThreadPool(1)
        //
        // /data/data/%packetname%/files/
        private var mAppLogDir: String? = null


        /**
         * 日志文件夹
         *
         * @return
         */
        val appLogDir: String?
            get() {
                Log.d("KtLog", "getAppLogDir： " + mAppLogDir!!)
                return mAppLogDir
            }

    }


    /**
     * 变量数据
     */
    // debug 开关
    private var DEBUG = true

    // 变量锁
    private val lockObj = Any()
    // mLogFilePreName + netease_log_last.txt
    private var mLogFilePreName = "ui"
    //
    // 当前时间
    private val mCalendar = Calendar.getInstance()
    //
    // 文件输入流
    private var mTempLogFileStream: OutputStream? = null
    // 当前文件大小
    private var mTempLogFileSize: Long = 0

    /**
     * debug 模式开关
     *
     * @param isDebug
     */
    fun setDebug(isDebug: Boolean) {
        this.DEBUG = isDebug
    }

    /**
     * 设置日志文件的 pre name
     *
     * @param prefix
     */
    fun setLogFilePreName(prefix: String?) {
        Log.d("KtLog", "setLogPreName： " + prefix!!)
        if (prefix != null && prefix != "") {
            mLogFilePreName = prefix
        }
    }


    /**
     * 初始化文件路径
     *
     * @param context
     */
    fun init(context: Context) {
        Log.d("KtLog", "---iniAppPath---")
        //
        synchronized(lockObj) {
            // /data/data/%packetname%/files/log/
            mAppLogDir = context.filesDir.path + File.separator + "log" + File.separator
            // 创建对应的路径
            val dir = File(mAppLogDir)
            if (!dir.exists()) {
                dir.mkdir()
            }
            Log.d("KtLog", "mAppLogDir: " + mAppLogDir!!)
        }
    }


    // ###################################公共方法 begin##########################################


    fun d(tagFromUser: String, msgFromUser: String) {
        if (DEBUG) {
            showLog(tagFromUser, msgFromUser, MODEL_DEBUG, Log.DEBUG)
        } else {
            showLog(tagFromUser, msgFromUser, MODEL_RELEASE, Log.DEBUG)
        }
    }

    fun v(tagFromUser: String, msgFromUser: String) {
        if (DEBUG) {
            showLog(tagFromUser, msgFromUser, MODEL_DEBUG, Log.VERBOSE)
        } else {
            showLog(tagFromUser, msgFromUser, MODEL_RELEASE, Log.VERBOSE)
        }
    }

    fun e(tagFromUser: String, msgFromUser: String) {
        if (DEBUG) {
            showLog(tagFromUser, msgFromUser, MODEL_DEBUG, Log.ERROR)
        } else {
            showLog(tagFromUser, msgFromUser, MODEL_RELEASE, Log.ERROR)
        }
    }

    fun i(tagFromUser: String, msgFromUser: String) {
        if (DEBUG) {
            showLog(tagFromUser, msgFromUser, MODEL_DEBUG, Log.INFO)
        } else {
            showLog(tagFromUser, msgFromUser, MODEL_RELEASE, Log.INFO)
        }
    }

    fun w(tagFromUser: String, msgFromUser: String) {
        if (DEBUG) {
            showLog(tagFromUser, msgFromUser, MODEL_DEBUG, Log.WARN)
        } else {
            showLog(tagFromUser, msgFromUser, MODEL_RELEASE, Log.WARN)
        }
    }


    // ####################################日志打印#########################################


    /**
     * 打印到文件 或 打印到控制台
     *
     * @param tagFromUser
     * @param msgFromUser
     * @param outDest
     * @param level
     */
    private fun showLog(tagFromUser: String?, msgFromUser: String?, outDest: Int, level: Int) {
        var tagFromUser = tagFromUser
        var msgFromUser = msgFromUser
        if (mAppLogDir == null) {
            Log.e("KtLog", "KtLog need init")
            return
        }
        if (tagFromUser == null) {
            tagFromUser = "TAG_NULL"
        }
        if (msgFromUser == null) {
            msgFromUser = "MSG_NULL"
        }
        // 日志级别
        if (level >= LOG_LEVEL) {
            // 输出到控制台
            if (outDest and SHOW_LOG_CONSOLE != 0) {
                logToConsole(tagFromUser, msgFromUser, level)
            }
            // 输出到文件
            if (outDest and SHOW_LOG_FILE != 0) {
                //
                val tagFromUser2File = tagFromUser
                val msgFromUser2File = msgFromUser
                //
                try {
                    mExecutorService?.submit {
                        this@KtLog.logToFile(
                            tagFromUser2File,
                            msgFromUser2File
                        )
                    }
                } catch (var9: Exception) {
                    Log.e("KtLog", "log -> $var9")
                }

            }
        }
    }

    /**
     * 将log打到控制台
     *
     * @param tagFromUser
     * @param msgFromUser
     * @param level
     */
    private fun logToConsole(tagFromUser: String, msgFromUser: String, level: Int) {
        when (level) {
            Log.DEBUG -> Log.d(APP_TAG, getConsoleLogMsg(tagFromUser, msgFromUser))
            Log.ERROR -> Log.e(APP_TAG, getConsoleLogMsg(tagFromUser, msgFromUser))
            Log.INFO -> Log.i(APP_TAG, getConsoleLogMsg(tagFromUser, msgFromUser))
            Log.VERBOSE -> Log.v(APP_TAG, getConsoleLogMsg(tagFromUser, msgFromUser))
            Log.WARN -> Log.w(APP_TAG, getConsoleLogMsg(tagFromUser, msgFromUser))
            else -> {
            }
        }
    }

    /**
     * 组合用户 tagFromUser 与 msgFromUser
     *
     * @param msgFromUser
     * @return
     */
    private fun getConsoleLogMsg(tagFromUser: String, msgFromUser: String): String {
        val sb = StringBuffer()
        sb.append(tagFromUser)
        sb.append(": ")
        sb.append(msgFromUser)
        return sb.toString()
    }

    /**
     * 将log打到文件日志
     *
     * @param tagFromUser
     * @param msgFromUser
     */
    private fun logToFile(tagFromUser: String, msgFromUser: String) {
        synchronized(lockObj) {
            // 输入流
            val tempLogFileStream = openTempFileOutStream()
            //
            if (tempLogFileStream != null) {
                try {
                    // 待输入的数据
                    val d = getFileLogMsg(tagFromUser, msgFromUser).toByteArray(charset("utf-8"))
                    // 写入
                    if (mTempLogFileSize < LOG_MAXSIZE) {
                        tempLogFileStream.write(d)
                        tempLogFileStream.write("\r\n".toByteArray())
                        tempLogFileStream.flush()
                        mTempLogFileSize += d.size.toLong()
                    } else {
                        // 关闭 temp 读写流
                        closeTempFileOutStream()
                        // temp ——> last 重命名
                        renameTemp2Last()
                        /**
                         * 重新创建 temp文件，并向 temp文件 写入数据
                         */
                        logToFile(tagFromUser, msgFromUser)
                    }// 大小超出
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Log.e(
                    "KtLog", "Log File open fail: [AppPath]=" + mAppLogDir
                            + ",[LogName]:" + mLogFilePreName
                )
            }
        }
    }

    /**
     * /data/data/%packetname%/files/mLogFilePrefix_name
     *
     * @param name
     * @return
     */
    private fun getNameFile(name: String): File? {
        // 路径空
        if (mAppLogDir == null || mAppLogDir!!.length == 0) {
            Log.e("KtLog", "KtLog should init")
            return null
        } else {
            return File(mAppLogDir + mLogFilePreName + "_" + name)
        }// 打开对应文件
    }


    /**
     * 组成Log字符串.添加时间信息.
     *
     * @param tagFromUser
     * @param msgFromUser
     * @return
     */
    private fun getFileLogMsg(tagFromUser: String, msgFromUser: String): String {
        //
        mCalendar.timeInMillis = System.currentTimeMillis()
        //
        val sb = StringBuffer()
        // 日期 + AppTag
        sb.append("[")
        sb.append(mCalendar.get(Calendar.YEAR))
        sb.append("-")
        sb.append(mCalendar.get(Calendar.MONTH) + 1)
        sb.append("-")
        sb.append(mCalendar.get(Calendar.DATE))
        sb.append(" ")
        sb.append(mCalendar.get(Calendar.HOUR_OF_DAY))
        sb.append(":")
        sb.append(mCalendar.get(Calendar.MINUTE))
        sb.append(":")
        sb.append(mCalendar.get(Calendar.SECOND))
        sb.append(":")
        sb.append(mCalendar.get(Calendar.MILLISECOND))
        sb.append(" ")
        sb.append(APP_TAG)
        sb.append(" ")
        sb.append("] ")
        // msg
        sb.append(tagFromUser)
        sb.append(": ")
        sb.append(msgFromUser)
        //
        return sb.toString()
    }


    /**
     * 获取日志临时文件输入流
     *
     * @return
     */
    private fun openTempFileOutStream(): OutputStream? {
        if (mTempLogFileStream == null) {
            try {
                // 没有初始化
                if (mAppLogDir == null || mAppLogDir!!.length == 0) {
                    Log.e("KtLog", "KtLog should init")
                    return null
                }
                // /data/data/%packetname%/files/mLogFilePreName+netease_log.temp
                val file = getNameFile(LOG_FILE_TEMP)
                // 文件为null
                if (file == null) {
                    Log.e("KtLog", "LOG_FILE_TEMP is null")
                    return null
                }
                // 文件存在
                if (file.exists()) {
                    mTempLogFileStream = FileOutputStream(file, true)
                    // 当前文件大小
                    mTempLogFileSize = file.length()
                } else {
                    // file.createNewFile();
                    mTempLogFileStream = FileOutputStream(file)
                    // 当前文件大小
                    mTempLogFileSize = 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("KtLog", "openTempFileOutStream exception: " + e.message)
            }

        }
        return mTempLogFileStream
    }

    /**
     * 关闭日志输出流
     */
    private fun closeTempFileOutStream() {
        try {
            if (mTempLogFileStream != null) {
                mTempLogFileStream!!.close()
                mTempLogFileStream = null
                mTempLogFileSize = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * netease_log.temp 重命名为 netease_log_last.txt
     */
    private fun renameTemp2Last() {
        synchronized(lockObj) {
            val tempFile = getNameFile(LOG_FILE_TEMP)
            val lastFile = getNameFile(LOG_FILE_LAST)
            // 删除上次的
            if (lastFile!!.exists()) {
                lastFile.delete()
            }
            // 重命名
            tempFile!!.renameTo(lastFile)
        }
    }

}
