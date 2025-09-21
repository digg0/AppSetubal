package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class DialogServicos : DialogFragment() {

    private lateinit var editNome: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editPreco: EditText
    private lateinit var editDuracao: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_editar_servico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa os EditTexts
        editNome = view.findViewById(R.id.EditText_NomeBarbearia)
        editDescricao = view.findViewById(R.id.EditText_DescriçãoServico)
        editPreco = view.findViewById(R.id.EditText_PrecoServico)
        editDuracao = view.findViewById(R.id.EditText_DuracaoServico)

        val botaoSalvar: Button = view.findViewById(R.id.button_SalvarServico)
        val botaoCancelar: Button = view.findViewById(R.id.button_Cancelar)

        botaoSalvar.setOnClickListener {
            salvarServicoFirestore()
        }

        botaoCancelar.setOnClickListener {
            dismiss()
        }
    }

    private fun salvarServicoFirestore() {
        val nome = editNome.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val preco = editPreco.text.toString().trim()
        val duracao = editDuracao.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || preco.isEmpty() || duracao.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (preco.toIntOrNull() == null || duracao.toIntOrNull() == null) {
            Toast.makeText(context, "Preencha preço e duração com números válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()

        // Cria mapa de dados, no mesmo estilo do AjusteHorarios
        val servicoData = hashMapOf(
            "nome" to nome,
            "descricao" to descricao,
            "preco" to preco,
            "duracao" to duracao
        )

        // Salva um novo documento com ID automático
        db.collection("GerServicos")
            .add(servicoData)
            .addOnSuccessListener {
                Toast.makeText(context, "Serviço salvo com sucesso!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}