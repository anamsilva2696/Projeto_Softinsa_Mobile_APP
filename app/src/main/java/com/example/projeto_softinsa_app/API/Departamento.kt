    package com.example.projeto_softinsa_app.API
    import android.content.Context
    import android.content.SharedPreferences
    import com.android.volley.Request
    import com.android.volley.RequestQueue
    import com.android.volley.Response
    import com.android.volley.toolbox.JsonObjectRequest
    import com.android.volley.toolbox.Volley
    import com.example.projeto_softinsa_app.Helpers.JsonHelper
    import org.json.JSONObject

    class Departamento(private val context: Context, private val editor: SharedPreferences.Editor?) {

        private lateinit var url: String

        private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        data class Departamento(
            val deparmentoId: Int,
            val departamentoNome: String,
            val dataCriacao: String,
            val descricao: String,
        )

        fun getDepartamento(callback: GetDepartamentoCallback, id: Int) {
            val jsonString = JsonHelper.ReadJSONFromAssets(context, "departamento.json")
            val response: JSONObject = JSONObject(jsonString)
            if (response.has("departamentos")) {
                val jsonArray = response.getJSONArray("departamentos")
                for (i in 0 until jsonArray.length()) {
                    val dataObject = jsonArray.getJSONObject(i)
                    if (id == dataObject.optInt("departamentoId")) {
                            val departamento = Departamento(
                                deparmentoId = dataObject.optInt("deparmentoId"),
                                departamentoNome = dataObject.optString("departamentoNome"),
                                dataCriacao = dataObject.optString("dataCriacao"),
                                descricao = dataObject.optString("descricao"),
                        )


                        callback.onSuccess(departamento)
                        }
                    }
                    } else {
                        val errorMessage = "Resposta JSON Inválida"
                        callback.onFailure(errorMessage)
                    }

        }

        interface GetDepartamentoCallback {
            fun onSuccess(departamento: Departamento)
            fun onFailure(errorMessage: String)
        }
    }