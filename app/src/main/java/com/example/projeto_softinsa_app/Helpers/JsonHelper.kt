package com.example.projeto_softinsa_app.Helpers

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class JsonHelper {
    companion object {
        fun ReadJSONFromAssets(context: Context, path: String): String {
            val identifier = "[ReadJSON]"
            try {
                val file = context.assets.open("$path")
                Log.i(
                    identifier,
                    "Found File: $file.",
                )
                val bufferedReader = BufferedReader(InputStreamReader(file))
                val stringBuilder = StringBuilder()
                bufferedReader.useLines { lines ->
                    lines.forEach {
                        stringBuilder.append(it)
                    }
                }
                Log.i(
                    identifier,
                    "getJSON stringBuilder: $stringBuilder.",
                )
                val jsonString = stringBuilder.toString()
                Log.i(
                    identifier,
                    "JSON as String: $jsonString.",
                )
                return jsonString
            } catch (e: Exception) {
                Log.e(
                    identifier,
                    "Error reading JSON: $e.",
                )
                e.printStackTrace()
                return ""
            }
        }

    fun addToJsonFile(context: Context, path: String, jsonObj: JSONObject, tag: String) {
        // parse existing/init new JSON

        var previousJson: String? = ReadJSONFromAssets(context, path)

        // create new "complex" object
        val mO: JSONObject = JSONObject(previousJson)
        var jA = JSONArray()

        try {
            jA = mO.getJSONArray(tag)

            jA.put(jsonObj)


        } catch (e: JSONException) {
            Log.d("error add json file", e.message.toString())
            e.printStackTrace()
        }

        // generate string from the object
        var jsonString: String? = null
        try {
            jsonString = mO!!.toString(4)
        } catch (e: JSONException) {
            Log.d("error add json file", e.message.toString())

            e.printStackTrace()
        }

        // write back JSON file
        writeToFile(context, path, jsonString.toString())
    }


    fun writeToFile(context: Context, fileName: String, fileContent: String) {
            try {
                // Copy the file from assets to internal storage
                FileUtils.copyAssetToInternalStorage(context, fileName, fileName)

                // Write to the copied file
                FileUtils.writeToFile(context, fileName, fileContent)

                // Read the file content
                val fileContentrrr = FileUtils.readFile(context, fileName)
                Log.d("file content", "File content: $fileContentrrr")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getCurrentDateFormatted(): String {
            // Get the current date
            val currentDate = LocalDate.now()

            // Define the desired date format
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Format the current date
            return currentDate.format(formatter)
        }
    }
}