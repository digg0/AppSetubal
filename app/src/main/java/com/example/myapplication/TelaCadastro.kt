package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import com.example.myapplication.databinding.CadastroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.StructuredQuery




class TelaCadastro : ComponentActivity() {


    private val database = FirebaseFirestore.getInstance()
    private lateinit var binding: CadastroBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CadastroBinding.inflate(layoutInflater)

        setContentView(binding.root)




        binding.botaoCadastrara.setOnClickListener{ view ->

            val email=binding.cadEmail.text.toString()
            val senha= binding.cadSenha.text.toString()
            val nome = binding.cadNome.text.toString()
            val contato = binding.cadContato.text.toString()

            val usuariosMap = hashMapOf(
                "nome" to nome,
                "contato" to contato,
                "email" to email,
                "senha" to senha
            )

            if(email.isEmpty()|| senha.isEmpty() || nome.isEmpty() || contato.isEmpty() ){
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()

            }else{
                auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener{ cadastro ->

                    if (cadastro.isSuccessful){
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        binding.cadEmail.setText("")
                        binding.cadSenha.setText("")
                        binding.cadNome.setText("")
                        binding.cadContato.setText("")
                        database.collection("Usuarios").document(nome).set(usuariosMap)
                        FirebaseAuth.getInstance().signOut()
                        voltarTelaLogin()
                    }
                }.addOnFailureListener{exceptions ->

                    val mensagemErro = when(exceptions){
                        is FirebaseAuthWeakPasswordException -> "Digite uma Senha com no minimo 6 Caracteres!"
                        is FirebaseAuthInvalidCredentialsException -> "Digite um Email valido!"
                        is FirebaseAuthUserCollisionException -> "Essa conta ja foi cadastrada!"
                        is FirebaseNetworkException -> "Sem Conexao com Internet!"
                        else -> "Erro ao cadastrar usuario!"
                    }
                    Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT).show()


                }
            }
        }






    }
    private fun voltarTelaLogin(){
        val intent = Intent (this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }





    }







