package com.example.projeto_softinsa_app.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projeto_softinsa_app.Helpers.FileUtils
import com.example.projeto_softinsa_app.Helpers.JsonHelper
import com.example.projeto_softinsa_app.MainActivity
import com.example.projeto_softinsa_app.MainCandidatura
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.Collections


class Authorization(private val context: Context, private val editor: SharedPreferences.Editor?) {
    private lateinit var url: String
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private var isServerPrimeiroLogin: Boolean = false

    interface LoginCallback {
        fun onSuccess(token: String)
        fun onFailure(errorMessage: String)
    }

    interface ChangePasswordCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

    fun login(email: String, password: String, isPrimeiroLogin: Boolean, callback: LoginCallback) {
        var jsonString = ""
        val file = File(context.filesDir, "users.json")
        if (file.exists()) {
            jsonString = FileUtils.readFile(context, "users.json")
        } else {
            jsonString = JsonHelper.ReadJSONFromAssets(context, "users.json")
        }
        val response: JSONObject = JSONObject(jsonString)
        try {
            val jsonArray = response.getJSONArray("users")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val token = item.getString("accessToken")
                val userId = item.getInt("userId")
                isServerPrimeiroLogin = item.getBoolean("isPrimeiroLogin")
                if (email == item.getString("email") && password == item.getString("password")) {
                    editor?.putString("token", token)
                    editor?.putInt("userId", userId)
                    saveUserId(context, userId)
                    editor?.putBoolean("isPrimeiroLogin", isPrimeiroLogin)
                    Log.d("tag", userId.toString())
                    editor?.apply()
                    callback.onSuccess(token)
                } else {
                    //val errorMessage = "Failed to login. Token not found."
                    //callback.onFailure(errorMessage)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
    fun saveUserId(context: Context, userId: Int) {
        // Get the SharedPreferences instance
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // Edit the SharedPreferences
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Put the userId in SharedPreferences
        editor.putInt("userId", userId)

        // Apply the changes
        editor.apply()
    }
    }

    fun getUserId(): Int {
        // Retrieve the userId from SharedPreferences
        return context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
            .getInt("userId", 0) // 0 is the default value if userId is not found
    }
    fun getIsServerPrimeiroLogin(): Boolean {
        return isServerPrimeiroLogin
    }
    fun registar(
        primeiroNome: String,
        ultimoNome: String,
        telemovel: String,
        email: String,
        password: String,
        isPrimeiroLogin: Boolean,
        callback: LoginCallback
    ) {
        var jsonString = ""
        val file = File(context.filesDir, "users.json")
        if (file.exists()) {
            jsonString = FileUtils.readFile(context, "users.json")
        } else {
            jsonString = JsonHelper.ReadJSONFromAssets(context, "users.json")
        }
        val response: JSONObject = JSONObject(jsonString)
        try {
            val body = JSONObject()
            val cargoObjeto = JSONObject()
            val userIds = ArrayList<Int>()
            val array = response.getJSONArray("users")
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val userId: Int = item.getInt("userId")
                userIds.add(userId)
            }

            val sortedNumbers = userIds.sortedDescending()

            var insertUserId = sortedNumbers.first() + 1

            body.put("userId", insertUserId)
                body.put("googleId", "")
                body.put("primeiroNome", primeiroNome)
                body.put("ultimoNome", ultimoNome)
                body.put("numeroFuncionario", 1)
                body.put("email", email)
                body.put("password", password)
                body.put("telemovel", telemovel)
                body.put("morada", "Rua 1")
                body.put("salario", 2000)
                body.put("ultimoLogin", JsonHelper.getCurrentDateFormatted())
                body.put("dataContratacao", JsonHelper.getCurrentDateFormatted())
                body.put("dataRegisto", JsonHelper.getCurrentDateFormatted())
                body.put("isPrimeiroLogin", false)
                body.put("isAtivo", true)
                body.put("isColaborador", true)
                body.put("isCandidato", false)
                body.put("verificationToken", "")
                body.put("accessToken", "")
                body.put("recoverToken", "")
                cargoObjeto.put("cargoId", 1)
                cargoObjeto.put("cargoNome", "Colaborador")
                body.put("cargo", cargoObjeto)
                body.put("departamentoId", 1)
                body.put("filialId", 1)

                JsonHelper.addToJsonFile(context, "users.json", body, "users")

                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


    // Função getUserDetail() removida, pois não parece estar relacionada às colunas desejadas.

    fun ChangePassword(userId: Int, email: String, newPassword: String, isPrimeiroLogin: Boolean, callback: ChangePasswordCallback) {
        val url = "https://softinsa-web-app-carreiras01.onrender.com/auth/primeiroLogin/$userId"

        val body = JSONObject()
        try {
            body.put("email", email)
            body.put("password", newPassword)
            body.put("isPrimeiroLogin", isPrimeiroLogin)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = JsonObjectRequest(
            Request.Method.POST, // Altere para POST
            url,
            body,
            Response.Listener { response ->
                Log.d("ChangePassword", "Response JSON: $response")
                // Atualize o valor de isPrimeiroLogin nas SharedPreferences
                val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isPrimeiroLogin", isPrimeiroLogin)
                editor.apply()
                Log.d("Debug", "isPrimeiroLogin atualizado para: " + isPrimeiroLogin);
                callback.onSuccess()
                Log.d("Debug", "isPrimeiroLogin atualizado para1: " + isPrimeiroLogin);
                editor.apply()
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                val errorMessage = "Erro de conexão"
                callback.onFailure(errorMessage)
            }
        )

        requestQueue.add(request)
    }
}