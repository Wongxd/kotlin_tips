package io.wongxd.kotlin_tips

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by wongxd on 2018/06/15.
 *            https://github.com/wongxd
 *            wxd1@live.com
 *
 */

fun canUseSdcard(): Boolean {
    var mExternalStorageWriteable = false
    //获取外存储的状态
    val state = Environment.getExternalStorageState()
    when (state) {
        Environment.MEDIA_MOUNTED -> { // 可读可写
            mExternalStorageWriteable = true
        }
        Environment.MEDIA_MOUNTED_READ_ONLY -> {
            // 可读
        }
        else -> {
            // 可能有很多其他的状态，但是我们只需要知道，不能读也不能写
        }
    }

    return mExternalStorageWriteable
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun getTextSize(paint: Paint, text: String): Array<Int> {
    val rect = Rect()
    paint.getTextBounds(text, 0, text.length, rect)
    val width = rect.width()//文字宽
    val height = rect.height()//文字高

    return arrayOf(width, height)
}


fun <A, B> bothNotNull(a: A?, b: B?, then: (a: A, b: B) -> Unit) {
    a?.let { aa ->
        b?.let { bb ->
            then.invoke(aa, bb)
        }
    }
}


inline fun <reified T : Activity> startAty(
    ctx: Context?,
    vararg pair: Pair<String, Any>,
    isStartNewTask: Boolean = false,
    isCleanTask: Boolean = false
) {

    ctx ?: return

    val i = Intent(ctx, T::class.java)

    if (isStartNewTask)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (isCleanTask) {
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    pair.forEach {
        val key = it.first
        val value = it.second
        when (value) {
            is String -> {
                i.putExtra(key, value)
            }

            is Int -> {
                i.putExtra(key, value)
            }


            is Float -> {
                i.putExtra(key, value)
            }


            is Double -> {
                i.putExtra(key, value)
            }


            is Serializable -> {
                i.putExtra(key, value)
            }

            is Boolean -> {
                i.putExtra(key, value)
            }

        }
    }

    ctx.startActivity(i)
}

inline fun <reified A : Activity> Activity.startAty(vararg params: Pair<String, String>) {
    io.wongxd.kotlin_tips.startAty<A>(this, *params)
}

inline fun <reified A : Activity> Fragment.startAty(vararg params: Pair<String, String>) {
    this.activity?.startAty<A>(*params)
}


fun openInSysBrowser(ctx: Context, url: String) {
    val intent = Intent()
    intent.action = "android.intent.action.VIEW"
    val content_url = Uri.parse(url)
    intent.data = content_url
    ctx.startActivity(intent)
}

fun checkPackage(context: Context, packageName: String?): Boolean {
    if (packageName == null || "" == packageName) return false
    return try {
        context.packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

}

fun logI(tag: String = "wongxd", info: String) {  //信息太长,分段打印

    var msg = info

    //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，

    //  把4*1024的MAX字节打印长度改为2001字符数

    val max_str_length = 2001 - tag.length

    //大于4000时

    while (msg.length > max_str_length) {

        Log.i(tag, msg.substring(0, max_str_length))

        msg = msg.substring(max_str_length)

    }

    //剩余部分

    Log.i(tag, msg)

}


/**
 * 时间戳转 "yyyy-MM-dd HH:mm"
 * @param customFmt "yyyy-MM-dd HH:mm" 自定义显示格式
 */
fun Long.getTime(isShowHour: Boolean = true, splitStr: String = "-", customFmt: String = ""): String {
    val res: String
    val fmt = if (customFmt.isBlank()) {
        if (isShowHour) {
            "yyyy${splitStr}MM${splitStr}dd HH:mm"
        } else {
            "yyyy${splitStr}MM${splitStr}dd"
        }
    } else {
        customFmt
    }

    val simpleDateFormat = SimpleDateFormat(fmt)
    val date = Date(if (this.toString().length == 10) this * 1000 else this)
    res = simpleDateFormat.format(date)
    return res
}


fun Long.getDate(): Date = Date(if (this.toString().length == 10) this * 1000 else this)

fun Long.sec2hour(s: Long = this): String {

    fun unitFormat(i: Long): String {
        return if (i in 0..9)
            "0$i"
        else
            "" + i
    }

    var timeStr: String = ""
    var hour = 0L
    var minute = 0L
    var second = 0L
    if (s <= 0)
        return "00:00"
    else {
        minute = s / 60
        if (minute < 60) {
            second = s % 60
            timeStr = unitFormat(minute) + ":" + unitFormat(second)
        } else {
            hour = minute / 60
            if (hour > 99)
                return "99:59:59"
            minute %= 60
            second = s - hour * 3600 - minute * 60
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
        }
    }
    return timeStr

}