package com.example.myapplication

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentAgendaBinding

import com.google.firebase.firestore.FirebaseFirestore

class Agenda : Fragment() { // nome ajustado

    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DatePicker para selecionar data
        binding.ContainerAgendamentosDoDia.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dia = dayOfMonth.toString().padStart(2, '0')
            val mes = (monthOfYear + 1).toString().padStart(2, '0')
            data = "$year-$mes-$dia"

            // Buscar os horários no Firebase
            carregarHorarios(data)
        }
    }

    private fun carregarHorarios(data: String) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("horariosDisponiveis").document(data)

        ref.get().addOnSuccessListener { document ->
            val grid = binding.GridHorarios
            grid.removeAllViews() // limpa os horários anteriores

            if (document.exists()) {
                val horariosSalvos = document.get("horarios") as? List<String> ?: emptyList()

                for (horario in horariosSalvos) {
                    val textView = TextView(requireContext()).apply {
                        text = horario
                        textSize = 16f
                        setPadding(80, 24, 80, 24)
                        setBackgroundResource(R.drawable.bg_horario)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
                        gravity = Gravity.CENTER
                    }
                    grid.addView(textView)
                }
            }
            // Se não houver horários → grid fica vazio dentro do container
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Erro ao carregar horários.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
