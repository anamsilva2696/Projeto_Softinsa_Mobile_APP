package com.example.projeto_softinsa_app.API
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import com.example.projeto_softinsa_app.Des_tudo.imagem_vaga
import com.example.projeto_softinsa_app.Helpers.JsonHelper
import org.json.JSONObject
import kotlin.collections.ArrayList

class Vaga(private val context: Context, private val editor: SharedPreferences.Editor?) {

    private lateinit var url: String
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    data class Vaga(
        val vagaId: Int,
        val titulo: String,
        val descricao: String,
        val habilitacoesMin: String,
        val experienciaMin: String,
        val remuneracao: Int,
        val dataRegisto: String,
        val dataAtualizacao: String,
        val isInterna: Boolean,
        val userId: Int,
        val filialId: Int,
        val departamentoId: Int
    )

    fun listVagas(callback: GetVagaCallback) {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "vagas.json")
        val response = JSONObject(jsonString)
                try {
                    val jsonArray = response.getJSONArray("vagas")
                    val ar_img_list_Vaga = ArrayList<imagem_vaga>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val vagaId = item.getInt("vagaId")
                        val titulo = item.getString("titulo")
                        val descricao = item.getString("descricao")
                        val habilitacoesMin = item.getString("habilitacoesMin")
                        val experienciaMin = item.getString("experienciaMin")
                        val remuneracao = item.getInt("remuneracao")
                        val dataRegisto = item.getString("dataRegisto")
                        val dataAtualizacao = item.getString("dataAtualizacao")
                        val isInterna = item.getBoolean("isInterna")
                        val userId = 1
                        val filialId = item.getInt("filialId")
                        val departamentoId = item.getInt("departamentoId")

                        val imgItem = imagem_vaga()
                        imgItem.atr_vagaId_vaga(vagaId)
                        imgItem.atr_titulo_vaga(titulo)
                        imgItem.atr_descricao_vaga(descricao)
                        imgItem.atr_habilitacoes_vaga(habilitacoesMin)
                        imgItem.atr_experiencia_vaga(experienciaMin)
                        imgItem.atr_renumeracao_vaga(remuneracao)
                        imgItem.atr_dataRegisto_vaga(dataRegisto)
                        imgItem.atr_dataAtualizacao_vaga(dataAtualizacao)
                        imgItem.atr_isInterna_vaga(isInterna)
                        imgItem.atr_userId_vaga(userId)
                        imgItem.atr_filialId_vaga(filialId)
                        imgItem.atr_departamentoId_vaga(departamentoId)

                        ar_img_list_Vaga.add(imgItem)
                    }
                    Log.d("listvagasstring",
                        "aqui")

                    callback.onSuccess(ar_img_list_Vaga)
                } catch (e: JSONException) {
                    val errorMessage = "Erro ao analisar a resposta do servidor: ${e.message}"
                    callback.onFailure(errorMessage)
                }
    }

    fun getVaga(id: Int, callback: GetVagaSingleCallback)
    {
        val jsonString = JsonHelper.ReadJSONFromAssets(context, "vagas.json")
        val response = JSONObject(jsonString)

        if (response.has("vagas")) {
                    val dataObject = response.getJSONObject("data")
                    // Extrair os campos desejados do objeto "data"
            if (id  == dataObject.optInt("vagaId")) {
                val vaga = Vaga(
                    vagaId = dataObject.optInt("vagaId"),
                    titulo = dataObject.optString("titulo"),
                    descricao = dataObject.optString("descricao"),
                    habilitacoesMin = dataObject.optString("habilitacoesMin"),
                    experienciaMin = dataObject.optString("experienciaMin"),
                    remuneracao = dataObject.optInt("remuneracao"),
                    dataRegisto = dataObject.optString("dataRegisto"),
                    dataAtualizacao = dataObject.optString("dataAtualizacao"),
                    isInterna = dataObject.optBoolean("isInterna"),
                    userId = dataObject.optInt("userId"),
                    filialId = dataObject.optInt("filialId"),
                    departamentoId = dataObject.optInt("departamentoId"),
                )
                callback.onSuccess(vaga)
            }
                } else {
                    // Lidar com a resposta JSON inválida ou ausência do campo "data"
                    val errorMessage = "Resposta JSON inválida. Por favor, tente novamente mais tarde."
                    callback.onFailure(errorMessage)
                }

    }

    interface GetVagaCallback {
        fun onSuccess(vagas: List<imagem_vaga>)
        fun onFailure(errorMessage: String)
    }

    interface GetVagaSingleCallback {
        fun onSuccess(vaga: Vaga)
        fun onFailure(errorMessage: String)
    }
}