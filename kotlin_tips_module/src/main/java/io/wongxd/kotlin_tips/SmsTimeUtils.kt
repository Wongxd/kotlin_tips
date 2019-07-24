package io.wongxd.kotlin_tips

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.widget.TextView
import java.lang.ref.WeakReference
import java.util.*

/**
 * 发送验证码倒计时工具类
 *
 */
class SmsTimeUtils {


    private var CURR_COUNT = 0
    private var RE_GET_CODE_STR = ""
    private var WAITING_STR = ""
    private var onWaiting: (TextView, Int) -> Unit = { textView, i -> }
    private var onFinished: (TextView) -> Unit = { textView -> }

    private var countdownTimer: Timer? = null
    private var tvSendCode: WeakReference<TextView>? = null


    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                if (countdownTimer != null) {
                    countdownTimer?.cancel()
                    countdownTimer = null
                }
                tvSendCode?.get()?.apply {
                    text = RE_GET_CODE_STR
                    isEnabled = true
                    onFinished(this)
                }

            } else {
                tvSendCode?.get()?.apply {
                    text = msg.what.toString() + WAITING_STR
                    isEnabled = false
                    onWaiting(this, msg.what)
                }
            }
            super.handleMessage(msg)
        }
    }


    /**
     *
     * @param textView 控制倒计时的view
     *
     * @param long  时长 秒
     *
     * @param reGetCodeStr 重新获取验证码时，提示的文字
     *
     * @param waitingStr 等待验证码时，提示的文字
     *
     * @param onWaiting 等待中，int 值为还需等待多少秒，可对 textView 做 ui 改变
     *
     * @param onFinished 结束等待，可对 textView 做 ui 改变
     */
    fun startCountdown(
        textView: WeakReference<TextView>,
        long: Int = 60,
        reGetCodeStr: String = "获取验证码",
        waitingStr: String = "秒后重试",
        onWaiting: (TextView, Int) -> Unit,
        onFinished: (TextView) -> Unit
    ) {
        tvSendCode = textView
        CURR_COUNT = long
        RE_GET_CODE_STR = reGetCodeStr
        WAITING_STR = waitingStr
        this.onWaiting = onWaiting
        this.onFinished = onFinished

        if (countdownTimer == null) {
            countdownTimer = Timer()
            countdownTimer?.schedule(object : TimerTask() {
                override fun run() {
                    val msg = handler.obtainMessage()
                    msg.what = CURR_COUNT--
                    handler.sendMessage(msg)
                }
            }, 0, 1000)
        }
    }
}
