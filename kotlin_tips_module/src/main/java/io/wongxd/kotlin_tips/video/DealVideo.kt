package io.wongxd.kotlin_tips.video

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.wongxd.kotlin_tips.simple_for_result.SimpleOnActivityResult

/**
 * Created by wongxd on 2019/4/26.
 */
class DealVideo private constructor(val ctx: Context?, val fgt: Fragment?, val aty: AppCompatActivity?) {

    companion object {

        fun getOne(activity: AppCompatActivity?): DealVideo {
            return DealVideo(activity?.applicationContext, null, activity)
        }

        fun getOne(fragment: Fragment?): DealVideo {
            return DealVideo(fragment?.activity?.applicationContext, fragment, null)
        }
    }

    fun pickVideo(callback: (String, Bitmap) -> Unit) {
        //Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, Thumbnails.MICRO_KIND);
        val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)

        val sim =
            fgt?.let { SimpleOnActivityResult.simpleForResult(fgt) } ?: SimpleOnActivityResult.simpleForResult(aty)

        sim.startForResult(i) { requestCode: Int, resultCode: Int, data: Intent? ->
            if (resultCode == RESULT_OK && null != data) {
                val selectedVideo: Uri? = data.data
                val filePathColumn: Array<String> = arrayOf(MediaStore.Video.Media.DATA)

                val cursor = ctx?.contentResolver?.query(
                    selectedVideo,
                    filePathColumn, null, null, null
                )
                cursor?.moveToFirst()

                val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                columnIndex?.let {
                    val videoPath = cursor.getString(columnIndex)
                    val bitmap = ThumbnailUtils.createVideoThumbnail(
                        videoPath,
                        MediaStore.Video.Thumbnails.MICRO_KIND
                    )
                    callback.invoke(videoPath, bitmap)

                }
                cursor?.close()

            }

        }
    }
}