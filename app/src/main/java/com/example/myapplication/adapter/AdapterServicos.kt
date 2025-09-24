package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Servicos
import com.google.firebase.firestore.FirebaseFirestore

class AdapterServicos(
    private val context: Context,
    private val servicos: MutableList<Servicos>
) : RecyclerView.Adapter<AdapterServicos.ServicoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicoViewHolder {
        val servicoLista = LayoutInflater.from(context)
            .inflate(R.layout.layout_servicos, parent, false)
        return ServicoViewHolder(servicoLista)
    }

    override fun getItemCount(): Int = servicos.size

    override fun onBindViewHolder(holder: ServicoViewHolder, position: Int) {
        val servico = servicos[position]

        holder.nome.text = servico.nome
        holder.descricao.text = servico.descricao
        holder.preco.text = "R$ ${servico.preco}"
        holder.duracao.text = "${servico.duracao} min"


        holder.botaoDeletar.setOnClickListener {


            val dialog = android.app.Dialog(context)
            dialog.setContentView(R.layout.layout_dialog_aviso)
            dialog.setCancelable(true)


            val botaoSim: Button = dialog.findViewById(R.id.botaoConfirmar)
            val botaoNao: Button = dialog.findViewById(R.id.botaoCancelar)
            val titulo: TextView = dialog.findViewById(R.id.textViewTitulo)
            val mensagem: TextView = dialog.findViewById(R.id.textViewMensagem)


            titulo.text = "Confirmação"
            mensagem.text = "Tem certeza que deseja deletar este serviço?"

            botaoSim.setOnClickListener {
                deletarServicoFirestore(servico.documentId)
                dialog.dismiss()
            }

            botaoNao.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun deletarServicoFirestore(documentId: String) {
        if (documentId.isEmpty()) {
            Toast.makeText(context, "ID do serviço não definido", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("GerServicos").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Serviço deletado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao deletar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class ServicoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.TituloServico)
        val descricao: TextView = itemView.findViewById(R.id.tvDescricao)
        val preco: TextView = itemView.findViewById(R.id.tvPreco)
        val duracao: TextView = itemView.findViewById(R.id.tvTempo)
        val botaoDeletar: Button = itemView.findViewById(R.id.buttonDeletar)
    }
}
