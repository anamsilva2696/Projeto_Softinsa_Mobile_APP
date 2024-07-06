package com.example.projeto_softinsa_app.lvadapador
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.negocio_softinsa_mobile_app.Des_tudo.imagem_investimento
import com.example.projeto_softinsa_app.R
import com.example.projeto_softinsa_app.API.Perfil


class Lvadapador_investimento(private val context: Context, private val InvestimentoList: List<imagem_investimento>) : BaseAdapter()  {

    override fun  getViewTypeCount(): Int {
        if(count > 0){
            return count;
        }else {
            return super.getViewTypeCount();

        } }
    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return InvestimentoList.size
    }

    override fun getItem(position: Int): Any {
        return InvestimentoList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.lv_item_oport, null, true)

            holder.tvTituloOportunidade = convertView!!.findViewById(R.id.Titulo_oportunidade) as TextView
            holder.tvNomeUtilizador = convertView!!.findViewById(R.id.Utilizador_Oportunidade) as TextView
            holder.tvEstadoOportunidade = convertView!!.findViewById(R.id.Estado_Oportunidade) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        val investimento = InvestimentoList[position]
        holder.tvTituloOportunidade?.text = investimento.descricao_img_investimento

        val userId = investimento.userId_img_investimento
        val User = Perfil(context, null)
        if (userId != null) {
            User.getUser(userId, object : Perfil.GetUserCallback{
                override fun onSuccess(user: Perfil.User) {

                    holder.tvNomeUtilizador?.text = user.primeiroNome + " " + user.ultimoNome
                }

                override fun onFailure(errorMessage: String) {
                    holder.tvNomeUtilizador?.text  = "Error: $errorMessage"
                }
            })
        } else {
            holder.tvNomeUtilizador?.text = "O user não existe"
        }

        return convertView!!
    }

    private inner class ViewHolder {

        var tvTituloOportunidade: TextView? = null
        var tvNomeUtilizador: TextView? = null
        var tvEstadoOportunidade: TextView? = null
    }
}