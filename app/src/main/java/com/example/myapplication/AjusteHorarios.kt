package com.example.myapplication

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.myapplication.databinding.HomefuncionarioBinding
import com.example.myapplication.databinding.TelatesteBinding
import com.google.firebase.firestore.FirebaseFirestore

class AjusteHorarios : ComponentActivity() {

    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private lateinit var binding: HomefuncionarioBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomefuncionarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Lista de horario e de checkbox que serão usadas no FOR la em baixo. ->
        val checkboxes = listOf(binding.checkbox1, binding.checkbox2, binding.checkbox3)
        val horarios = listOf("10:30", "11:00", "11:30")


        val datePicker = binding.datePicker //Faz com que o calendario possa ser utilizado como uma informação.
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            //Transforma a data selecionada em String, padStart adiciona um 0 a esquerda pra manter o padrão de 2 digitos.
            val dia = dayOfMonth.toString().padStart(2, '0')
            val mes = (monthOfYear + 1).toString().padStart(2, '0')

            // Passando a data em formato limpo pra ser usada como chave. ->
            data = "$year-$mes-$dia"


            val db = FirebaseFirestore.getInstance()
            val ref = db.collection("horariosDisponiveis").document(data)

            //Resgata as informações do firebase e converte pra String. ->
            ref.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val horariosSalvos = document.get("horarios") as? List<String> ?: emptyList()

                    // marca e desmarca o checkbox de acordo com as informações que vieram do firebase ->
                    for (checkbox in checkboxes) {
                        checkbox.isChecked = horariosSalvos.contains(checkbox.text.toString())

                    }
                }
            }


        }



        for ((index, checkbox) in checkboxes.withIndex()) {

            checkbox.text = horarios[index] //Define um horario pra cada chekbox pegando as variaveis la de cima

            checkbox.setOnCheckedChangeListener { buttonView, isCheccked ->
                val selecionados = checkboxes.filter { it.isChecked }.map { it.text.toString() } //pega o texto dos checkbox que estão selecionados

                //verificação caso data não seja selecionada
                if (data.isEmpty()) {
                    Toast.makeText(this, "Selecione uma data primeiro", Toast.LENGTH_SHORT).show()
                    buttonView.isChecked = false
                    return@setOnCheckedChangeListener

                }

                salvarHorariosDisponiveis(selecionados, data)
            }
        }


    }

    private fun salvarHorariosDisponiveis(horariosSelecionados: List<String>, data: String) {
        val database = FirebaseFirestore.getInstance()

        val horariosDisponiveis = hashMapOf(
            "horarios" to horariosSelecionados,
            "data" to data
        )


        database.collection("horariosDisponiveis").document(data).set(horariosDisponiveis)

            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}









