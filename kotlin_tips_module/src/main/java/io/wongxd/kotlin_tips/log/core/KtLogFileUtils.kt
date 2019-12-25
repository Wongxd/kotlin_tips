package io.wongxd.kotlin_tips.log.core

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 压缩文件
 */
object KtLogFileUtils {

    private val TAG = "kotlin_tips_log_fileUtils"


    /**
     * 多个文件的压缩
     *
     * @param filePaths 文件列表
     * @param destZip   压缩后文件 例:abc.zip
     * @return
     */
    fun exportZipFromPaths(filePaths: List<String>?, destZip: File?): Boolean {
        // 空判断
        if (filePaths == null || filePaths.isEmpty() || destZip == null) {
            return false
        }
        // 获取 lastFiles 路径下的全部文件
        val lastFiles = ArrayList<File>()
        val size = filePaths.size
        for (i in 0 until size) {
            exportFiles(lastFiles, filePaths[i])
        }
        // 压缩文件
        return exportZipFromFiles(lastFiles, destZip)
    }

    /**
     * 读取 path 路径下的全部文件
     *
     * @param exportFiles 输出文件列表
     * @param path        对应的输入路径
     */
    private fun exportFiles(exportFiles: MutableList<File>?, path: String) {
        if (TextUtils.isEmpty(path) || exportFiles == null) {
            return
        }
        exportFiles(exportFiles, File(path))
    }

    /**
     * 读取 file 路径下的全部文件
     *
     * @param exportFiles 输出文件列表
     * @param file        对应的输入路径
     */
    private fun exportFiles(exportFiles: MutableList<File>?, file: File?) {
        // 空判断
        if (file == null || !file.exists() || exportFiles == null) {
            return
        }
        // 路径文件
        if (!file.isDirectory) {
            exportFiles.add(file)
        } else {
            val files = file.listFiles()
            if (files == null || files.size == 0) {
                return
            }
            val length = files.size
            for (i in 0 until length) {
                exportFiles(exportFiles, files[i])
            }
        }// 文件列表
    }

    /**
     * 多个文件的压缩
     *
     * @param list    文件列表
     * @param destZip 压缩后文件 例:abc.zip
     * @return
     */
    private fun exportZipFromFiles(list: List<File>?, destZip: File?): Boolean {
        // 空判断
        if (list == null || list.isEmpty() || destZip == null) {
            return false
        }
        var input: InputStream? = null
        var zipOut: ZipOutputStream? = null
        //
        try {
            // 压缩文件存在，则删除
            if (destZip.exists()) {
                destZip.delete()
            }
            // 创建压缩文件
            try {
                destZip.createNewFile()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

            // 创建文件目录
            if (!destZip.parentFile.exists()) {
                destZip.parentFile.mkdir()
            }
            // 压缩文件流
            zipOut = ZipOutputStream(FileOutputStream(destZip))
            //
            var file: File? = null
            val buf = ByteArray(4096)
            //
            val length = list.size
            for (i in 0 until length) {
                file = list[i]
                if (!file.isDirectory) {
                    input = FileInputStream(file)
                    zipOut.putNextEntry(ZipEntry(file.parentFile.absolutePath + File.separator + file.name))
                    var readCount = input.read(buf)
                    while (readCount > 0) {
                        zipOut.write(buf, 0, readCount)
                        readCount = input.read(buf)
                    }
                    zipOut.closeEntry()
                    input.close()
                }
            }
            zipOut.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                input?.close()
                zipOut?.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
    }

    // ########################文件 目录#########################

    /**
     * 获取应用私有cache目录
     *
     *
     * /sdcard/Android/data/包名/cache
     */
    fun getAppCachePath(context: Context): String {
        val file = context.externalCacheDir
        //先判断外部存储是否可用
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && file != null) {
            file.absolutePath
        } else {
            context.cacheDir.absolutePath
        }
    }
}
