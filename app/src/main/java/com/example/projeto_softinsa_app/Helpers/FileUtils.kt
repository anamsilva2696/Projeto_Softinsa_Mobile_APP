package com.example.projeto_softinsa_app.Helpers

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    private const val TAG = "FileUtils"

    @Throws(IOException::class)
    fun copyAssetToInternalStorage(context: Context, assetFileName: String, destinationFileName: String) {
        val file = File(context.filesDir, destinationFileName)
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                Log.d(TAG, "Parent directory created: ${parentDir.absolutePath}")
            } else {
                Log.e(TAG, "Failed to create parent directory: ${parentDir.absolutePath}")
            }
        }

        context.assets.open(assetFileName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                copyStream(inputStream, outputStream)
                Log.d(TAG, "File copied to: ${file.absolutePath}")
            }
        }
    }

    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }

    @Throws(IOException::class)
    fun writeToFile(context: Context, fileName: String, data: String) {
        val file = File(context.filesDir, fileName)
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                Log.d(TAG, "Parent directory created: ${parentDir.absolutePath}")
            } else {
                Log.e(TAG, "Failed to create parent directory: ${parentDir.absolutePath}")
            }
        }

        FileOutputStream(file).use { fos -> // false to overwrite, true to append
            fos.write(data.toByteArray())
            Log.d(TAG, "Data written to: ${file.absolutePath}")
        }
    }

    @Throws(IOException::class)
    fun readFile(context: Context, fileName: String): String {
        val file = File(context.filesDir, fileName)
        return FileInputStream(file).use { fis ->
            fis.readBytes().toString(Charsets.UTF_8)
        }
    }
}