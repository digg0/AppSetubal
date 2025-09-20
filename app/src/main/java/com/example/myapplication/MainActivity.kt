package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplication.databinding.Tela1Binding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private lateinit var binding: Tela1Binding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Tela1Binding.inflate(layoutInflater)
        setContentView(binding.root)



//        val botaoFunc = findViewById<Button>(R.id.botao_func)
//
//        botaoFunc.setOnClickListener{
//            val intent = Intent(this, SecondActivity::class.java)
//            startActivity(intent)
//        }

        binding.botaoEntrar.setOnClickListener{
            val email = binding.campoUsuario.text.toString()
            val senha = binding.campoSenha.text.toString()

            if(email.isEmpty() || senha.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener{ autenticacao ->
                    if (autenticacao.isSuccessful){
                        navegarTelaPrincipal()

                    }

                }.addOnFailureListener(){
                    Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
                }

            }
        }

       binding.botaoCadastro.setOnClickListener{
           val intent = Intent(this, TelaCadastro::class.java)
           startActivity(intent)
       }



    }
    private fun navegarTelaPrincipal(){
        val intent = Intent (this, BarberHub::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val usuarioAtual= FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null){
            navegarTelaPrincipal()
        }
    }
}