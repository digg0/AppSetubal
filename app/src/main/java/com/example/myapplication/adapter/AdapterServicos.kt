package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Servicos

class AdapterServicos(private val context: Context, private  val servicos: MutableList<Servicos>): RecyclerView.Adapter<AdapterServicos.ServicoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicoViewHolder {
        val servicoLista = LayoutInflater.from(context).inflate(R.layout.layout_servicos, parent, false)
        val holder = ServicoViewHolder(servicoLista)
        return holder

    }

    override fun getItemCount(): Int = servicos.size

    override fun onBindViewHolder(holder: ServicoViewHolder, position: Int) {
        val servico = servicos[position]  // define o item atual

        holder.nome.text = servico.nome
        holder.descricao.text = servico.descricao
        holder.preco.text = "R$ ${servico.preco}"
        holder.duracao.text = "${servico.duracao} min"
    }
    inner class ServicoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.findViewById<TextView >(R.id.TituloServico)
        val descricao = itemView.findViewById<TextView>(R.id.tvDescricao)
        val preco = itemView.findViewById<TextView>(R.id.tvPreco)
        val duracao = itemView.findViewById<TextView>(R.id.tvTempo)

    }

}