package io.wongxd.kotlin_tips.image

import android.os.Handler
import android.os.Looper

/**
 * Created by wongxd on 2019/12/30.
 */
class MainLooper private constructor(looper: Looper) : Handler(looper) {
    companion object {
        val instance = MainLooper(Looper.getMainLooper())

        fun runOnUiThread(runnable: () -> Unit) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                runnable()
            } else {
                instance.post(runnable)
            }

        }
    }
}