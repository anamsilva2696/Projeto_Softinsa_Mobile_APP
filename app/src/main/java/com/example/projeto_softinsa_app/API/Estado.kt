package com.example.projeto_softinsa_app.API

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projeto_softinsa_app.Des_tudo.imagem_estado
import com.example.projeto_softinsa_app.Des_tudo.imagem_tipoProjeto
import com.example.projeto_softinsa_app.Helpers.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class Estado(private val context: Context, private val editor: SharedPreferences.Editor?) {

    private lateinit var url: String
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    class Estado(
        val estadoId: Int,
        val estadoNome: String,
    ) {
        // Additional methods or properties can be added here
    }




    fun get_Estado(id: Int, callback: GetEstadoSingleCallback)
    {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "estado.json")
        val response: JSONObject = JSONObject(jsonString)
        if (response.has("estados")) {
            val jsonArray = response.getJSONArray("estados")
            for (i in 0 until jsonArray.length()) {
                val dataObject = jsonArray.getJSONObject(i)
                if (id == dataObject.optInt("estadoId")) {
                        // Extrair os campos desejados do objeto "data"
                        val estado = Estado(
                            estadoId = dataObject.optInt("estadoId"),
                            estadoNome = dataObject.optString("estadoNome"),
                        )

                        callback.onSuccess(estado)
                }
            }

                } else {
                    // Lidar com a resposta JSON inválida ou ausência do campo "data"
                    val errorMessage = "Resposta JSON inválida. Por favor, tente novamente mais tarde."
                    callback.onFailure(errorMessage)
                }
            }


    fun list_Estado(callback: GetEstadoCallback) {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "estado.json")
        val response: JSONObject = JSONObject(jsonString)
                try {
                    val jsonArray = response.getJSONArray("estados")
                    val ar_img_list_Estado = ArrayList<imagem_estado>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val estadoId = item.getInt("estadoId")
                        val estadoNome = item.getString("tipoProjetoNome")

                        val imgItem = imagem_estado()
                        imgItem.atr_estadoId_img_estado(estadoId)
                        imgItem.atr_estadoNome_img_estado(estadoNome)


                        ar_img_list_Estado.add(imgItem)
                    }

                    callback.onSuccess(ar_img_list_Estado)
                } catch (e: JSONException) {
                    val errorMessage = "Erro ao analisar a resposta do servidor: ${e.message}"
                    callback.onFailure(errorMessage)
                }
            }

    interface GetEstadoCallback {
        fun onSuccess(estado: List<imagem_estado>)
        fun onFailure(errorMessage: String)
    }

    interface GetEstadoSingleCallback {
        fun onSuccess(estado: Estado)
        fun onFailure(errorMessage: String)
    }

    interface GetUpdateCallback {
        fun onSuccess(sucesso: Boolean)
        fun onFailure(errorMessage: String)
    }

}