package com.example.projeto_softinsa_app.API

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projeto_softinsa_app.Des_tudo.imagem_tipoProjeto
import com.example.projeto_softinsa_app.Helpers.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class Tipo_Projeto(private val context: Context, private val editor: SharedPreferences.Editor?) {

    private lateinit var url: String
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    class Tipo_Projeto(
        val tipoProjetoId: Int,
        val tipoProjetoNome: String,
    ) {
        // Additional methods or properties can be added here
    }




    fun getTipo_Projeto(id: Int, callback: GetTipo_ProjetoSingleCallback)
    {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "tipos-projeto.json")
        val response: JSONObject = JSONObject(jsonString)
        if (response.has("tipos_projeto")) {
            val jsonArray = response.getJSONArray("tipos_projeto")
            for (i in 0 until jsonArray.length()) {
                val dataObject = jsonArray.getJSONObject(i)
                if (id == dataObject.optInt("tipoProjetoId")) {
                    // Extrair os campos desejados do objeto "data"
                    val area_negocio = Tipo_Projeto(
                        tipoProjetoId = dataObject.optInt("tipoProjetoId"),
                        tipoProjetoNome = dataObject.optString("tipoProjetoNome"),
                    )

                    callback.onSuccess(area_negocio)
                    }
                }
                } else {
                    // Lidar com a resposta JSON inválida ou ausência do campo "data"
                    val errorMessage = "Resposta JSON inválida. Por favor, tente novamente mais tarde."
                    callback.onFailure(errorMessage)
                }
            }


    fun listTipo_Projetos(callback: GetTipo_ProjetoCallback) {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "tipos-projeto.json")
        val response: JSONObject = JSONObject(jsonString)
                try {
                    val jsonArray = response.getJSONArray("tipos_projeto")
                    val ar_img_list_tipoProjeto = ArrayList<imagem_tipoProjeto>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val tipoProjetoId = item.getInt("tipoProjetoId")
                        val tipoProjetoNome = item.getString("tipoProjetoNome")

                        val imgItem = imagem_tipoProjeto()
                        imgItem.atr_tipoProjetoId_tipoProjeto(tipoProjetoId)
                        imgItem.atr_tipoProjetoNome_tipoProjeto(tipoProjetoNome)


                        ar_img_list_tipoProjeto.add(imgItem)
                    }

                    callback.onSuccess(ar_img_list_tipoProjeto)
                } catch (e: JSONException) {
                    val errorMessage = "Erro ao analisar a resposta do servidor: ${e.message}"
                    callback.onFailure(errorMessage)
                }
            }

    interface GetTipo_ProjetoCallback {
        fun onSuccess(tipo_projeto: List<imagem_tipoProjeto>)
        fun onFailure(errorMessage: String)
    }

    interface GetTipo_ProjetoSingleCallback {
        fun onSuccess(tipo_projeto: Tipo_Projeto)
        fun onFailure(errorMessage: String)
    }

    interface GetUpdateCallback {
        fun onSuccess(sucesso: Boolean)
        fun onFailure(errorMessage: String)
    }

}