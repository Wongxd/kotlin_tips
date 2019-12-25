package io.wongxd.kotlin_tips.log

import android.content.Context
import io.wongxd.kotlin_tips.log.core.KtLog


/**
 * create by xiaxl on 2019.12.05
 *
 *
 * 网络日志
 */
object KtNetLog {

    private val mLog = KtLog()

    private val LOG_FILE_PRE_NAME = "net"

    init {
        mLog.setLogFilePreName(LOG_FILE_PRE_NAME)
    }

    fun init(context: Context, isDebug: Boolean) {
        // 初始化
        mLog.init(context)
        // debug模式
        mLog.setDebug(isDebug)
    }

    fun v(tag: String, msg: String) {
        mLog.v(tag, msg)
    }

    fun d(tag: String, msg: String) {
        mLog.d(tag, msg)
    }

    fun i(tag: String, msg: String) {
        mLog.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        mLog.w(tag, msg)
    }

    fun e(tag: String, msg: String) {
        mLog.e(tag, msg)
    }
}
