package com.example.myapplication

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentConfigsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class AjusteHorarios : Fragment() {

    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = "" // guarda a data selecionada
    private var _binding: FragmentConfigsBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkboxes: List<android.widget.CheckBox>
    private lateinit var horarios: List<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            voltarTelaLogin()
        }



        // Atualizar banco (apagar antigos + criar próximos)
        atualizarDias()

        // Lista de checkboxes
        checkboxes = listOf(
            binding.checkbox1, binding.checkbox2, binding.checkbox3, binding.checkbox4,
            binding.checkbox5, binding.checkbox6, binding.checkbox7, binding.checkbox8,
            binding.checkbox9, binding.checkbox10, binding.checkbox11, binding.checkbox12,
            binding.checkbox13, binding.checkbox14, binding.checkbox15, binding.checkbox16,
            binding.checkbox17, binding.checkbox18, binding.checkbox19, binding.checkbox20,
            binding.checkbox21
        )

        // Horários correspondentes
        horarios = listOf(
            "08:00", "08:30", "09:00", "09:30",
            "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30",
            "16:00", "16:30", "17:00", "17:30",
            "18:00"
        )

        // Define os textos nos checkboxes
        for ((index, cb) in checkboxes.withIndex()) {
            cb.text = horarios[index]
        }

        val datePicker = binding.ContainerAgendamentosDoDia

        // Configurar DatePicker para o dia atual
        val hoje = java.util.Calendar.getInstance()
        val diaAtual = hoje.get(Calendar.DAY_OF_MONTH)
        val mesAtual = hoje.get(Calendar.MONTH)
        val anoAtual = hoje.get(Calendar.YEAR)

        datePicker.init(anoAtual, mesAtual, diaAtual) { _, year, monthOfYear, dayOfMonth ->
            onDataSelecionada(year, monthOfYear, dayOfMonth)
        }

        // Carregar horários do dia atual ao abrir a tela
        onDataSelecionada(anoAtual, mesAtual, diaAtual)

        // Permitir marcar/desmarcar sem salvar automático
        for (checkbox in checkboxes) {
            checkbox.setOnCheckedChangeListener { _, _ -> }
        }

        // Botão para salvar horários
        binding.buttonSalvarHorarios.setOnClickListener {
            if (data.isEmpty()) {
                Toast.makeText(requireContext(), "Selecione uma data primeiro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val horariosSelecionados = checkboxes.mapIndexed { idx, cb ->
                val status = when {
                    !cb.isEnabled -> "ocupado"
                    cb.isChecked -> "disponivel"
                    else -> "indisponivel"
                }
                mapOf(
                    "hora" to horarios[idx],
                    "status" to status
                )
            }

            salvarHorariosDisponiveis(horariosSelecionados, data)
            Toast.makeText(requireContext(), "Horários salvos com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDataSelecionada(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val dia = dayOfMonth.toString().padStart(2, '0')
        val mes = (monthOfYear + 1).toString().padStart(2, '0')
        data = "$year-$mes-$dia" // chave do documento

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("horariosDisponiveis").document(data)

        ref.get().addOnSuccessListener { document ->
            preencherCheckboxes(document)
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preencherCheckboxes(document: com.google.firebase.firestore.DocumentSnapshot) {
        if (document.exists()) {
            val raw = document.get("horarios")
            val horariosSalvos = (raw as? List<*>)?.mapNotNull {
                it as? Map<String, Any>
            } ?: emptyList()

            for ((index, cb) in checkboxes.withIndex()) {
                val hora = horarios[index]
                val horarioData = horariosSalvos.find { it["hora"] == hora }

                if (horarioData != null) {
                    when (horarioData["status"]) {
                        "disponivel" -> {
                            cb.isChecked = true
                            cb.isEnabled = true
                        }
                        "indisponivel" -> {
                            cb.isChecked = false
                            cb.isEnabled = true
                        }
                        "ocupado" -> {
                            cb.isChecked = false
                            cb.isEnabled = false
                        }
                        else -> {
                            cb.isChecked = false
                            cb.isEnabled = true
                        }
                    }
                } else {
                    cb.isChecked = false
                    cb.isEnabled = true
                }
            }
        } else {
            for (cb in checkboxes) {
                cb.isChecked = false
                cb.isEnabled = true
            }
        }
    }


    private fun salvarHorariosDisponiveis(
        horariosSelecionados: List<Map<String, Any>>,
        data: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("horariosDisponiveis").document(data)

        ref.get().addOnSuccessListener { document ->
            val horariosAtuais = (document.get("horarios") as? List<*>)?.mapNotNull {
                it as? Map<String, Any>
            } ?: emptyList()

            val mergedHorarios = horariosSelecionados.map { novo ->
                val hora = novo["hora"]
                val atual = horariosAtuais.find { it["hora"] == hora }
                if (atual?.get("status") == "ocupado") {
                    mapOf("hora" to hora, "status" to "ocupado")
                } else {
                    novo
                }
            }

            val horariosDisponiveis = hashMapOf(
                "horarios" to mergedHorarios,
                "data" to data
            )

            ref.set(horariosDisponiveis)
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun voltarTelaLogin() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun atualizarDias() {
        val db = FirebaseFirestore.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val limitePassado = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, -30)
        }
        val limitePassadoStr = dateFormat.format(limitePassado.time)

        // Apagar últimos 30 dias
        db.collection("horariosDisponiveis")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val dataDoc = doc.getString("data") ?: continue
                    if (dataDoc < limitePassadoStr) {
                        println("DEBUG >>> apagando dia antigo: $dataDoc")
                        doc.reference.delete()
                    }
                }
            }


        val horariosPadrao = listOf(
            "08:00", "08:30", "09:00", "09:30",
            "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30",
            "16:00", "16:30", "17:00", "17:30",
            "18:00"
        ).map { hora -> mapOf("hora" to hora, "status" to "indisponivel") }

        // Criar próximos 20 dias se não existirem
        for (i in 0..20) {
            val dia = java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.DAY_OF_YEAR, i)
            }
            val dataStr = dateFormat.format(dia.time)

            val ref = db.collection("horariosDisponiveis").document(dataStr)
            ref.get().addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    val novoDia = hashMapOf(
                        "data" to dataStr,
                        "horarios" to horariosPadrao
                    )
                    ref.set(novoDia).addOnSuccessListener {
                        println("DEBUG >>> criado novo dia: $dataStr")
                    }
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
