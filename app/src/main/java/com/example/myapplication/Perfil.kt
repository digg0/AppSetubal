package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.AdapterServicos
import com.example.myapplication.model.Servicos
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// ====================== Perfil ======================
class Perfil : Fragment() {

    private lateinit var recyclerViewServicos: RecyclerView
    private lateinit var adapterServicos: AdapterServicos
    private val listaServicos: MutableList<Servicos> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        recyclerViewServicos = view.findViewById(R.id.recyclerViewServicos)
        recyclerViewServicos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewServicos.setHasFixedSize(true)

        adapterServicos = AdapterServicos(requireContext(), listaServicos)
        recyclerViewServicos.adapter = adapterServicos

        observarServicosFirestore()

        return view
    }

    private fun observarServicosFirestore() {
        listenerRegistration = db.collection("GerServicos")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Perfil", "Erro ao carregar serviços", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listaServicos.clear()
                    for (doc in snapshots) {
                        val servico = doc.toObject(Servicos::class.java)
                        // Preenche o documentId no objeto
                        val servicoComId = servico.copy(documentId = doc.id)
                        listaServicos.add(servicoComId)
                    }
                    adapterServicos.notifyDataSetChanged()
                }
            }
    }

    // --------- Função adicional para carregar serviços diretamente ---------
    private fun carregarServicos() {
        db.collection("GerServicos")
            .get()
            .addOnSuccessListener { result ->
                listaServicos.clear()
                for (document in result.documents) {
                    val servico = Servicos(
                        documentId = document.id,
                        nome = document.getString("nome") ?: "",
                        descricao = document.getString("descricao") ?: "",
                        preco = document.getString("preco") ?: "",
                        duracao = document.getString("duracao") ?: ""
                    )
                    listaServicos.add(servico)
                }
                adapterServicos.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao carregar serviços: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove() // remove listener para evitar memory leak
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botaoNovoServico: Button = view.findViewById(R.id.button_NovoServico)
        botaoNovoServico.setOnClickListener {
            val dialog = DialogServicos()
            dialog.show(parentFragmentManager, "DialogServicos")
        }
    }
}
