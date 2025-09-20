package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainFuncBinding
import org.jetbrains.annotations.Async.Schedule



class BarberHub : AppCompatActivity() {

    private lateinit var  binding : ActivityMainFuncBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFuncBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFrament(Home())

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_home -> replaceFrament(Home())
                R.id.bottom_schedule -> replaceFrament(Agenda())
                R.id.bottom_config -> replaceFrament(AjusteHorarios())
                R.id.bottom_profile -> replaceFrament(Perfil())
                R.id.bottom_config -> replaceFrament(Configs())




                else ->{

                }
            }
            true
        }




    }

    private fun replaceFrament(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}

