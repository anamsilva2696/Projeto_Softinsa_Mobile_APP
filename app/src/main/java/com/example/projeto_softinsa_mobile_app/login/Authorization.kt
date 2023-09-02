package com.example.projeto_softinsa_mobile_app.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class Authorization(private val context: Context, private val editor: SharedPreferences.Editor?) {
    private lateinit var url: String
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    interface LoginCallback {
        fun onSuccess(token: String)
        fun onFailure(errorMessage: String)
    }

    interface ChangePasswordCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

    fun login(email: String, password: String, isPrimeiroLogin: Boolean, callback: LoginCallback) {
        url = "https://softinsa-web-app-carreiras01.onrender.com/auth/login" // Substitua pela sua URL de API para login

        val body = JSONObject()
        try {
            body.put("email", email)
            body.put("password", password)
            body.put("isPrimeiroLogin", isPrimeiroLogin)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            Response.Listener { response ->
                val token = response.optString("accessToken")
                val userId = response.optInt("userId")

                // Imprimir a resposta JSON para solução de problemas
                Log.d("Authorization", "Response JSON: $response")

                if (token.isNotEmpty()) {
                    editor?.putString("token", token)
                    editor?.putInt("userId", userId)
                    Log.d("tag", userId.toString())
                    editor?.apply()

                    callback.onSuccess(token)
                } else {
                    val errorMessage = "Failed to login. Token not found."
                    callback.onFailure(errorMessage)
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                val errorMessage = "Failed to login. Please check your credentials."
                callback.onFailure(errorMessage)
            })

        requestQueue.add(request)
    }

    fun getUserId(): Int {
        return context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
            .getInt("userId", 0)
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
        url = "https://softinsa-web-app-carreiras01.onrender.com/auth/signup" // Substitua pela URL da sua API para registro

        val body = JSONObject()
        try {
            body.put("primeiroNome", primeiroNome)
            body.put("ultimoNome", ultimoNome)
            body.put("telemovel", telemovel)
            body.put("email", email)
            body.put("password", password)
            body.put("isPrimeiroLogin", isPrimeiroLogin)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            Response.Listener { response ->
                // Print the response JSON for troubleshooting
                Log.d("Authorization", "Response JSON: $response")

                // Verifique se o registro foi bem-sucedido com base na resposta do servidor
                val success = response.optBoolean("success")
                if (success) {
                    callback.onSuccess("Registration successful")
                } else {
                    val errorMessage = response.optString("message", "Failed to sign up.")
                    callback.onFailure(errorMessage)
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                // Lida com o erro de registro e chama o onFailure callback com a mensagem de erro
                val errorMessage = "Failed to sign up."
                callback.onFailure(errorMessage)
            })

        requestQueue.add(request)
    }

    // Função getUserDetail() removida, pois não parece estar relacionada às colunas desejadas.

    fun ChangePassword(id: Int, email: String, newPassword: String, callback: ChangePasswordCallback) {
        val url = "https://softinsa-web-app-carreiras01.onrender.com/user/update/$id"

        val body = JSONObject()
        try {
            body.put("email", email)
            body.put("password", newPassword)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            Response.Listener { response ->
                Log.d("ChangePassword", "Response JSON: $response")
                callback.onSuccess()
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