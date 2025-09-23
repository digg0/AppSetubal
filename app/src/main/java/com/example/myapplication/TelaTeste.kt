package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TelaTeste : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.telateste)

        val botaoSair: Button = findViewById(R.id.botaotestesair)
        botaoSair.setOnClickListener {
            // Deslogar o usuário do Firebase
            auth.signOut()

            // Voltar para a tela de login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // fecha a tela de teste
        }
    }
}
