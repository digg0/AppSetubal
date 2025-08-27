package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ComponentActivity
import com.example.myapplication.databinding.TelatesteBinding
import com.google.firebase.auth.FirebaseAuth


class TelaTeste : androidx.activity.ComponentActivity() {

    private lateinit var binding: TelatesteBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = TelatesteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.botaoDeslogar.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, MainActivity::class.java)
            startActivity(voltarTelaLogin)
            finish()


        }
    }
}