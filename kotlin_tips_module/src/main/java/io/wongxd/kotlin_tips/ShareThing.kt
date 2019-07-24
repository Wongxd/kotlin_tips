package io.wongxd.kotlin_tips

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import java.io.*
import java.net.URL


/**
 * Created by wongxd on 2018/11/28.
 */
class ShareThing {


    fun getLocalBitmap(ctx: Context, id: Int): Bitmap {
        return BitmapFactory.decodeResource(ctx.resources, id)
    }


    /**
     * 需要跑在子线程
     *
     *
     * 把网络资源图片转化成bitmap
     *
     * @param url 网络资源图片
     * @return Bitmap
     */
    fun getNetBitmap(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        var `in`: InputStream? = null
        var out: BufferedOutputStream? = null
        try {
            `in` = BufferedInputStream(URL(url).openStream(), 1024)
            val dataStream = ByteArrayOutputStream()
            out = BufferedOutputStream(dataStream, 1024)
            copy(`in`, out)
            out.flush()
            var data: ByteArray? = dataStream.toByteArray()
            bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
            data = null
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    @Throws(IOException::class)
    private fun copy(`in`: InputStream, out: OutputStream) {
        val b = ByteArray(1024)
        var read: Int = -1
        while ((`in`.read(b)).also { read = it } != -1) {
            out.write(b, 0, read)
        }
    }

    private fun saveBitmap(ctx: Context, bm: Bitmap, picName: String): Uri? {
        try {
            val dir: String =
                Environment.getExternalStorageDirectory().absolutePath + "/${ctx.applicationContext.packageName}/" + picName + ".jpg"
            val f = File(dir)
            if (!f.exists()) {
                f.parentFile.mkdirs()
                f.createNewFile()
            }

            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()


            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //版本是否在7.0以上
                FileProvider.getUriForFile(
                    ctx,
                    ctx.applicationContext.packageName + ".fileProvider",
                    f
                )
            } else {
                Uri.fromFile(f)
            }
            return uri
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    fun shareImg(ctx: Context, bmp: Bitmap, title: String = "图片分享") {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND//设置分享行为
        shareIntent.type = "image/*"  //设置分享内容的类型
        shareIntent.putExtra(Intent.EXTRA_STREAM, saveBitmap(ctx, bmp, "img"))
        //创建分享的Dialog
        val realIntent = Intent.createChooser(shareIntent, title)

        ctx.startActivity(realIntent)
    }


    fun shareText(ctx: Context, content: String, title: String = "文字分享") {

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND//设置分享行为
        shareIntent.type = "text/plain"  //设置分享内容的类型
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)//添加分享内容标题
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)//添加分享内容

        //创建分享的Dialog
        val realIntent = Intent.createChooser(shareIntent, title)

        ctx.startActivity(realIntent)
    }
}