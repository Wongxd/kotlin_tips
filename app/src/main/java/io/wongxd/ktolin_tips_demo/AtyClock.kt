package io.wongxd.ktolin_tips_demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.aty_clock.*
import java.util.*

/**
 * Created by wongxd on 2019/7/24.
 */
class AtyClock : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_clock)

        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tv_clock.doInvalidate()
                }
            }
        }

        timer.schedule(timerTask, 0, 1000)

    }
}