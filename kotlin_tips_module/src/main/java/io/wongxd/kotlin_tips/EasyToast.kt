package io.wongxd.kotlin_tips

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast


/**
 * 一个简单易用的Toast封装类。用于提供易用的、多样式的Toast组件进行使用
 *
 * DATE: 2018/5/9
 *
 * AUTHOR: haoge
 */
class EasyToast private constructor(private val builder: Builder) {

    private var context: Context? = null
    private var toast: Toast? = null
    private var tv: TextView? = null

    fun init(context: Context?) {
        this.context = context?.applicationContext
    }

    fun show(resId: Int) {
        show(context?.getString(resId) ?: "")
    }

    fun show(message: String?, vararg any: Any) {
        context ?: throw IllegalStateException("You must call EasyToast#init(context) first.")

        if (TextUtils.isEmpty(message)) {
            return
        }

        var result = message as String
        if (any.isNotEmpty()) {
            result = String.format(message, any)
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            showInternal(result)
        } else {
            mainHandler.post { showInternal(result) }
        }
    }

    private fun showInternal(message: String) {
        createToastIfNeeded()

        if (builder.isDefault) {
            toast?.setText(message)
            toast?.show()
        } else {
            tv?.text = message
            toast?.show()
        }
    }

    @SuppressLint("ShowToast")
    private fun createToastIfNeeded() {
        if (toast == null) {
            if (builder.isDefault) {
                toast = Toast.makeText(context, "", builder.duration)
            } else {
                val container = LayoutInflater.from(context).inflate(builder.layoutId, null)
                tv = container.findViewById(builder.tvId)
                toast = Toast(context)
                toast?.view = container
                toast?.duration = builder.duration
            }

            if (builder.gravity != 0) {
                toast?.setGravity(builder.gravity, builder.offsetX, builder.offsetY)
            }
        }
    }

    companion object {

        internal val mainHandler by lazy { return@lazy Handler(Looper.getMainLooper()) }
        /**
         * 默认提供的Toast实例，在首次使用时进行加载。
         */
        val DEFAULT: EasyToast by lazy {
            return@lazy newBuilder()
                .build()
        }

        private fun newBuilder(): Builder {
            return Builder(null, true, 0, 0)
        }

        fun newBuilder(ctx: Context, layoutId: Int, tvId: Int): Builder {
            return Builder(ctx, false, layoutId, tvId)
        }
    }

    class Builder(
        private var ctx: Context?,
        internal var isDefault: Boolean,
        internal var layoutId: Int,
        internal var tvId: Int
    ) {

        internal var duration: Int = Toast.LENGTH_SHORT
        internal var gravity: Int = 0
        internal var offsetX: Int = 0
        internal var offsetY: Int = 0

        fun setGravity(gravity: Int, offsetX: Int, offsetY: Int): Builder {
            this.gravity = gravity
            this.offsetX = offsetX
            this.offsetY = offsetY
            return this
        }

        fun setDuration(duration: Int): Builder {
            this.duration = duration
            return this
        }


        fun build(): EasyToast {
            return EasyToast(this).apply { init(ctx) }
        }
    }
}