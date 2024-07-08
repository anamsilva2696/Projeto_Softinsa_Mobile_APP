package com.example.projeto_softinsa_app.API
import android.content.Context
import android.content.SharedPreferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.projeto_softinsa_app.Helpers.JsonHelper
import org.json.JSONObject

class Filial(private val context: Context, private val editor: SharedPreferences.Editor?) {

    private lateinit var url: String

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    data class Filial(
        val filialId: Int,
        val filialNome: String,
        val morada: String,
        val telemovel: String,
        val email: String,
    )

    fun getFilial(callback: GetFilialCallback, id: Int) {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "filial.json")
        val response: JSONObject = JSONObject(jsonString)
        if (response.has("filiais")) {
                    val dataObject = response.getJSONObject("filiais")
                    if (id == dataObject.optInt("filialId")) {
                        val filial = Filial(
                            filialId = dataObject.optInt("filialId"),
                            filialNome = dataObject.optString("filialNome"),
                            morada = dataObject.optString("morada"),
                            telemovel = dataObject.optString("telemovel"),
                            email = dataObject.optString("email")

                        )
                        callback.onSuccess(filial)
                    }
                } else {
                    val errorMessage = "Resposta JSON Inválida"
                    callback.onFailure(errorMessage)
                }
            }

    interface GetFilialCallback {
        fun onSuccess(user: Filial)
        fun onFailure(errorMessage: String)
    }
}